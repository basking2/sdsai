package com.github.basking2.sdsai;

/**
 * A half-open interval the starts at point a and continues until point b but excluding point b.
 *
 * Because of this, the interval [1, 2) is above the interval [0, 1) but is not above the point 1.
 * This is because the end value, 1, in the interval [0, 1) is excluded while when comparing the point 1
 * to the start of the interval [1, 2), point 1 is included.
 *
 * @param <K>
 */
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

    /**
     * If the point is contained in this range.
     * @param pt The point.
     * @return True of the point is in the range. Recall that the max is exclusive.
     */
    public boolean contains(final K pt) {
        if (pt == null) {
            return false;
        }

        int cmp = this.min.compareTo(pt);

        if (cmp <= 0) {
            // This min is lower or equal.
            cmp = this.max.compareTo(pt);
            if (cmp > 0) {
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
        return below(that.min, that.max);
    }

    public boolean below(final K min, final K max) {
        return this.max.compareTo(min) <= 0;
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
        return above(that.min, that.max);
    }

    public boolean above(final K min, final K max) {
        return this.min.compareTo(max) >= 0;
    }

    public K getMin() {
        return min;
    }

    public K getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "[" + min + " - " + max + ")";
    }
}
