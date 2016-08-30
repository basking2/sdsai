package com.github.basking2.sdsai.paxos;

import java.util.List;

/**
 */
public abstract class Acceptor<V> {
    private Proposal<V> lastAccepted = null;
    private Long highestPrepared = 0L;
    private final List<Learner<V>> learners;

    public Acceptor(List<Learner<V>> learners) throws Exception {
        this.learners = learners;
        this.lastAccepted = loadImpl();
    }

    public Promise<V> prepare(final Long n) {

        // If we have already promised to not accept something higher.
        // We may have already accepted this, too!
        if (n <= highestPrepared) {
            return Promise.DENIED;
        }

        // Promise not to accept anything.
        highestPrepared = n;

        // Otherwise, return a promise with a copy of the last proposal we did accept (or null).
        return new Promise(lastAccepted.getN(), lastAccepted);
    }

    public void accept(final Proposal<V> proposal) throws Exception {

        // If we promised N and the user will accept the value, store and report it.
        if (highestPrepared <= proposal.getN() && acceptImpl(proposal)) {

            // Accept the value.
            lastAccepted = proposal;

            storeImpl(proposal);

            // Notify learners.
            for (final Learner l : learners) {
                l.learn(lastAccepted);
            }
        }
    }

    /**
     * Store the latest proposal.
     * @param proposal
     * @throws Exception
     */
    protected abstract void storeImpl(final Proposal<V> proposal) throws Exception;

    /**
     * Load the latest proposal.
     * @return
     * @throws Exception
     */
    protected abstract Proposal<V> loadImpl() throws Exception;

    /**
     * @param proposal The proposal to accept.
     * @return True if the Acceptor should accept the proposal. False otherwise.
     */
    protected boolean acceptImpl(final Proposal<V> proposal) {
        return true;
    }
}
