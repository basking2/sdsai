package org.sdsai.paxos;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class Learner<V> {
    private final int quorum;

    private final Map<Proposal<V>, Long> values = new HashMap<Proposal<V>, Long>();

    // The eventual winner.
    Proposal<V> winner = null;

    public Learner(final int quorum) {
        this.quorum = quorum;
    }

    /**
     * @param p
     * @return True when a value is set.
     */
    public boolean learn(final Proposal<V> p) {
        // Get the votes count.
        Long votes = values.get(p.getValue());
        if (votes == null) {
            votes = 0L;
        }

        // Increment it.
        values.put(p, votes+1);

        // Check it.
        return (votes >= quorum);
    }

    /**
     * @return Null or the value in the case of a winning decision.
     */
    public V getValue() {
        return winner == null? null : winner.getValue();
    }
}
