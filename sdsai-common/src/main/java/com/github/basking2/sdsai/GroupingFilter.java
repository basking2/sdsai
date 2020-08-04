package com.github.basking2.sdsai;

import java.util.function.Predicate;

/**
 * A grouping filter is a type of {@link Predicate} that may be combined with other instances.
 *
 * The combination of two {@link GroupingFilter} objects results in a {@link GroupingFilter}
 * that selects values that satisfy both of the functions in a single operations.
 *
 * This is useful because we can describe work to do in terms of fewer functions.
 *
 * @param <T>
 */
public interface GroupingFilter<T> extends Predicate<T> {

    class RangeImpl<T extends Comparable<T>> implements GroupingFilter<T> {
        protected final T start;
        protected final boolean startExclusive;
        protected final T end;
        protected final boolean endExclusive;

        public RangeImpl(final T start, final boolean startExclusive, final T end, final boolean endExclusive)
        {
            this.start = start;
            this.startExclusive = startExclusive;
            this.end = end;
            this.endExclusive = endExclusive;
        }

        @Override
        public boolean test(T t) {
            if (start != null) {
                final int cmp = start.compareTo(t);
                if (cmp > 0) {
                    return false;
                }

                if (cmp == 0 && startExclusive) {
                    return false;
                }
            }

            if (end != null) {
                final int cmp = end.compareTo(t);
                if (cmp < 0) {
                    return false;
                }

                if (cmp == 0 && endExclusive) {
                    return false;
                }
            }

            return true;
        }

        /**
         * Given another range, return a range the is the intersection of these two.
         * @param that The other range.
         * @return A range that is the intersection.
         */
        public RangeImpl<T> simplify(final RangeImpl<T> that) {
            final T start;
            final boolean startExclusive;
            final T end;
            final boolean endExclusive;

            if (this.start == null) {
                if (that.start != null) {
                    start = that.start;
                    startExclusive = that.startExclusive;
                } else {
                    start = null;
                    startExclusive = false;
                }
            } else if (that.start == null) {
                start = this.start;
                startExclusive = this.startExclusive;
            } else {
                final int cmp = this.start.compareTo(that.start);
                if (cmp < 0) {
                    // That has a higher start.
                    start = that.start;
                    startExclusive = that.startExclusive;
                } else if (cmp > 0) {
                    start = this.start;
                    startExclusive = this.startExclusive;
                } else {
                    start = this.start;
                    startExclusive = this.startExclusive && that.startExclusive;
                }
            }

            if (this.end == null) {
                if (that.end != null) {
                    end = that.end;
                    endExclusive = that.endExclusive;
                } else {
                    end = null;
                    endExclusive = false;
                }
            } else if (that.end == null) {
                end = this.end;
                endExclusive = this.endExclusive;
            } else {
                final int cmp = this.end.compareTo(that.end);
                if (cmp > 0) {
                    // That has a lower end.
                    end = that.end;
                    endExclusive = that.endExclusive;
                } else if (cmp < 0) {
                    end = this.end;
                    endExclusive = this.endExclusive;
                } else {
                    end = this.end;
                    endExclusive = this.endExclusive && that.endExclusive;
                }
            }

            if (start == null && end == null) {
                return new True<>();
            } else if (start != null && end != null) {
                final int cmp = start.compareTo(end);
                if (cmp > 0) {
                    return new False<>();
                }
                if (cmp == 0 && (startExclusive || endExclusive)) {
                    return new False<>();
                }
            }

            return new RangeImpl<>(start, startExclusive, end, endExclusive);
        }
    }

    class True<T extends Comparable<T>> extends RangeImpl<T> {
        public True() {
            super(null, false, null, false);
        }


        @Override
        public boolean test(final T t) {
            return true;
        }

        @Override
        public RangeImpl<T> simplify(final RangeImpl<T> that) {
            return that;
        }
    }

    class False<T extends Comparable<T>> extends RangeImpl<T> {
        public False() {
            super(null, false, null, false);
        }


        @Override
        public boolean test(final T t) {
            return false;
        }

        @Override
        public RangeImpl<T> simplify(final RangeImpl<T> that) {
            return this;
        }
    }

    class GreaterThan<T extends Comparable<T>> extends RangeImpl<T> {
        GreaterThan(final T t) {
            super(t, true, null, true);
        }
    }

    class GreaterThanEqual<T extends Comparable<T>> extends RangeImpl<T> {
        GreaterThanEqual(final T t) {
            super(t, false, null, true);
        }
    }

    class LessThan<T extends Comparable<T>> extends RangeImpl<T> {
        LessThan(final T t) {
            super(null, true, t, true);
        }
    }

    class LessThanEqual<T extends Comparable<T>> extends RangeImpl<T> {
        LessThanEqual(final T t) {
            super(null, true, t, false);
        }
    }

    class Equal<T extends Comparable<T>> extends RangeImpl<T> {
        Equal(final T t) {
            super(t, false, t, false);
        }
    }
}
