package org.sdsai.paxos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 */
public class Proposer<V> {
    private final int quorum;

    private final List<Acceptor<V>> acceptors;

    public Proposer(final int quorum, List<Acceptor<V>> acceptors) {
        this.quorum = quorum;
        this.acceptors = acceptors;
    }

    /**
     * Choose a value by gathering promises, and perhaps issuing a proposal.
     *
     * @param value The value to propose if one has not already been chosen.
     * @return The accepted proposal. The value may be different than what we submit.
     */
    public Proposal<V> choose(final V value) throws Exception {

        // Phase 1 - Propose and get a promise for a value N.
        Promise<V> promise = null;
        while (promise == null) {
            final Long n = chooseNImpl();
            promise = phase1(n);
        }

        // A proposal was already selected.
        if (promise.getProposal() != null) {
            return promise.getProposal();
        }
        else {
            final Proposal<V> proposal = new Proposal<V>(promise.getN(), value);
            phase2(proposal);
            return proposal;
        }
    }

    /**
     * Secure promises or discover a chosen value.
     *
     * @return The promise for proposed value N or NULL if we have no quorum.
     */
    private Promise<V> phase1(final Long n) {

        final ArrayList<Promise<V>> promises = new ArrayList<Promise<V>>(acceptors.size());

        for (final Acceptor<V> acceptor : acceptors) {
            final Promise<V> p = acceptor.prepare(n);

            // If the promise is not null (timed out) and not denied, collect it.
            if (p != null && !p.isDenied()) {
                promises.add(p);
            }
        }

        if (promises.size() < quorum) {
            return null;
        }

        // Pick the latest chosen value, if any. This may be null.
        final Proposal<V> latestProposal = chooseLatestProposal(promises);

        return new Promise(n, latestProposal);
    }

    private Proposal<V> chooseLatestProposal(final List<Promise<V>> promises) {
        Proposal<V> latestProposal = null;

        for (final Promise<V> prevPromise : promises) {
            final Proposal<V> prevProposal = prevPromise.getProposal();
            if (latestProposal == null) {
                // If latest is null, just copy the previous one. No worries.
                latestProposal = prevProposal;
            } else if (prevProposal != null && latestProposal.getN() < prevProposal.getN()) {
                latestProposal = prevProposal;
            }
        }

        return latestProposal;
    }

    private void phase2(final Proposal<V> proposal) throws Exception {
        for (final Acceptor<V> acceptor : acceptors) {
            acceptor.accept(proposal);
        }
    }

    protected Long chooseNImpl() {
        return System.currentTimeMillis();
    }
}
