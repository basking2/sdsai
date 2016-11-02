package com.github.basking2.sdsai.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Establish TCP connections to hosts using an ID. In-bound connections handshake
 * with the server by sending the remote system's ID. Once a connection is
 * established it is sent to {@link SocketHandler}s for management. Once a connection is
 * established, this object does not manage the {@link SocketChannel} in any way.
 *
 * This class is not completely thread safe as it manipulates a selector's key sets.
 */
public class TcpPool implements AutoCloseable {

    /**
     * Log.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TcpPool.class);

    /**
     * How to convert a node ID into a network address.
     */
    private IdTranslator idTranslator;

    private List<SocketHandler> socketHandlers;

    /**
     * A limit on the length of an ID sent during a handshake.
     */
    public static final int ID_LIMIT = 1000;

    private ServerSocketChannel serverSocketChannel;
    private String nodeId;
    private Selector selector;

    public TcpPool(final String listen) throws IOException {
        this(listen, new DefaultIdTranslator());
    }

    public TcpPool(final String listen, final IdTranslator idTranslator, final SocketHandler ... socketHandlerArr)throws IOException {
        this.idTranslator = idTranslator;
        this.socketHandlers = new ArrayList<>();
        for (final SocketHandler socketHandler : socketHandlerArr) {
            this.socketHandlers.add(socketHandler);
        }
        this.nodeId = listen;
        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open().bind(idTranslator.translate(listen));
        this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        this.serverSocketChannel.configureBlocking(false);
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * Allow the TcpPool to do work on its sockets.
     */
    public void runOnce(final long timeout) throws IOException {
        final int socketCount = selector.select(timeout);

        processSelect(socketCount);
    }

    public void runOnceNow() throws IOException {
        final int socketCount = selector.selectNow();

        processSelect(socketCount);
    }

    private void processSelect(int socketCount) throws IOException {
        if (socketCount > 0) {
            for (
                final Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                keys.hasNext();
            ) {
                final SelectionKey key = keys.next();

                if (!key.isValid()) {
                    // Nop.
                }
                else if (key.isConnectable()) {
                    final SocketChannel sock = (SocketChannel)key.channel();
                    final TcpPoolAttachment tcpPoolAttachment = (TcpPoolAttachment) key.attachment();

                    sock.finishConnect();
                    tcpPoolAttachment.handshake = ByteBuffer.wrap(nodeId.getBytes());

                    // Register for write.
                    sock.register(selector, SelectionKey.OP_WRITE, tcpPoolAttachment);
                }
                else if (key.isWritable()) {
                    final SocketChannel sock = (SocketChannel)key.channel();
                    final TcpPoolAttachment tcpPoolAttachment = (TcpPoolAttachment) key.attachment();

                    if (tcpPoolAttachment.len.position() < tcpPoolAttachment.len.limit()) {
                        sock.write(tcpPoolAttachment.len);
                    }

                    if (tcpPoolAttachment.len.position() >= tcpPoolAttachment.len.limit()) {
                        // If writable, we're sending the handshake.
                        sock.write(tcpPoolAttachment.handshake);

                        // If we've written to our limit, cancel the write key and register as ready.
                        if (tcpPoolAttachment.isHandshakeDone()) {
                            passOwnershipToListeners(key, tcpPoolAttachment, sock);
                        }
                    }
                }
                else if (key.isAcceptable()) {
                    final ServerSocketChannel serverChan = (ServerSocketChannel)key.channel();

                    final SocketChannel chan = serverChan.accept();

                    if (chan != null) {
                        chan.setOption(StandardSocketOptions.TCP_NODELAY, true);
                        chan.configureBlocking(false);
                        chan.register(selector, SelectionKey.OP_READ, new TcpPoolAttachment("[no id yet]"));
                    }
                }
                else if (key.isReadable()) {
                    final SocketChannel sock = (SocketChannel)key.channel();
                    final TcpPoolAttachment tcpPoolAttachment = (TcpPoolAttachment) key.attachment();

                    // If handshake is null, we didn't get a handshake length yet.
                    if (tcpPoolAttachment.handshake == null) {
                        sock.read(tcpPoolAttachment.len);

                        if (tcpPoolAttachment.len.position() >= 4) {

                            int len = tcpPoolAttachment.len.getInt(0);

                            if (len > ID_LIMIT) {
                                key.cancel();
                                sock.close();
                                throw new IOException("ID Limit exceeded: "+len +" > "+ID_LIMIT);
                            }

                            tcpPoolAttachment.handshake = ByteBuffer.allocate(len);
                        }
                    }

                    if (tcpPoolAttachment.handshake != null) {
                        sock.read(tcpPoolAttachment.handshake);

                        // If we've received up to the limit of our handshake buffer, take action.
                        if (tcpPoolAttachment.isHandshakeDone()) {
                            tcpPoolAttachment.id = new String(tcpPoolAttachment.handshake.array());
                            passOwnershipToListeners(key, tcpPoolAttachment, sock);
                        }
                    }
                }
                else {
                    throw new IOException("Unexpected socket state. Interest Ops:" + key.interestOps());
                }

                keys.remove();
            }
        }

    }

    @Override
    public void close() throws Exception {
        for (SelectionKey k : selector.keys()) {
            try {
                k.channel().close();
            } catch (IOException e) {
                LOG.warn("Failed to close channel.", e);
            }
        }

        selector.close();
    }

    public Future<SocketChannel> connect(final String id) throws IOException {
        if (id.equals(nodeId)) {
            throw new IOException("Node may not connect to itself: "+id);
        }

        // Search if we already have a connection we're working on.
        for (final SelectionKey key : selector.keys()) {
            if (key.attachment() instanceof TcpPoolAttachment) {
                final TcpPoolAttachment tcpPoolAttachment = (TcpPoolAttachment) key.attachment();

                if (id.equals(tcpPoolAttachment.id)) {
                    return tcpPoolAttachment.future;
                }
            }
        }

        final SocketAddress addr = idTranslator.translate(id);
        final SocketChannel chan = SocketChannel.open();

        chan.setOption(StandardSocketOptions.TCP_NODELAY, true);
        chan.configureBlocking(false);
        final TcpPoolAttachment attachment = new TcpPoolAttachment(id);
        chan.register(selector, SelectionKey.OP_CONNECT, attachment);
        chan.connect(addr);

        return attachment.future;
    }

    public static class DefaultIdTranslator implements IdTranslator {
        @Override public SocketAddress translate(String id) {
            final URI uri = URI.create(id);

            if (!uri.getScheme().equals("tcp")) {
                throw new IllegalArgumentException("Scheme must be \"tcp\".");
            }

            return new InetSocketAddress(uri.getHost(), uri.getPort());
        }
    }

    private static class TcpPoolAttachment {
        public String id;
        public ByteBuffer len;
        public ByteBuffer handshake;
        public CompletableFuture<SocketChannel> future;

        public TcpPoolAttachment(String id) {
            this.id = id;
            this.len = ByteBuffer.allocate(4);
            this.len.putInt(0, id.getBytes().length);
            this.future = new CompletableFuture<>();
        }

        public boolean isHandshakeDone() {
            return (handshake != null) && (handshake.position() >= handshake.limit());
        }
    }

    public void addSocketHandler(final SocketHandler socketHandler) {
        this.socketHandlers.add(socketHandler);
    }

    private void passOwnershipToListeners(final SelectionKey key, final TcpPoolAttachment attachment, final SocketChannel chan) {
        LOG.debug("Passing ownership of {}.", attachment.id);
        key.cancel();

        if (attachment.future != null) {
            attachment.future.complete(chan);
        }

        for (final SocketHandler socketHandler: socketHandlers) {
            socketHandler.handleNewSocket(attachment.id, chan);
        }
    }

    @FunctionalInterface
    public interface SocketHandler {
        void handleNewSocket(String id, SocketChannel socketChannel);
    }

    @FunctionalInterface
    public interface IdTranslator {
        SocketAddress translate(String id);
    }

}
