package org.sdsai.paxos;

import java.io.Serializable;
import java.util.Comparator;

/**
 */
public class Promise<V> implements Serializable {
    public static final Promise DENIED = new Promise();

    private final Long n;
    private final Proposal<V> proposal;
    private final boolean denied;

    public Promise(final Long n, final Proposal<V> proposal) {
        this.n = n;
        this.proposal = proposal;
        this.denied = false;
    }

    public Promise(final Long n) {
        this(n, null);
    }
    public Promise() {
        n = 0L;
        proposal = null;
        denied = true;
    }

    public Long getN() {
        return n;
    }

    public Proposal<V> getProposal() {
        return proposal;
    }

    public static class PromiseComparator implements Comparator<Promise>
    {
        @Override
        public int compare(final Promise o1, final Promise o2) {
            if (o1 == o2) {
                return 0;
            }

            if (o1 == null) {
                return -1;
            }

            if (o2 == null) {
                return 1;
            }

            if (o1.getN() < o2.getN()) {
                return -1;
            }

            if (o1.getN() > o2.getN()) {
                return 1;
            }

            return 0;
        }
    }

    /**
     * Is this promise message a denial?
     *
     * If true, then there is no value for {@link #getN()} and null is returned by {@link #getProposal()}.
     *
     * @return
     */
    public boolean isDenied() {
        return denied;
    }

    /**
     * Return a proposal with the number set to {@link #getProposal()}'s value, if not null or {@link #getN()}.
     * @param value The value for the proposal if there is no existing proposal.
     * @return A proposal to satisfy this promise.
     */
    public Proposal<V> toProposal(final V value) {
        if (proposal != null) {
            return new Proposal(n, proposal.getValue());
        } else {
            return new Proposal<V>(n, value);
        }
    }
}
