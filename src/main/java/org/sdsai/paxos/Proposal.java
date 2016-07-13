package org.sdsai.paxos;

import java.io.Serializable;

/**
 */
public class Proposal<V> implements Serializable {
    final private Long n;
    final private V value;

    public Proposal(final Long n, final V value) {
        this.n = n;
        this.value = value;
    }

    public V getValue() {
        return value;
    }

    public Long getN() {
        return n;
    }
}
