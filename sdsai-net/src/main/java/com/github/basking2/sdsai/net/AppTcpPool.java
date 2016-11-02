package com.github.basking2.sdsai.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;

/**
 * A {@link TcpPool} that connects with an app prefix.
 */
public class AppTcpPool extends TcpPool {
    public AppTcpPool(final String listening, final String schema, TcpPool.SocketHandler ... socketHandlers) throws IOException {
        super(
                listening,
                id -> {
                    final URI uri = URI.create(id);

                    if (!uri.getScheme().equals(schema)) {
                        throw new IllegalArgumentException("Scheme must be \"" + schema +"\".");
                    }

                    return new InetSocketAddress(uri.getHost(), uri.getPort());
                },
                socketHandlers
        );
    }
}
