package com.github.basking2.sdsai;

public class Interval<K extends Comparable<K>> {
    private K min;
    private K max;

    public Interval(final K min, final K max) {
        if (min.compareTo(max)>0) {
            throw new IllegalStateException("Min must not be greater than max.");
        }

        this.min = min;
        this.max = max;
    }

    public boolean overlaps(Interval<K> that) {
        if (that == null) {
            return false;
        }

        int cmp = this.min.compareTo(that.min);

        if (cmp < 0) {
            // Part of this is below that.
            cmp = this.max.compareTo(that.min);
            if (cmp > 0) {
                // Part of this is above that.
                return true;
            }
            else {
                // If max is below or equal to min, false.
                return false;
            }
        } else if (cmp > 0) {
            // Part of this is in or above this range.
            cmp = this.min.compareTo(that.max);
            if (cmp < 0) {
                // Min is inside.
                return true;
            }
            else {
                return false;
            }
        } else {
            // MIns are equal. Overlap.
            return true;
        }
    }

    public boolean contains(final Interval<K> that) {
        if (that == null) {
            return false;
        }

        int cmp = this.min.compareTo(that.min);

        if (cmp < 0) {
            // This min is lower.
            cmp = this.max.compareTo(that.max);
            if (cmp > 0) {
                // This max is higher.
                return true;
            }
        }

        return false;
    }

    public boolean containsOrEqual(final Interval<K> that) {
        if (that == null) {
            return false;
        }

        int cmp = this.min.compareTo(that.min);

        if (cmp <= 0) {
            // This min is lower.
            cmp = this.max.compareTo(that.max);
            if (cmp >= 0) {
                // This max is higher.
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }

        if (! (o instanceof Interval)) {
            return false;
        }

        final Interval<K> that = (Interval<K>) o;

        if (!this.min.equals(that.min)) {
            return false;
        }

        if (!this.max.equals(that.max)) {
            return false;
        }

        return true;
    }

    /**
     * Is the Node fully below the given node?
     *
     * That is, is the max of this node equal to r lower than the min of the given node?
     *
     * @param that The given node.
     * @return True if the max of this node is equal to or lower than the min of the given node.
     */
    public boolean below(final Interval<K> that) {
        return this.max.compareTo(that.min) <= 0;
    }

    /**
     * Is this Node fully above the given node?
     *
     * That is, is the min of this node equal to or higher than the max of the given node?
     *
     * @param that The given node.
     * @return True if the min of this node is equal to or higher than the max of the given node.
     */
    public boolean above(final Interval<K> that) {
        return this.min.compareTo(that.max) >= 0;
    }

    public K getMin() {
        return min;
    }

    public K getMax() {
        return max;
    }
}
