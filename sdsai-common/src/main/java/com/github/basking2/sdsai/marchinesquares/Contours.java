package com.github.basking2.sdsai.marchinesquares;

/**
 * Contours are lines in a square.
 */
public class Contours {
    /**
     * An array of integers values that represent lines in a square.
     *
     * A square is a 4-cell (2x2) grid. Lines start on even indexes
     * (0, 2, 4) and end on odd indexes (1, 3, 5). Thus the array of 8
     * can hold 4 line segment.
     *
     * If a line starts and ends on 0, it is not set.
     *
     * Otherwise, the line will be defined by the even-indexed value being the
     * side of the square the line begins on and the odd-indexed value being the
     * side of the square the line ends on. For line start and stop values,
     * 0 means the line starts or ends in the center of the top-side of the square.
     * A value of 1 means the line starts or ends in the center of the right-side of the square.
     * A value of 2 means the line starts or ends in the center of the bottom-side of the square.
     * A value of 3 means the line starts or ends in the center of the left-side of the square.
     */
    private final short[] lines;

    private final int lineCount;
    /**
     * Create a contour given the 4-element array of point values.
     *
     * Point values may be -1, 0, or 1 denoting that a corner of the square is
     * below, at, or above the threshold. The 4-value array is used
     * to populate the {@link #lines} member of this class with line segments.
     *
     * The order of the points, starting at index 0 are top-left, top-right, bottom-right, bottom-left.
     * That is, they proceed around the square in a clockwise manner.
     *
     * @param pointValues A 4-element array of values -1, 0, or 1.
     */
    public Contours(final int[] pointValues) {
        if (pointValues.length != 4) {
            throw new IllegalArgumentException("The pointValues array must be 4 elements long.");
        }

        switch (pointValues[0]) {
            case -1:
                switch (pointValues[1]) {
                    case -1:
                        switch (pointValues[2]) {
                            case -1:
                                switch (pointValues[3]) {
                                    case -1:
                                        // -1 -1 All below. No contours.
                                        // -1 -1
                                        lines = new short[0];
                                        lineCount = 0;
                                        break;
                                    case 0:
                                        // -1 -1
                                        //  0 -1
                                        lines = new short[]{ 2, 3 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        // -1 -1
                                        //  1 -1
                                        lines = new short[]{ 2, 3, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (pointValues[3]) {
                                    case -1:
                                        // -1 -1
                                        // -1  0
                                        lines = new short[]{ 1, 2 };
                                        lineCount = 1;
                                        break;
                                    case 0:
                                        // -1 -1
                                        //  0  0
                                        lines = new short[]{ 1, 3 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        // -1 -1
                                        //  1  0
                                        lines = new short[]{ 1, 3, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (pointValues[3]) {
                                    case -1:
                                        // -1 -1
                                        // -1  1
                                        lines = new short[]{ 1, 2, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        // -1 -1
                                        //  0  1
                                        lines = new short[]{ 3, 1, 1, 2 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        // -1 -1
                                        //  1  1
                                        lines = new short[]{ 1, 3, 3, 1 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                        }
                        break;
                    case 0:
                        switch (pointValues[2]) {
                            case -1:
                                switch (pointValues[3]) {
                                    case -1:
                                        // -1  0
                                        // -1 -1
                                        lines = new short[]{ 0, 1 };
                                        lineCount = 1;
                                        break;
                                    case 0:
                                        // -1  0
                                        //  0 -1
                                        lines = new short[]{ 0, 3, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        // -1  0
                                        //  1 -1
                                        lines = new short[]{ 0, 3, 3, 2, 2, 1 };
                                        lineCount = 3;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (pointValues[3]) {
                                    case -1:
                                        // -1  0
                                        // -1  0
                                        lines = new short[]{ 0, 2 };
                                        lineCount = 1;
                                        break;
                                    case 0:
                                        // -1  0
                                        //  0  0
                                        lines = new short[]{ 0, 3 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        // -1  0
                                        //  1  0
                                        lines = new short[]{ 0, 3, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (pointValues[3]) {
                                    case -1:
                                        // -1  0
                                        // -1  1
                                        lines = new short[]{ 0, 2, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        // -1  0
                                        //  0  1
                                        lines = new short[]{ 0, 3, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        // -1  0
                                        //  1  1
                                        lines = new short[]{ 0, 3, 3, 1 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                        }
                        break;
                    case 1:
                        switch (pointValues[2]) {
                            case -1:
                                switch (pointValues[3]) {
                                    case -1:
                                        // -1  1
                                        // -1 -1
                                        lines = new short[]{ 0, 1, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        // -1  1
                                        //  0 -1
                                        lines = new short[]{ 2, 1, 1, 0, 0, 3 };
                                        lineCount = 3;
                                        break;
                                    case 1:
                                        // -1  1
                                        //  1 -1
                                        lines = new short[]{ 2, 1, 1, 0, 0, 3, 3, 2 };
                                        lineCount = 4;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (pointValues[3]) {
                                    case -1:
                                        // -1  1
                                        // -1  0
                                        lines = new short[]{ 1, 0, 0, 2 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        // -1  1
                                        //  0  0
                                        lines = new short[]{ 1, 0, 0, 3};
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        // -1  1
                                        //  1  0
                                        lines = new short[]{ 1, 0, 0, 3, 3, 2 };
                                        lineCount = 3;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (pointValues[3]) {
                                    case -1:
                                        // -1  1
                                        // -1  1
                                        lines = new short[]{ 2, 0, 0, 2 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        // -1  1
                                        //  0  1
                                        lines = new short[]{ 2, 0, 0, 3 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        // -1  1
                                        //  1  1
                                        lines = new short[]{ 0, 3, 3, 0 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                }
                break;
            case 0:
                switch (pointValues[1]) {
                    case -1:
                        switch (pointValues[2]) {
                            case -1:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  0 -1
                                        // -1 -1
                                        lines = new short[]{ 3, 0 };
                                        lineCount = 1;
                                        break;
                                    case 0:
                                        //  0 -1
                                        //  0 -1
                                        lines = new short[]{ 2, 0 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  0 -1
                                        //  1 -1
                                        lines = new short[]{ 3, 2, 2, 0 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  0 -1
                                        // -1  0
                                        lines = new short[]{ 1, 0, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  0 -1
                                        //  0  0
                                        lines = new short[]{ 1, 0 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  0 -1
                                        //  1  0
                                        lines = new short[]{ 1, 0, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  0 -1
                                        // -1  1
                                        lines = new short[]{ 3, 2, 2, 1, 1, 0 };
                                        lineCount = 3;
                                        break;
                                    case 0:
                                        //  0 -1
                                        //  0  1
                                        lines = new short[]{ 2, 1, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  0 -1
                                        //  1  1
                                        lines = new short[]{ 3, 1, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                        }
                        break;
                    case 0:
                        switch (pointValues[2]) {
                            case -1:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  0  0
                                        // -1 -1
                                        lines = new short[]{ 1, 3 };
                                        lineCount = 1;
                                        break;
                                    case 0:
                                        //  0  0
                                        //  0 -1
                                        lines = new short[]{ 1, 2 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  0  0
                                        //  1 -1
                                        lines = new short[]{ 3, 2, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  0  0
                                        // -1  0
                                        lines = new short[]{ 3, 2 };
                                        lineCount = 1;
                                        break;
                                    case 0:
                                        //  0  0
                                        //  0  0
                                        lines = new short[0];
                                        lineCount = 0;
                                        break;
                                    case 1:
                                        //  0  0
                                        //  1  0
                                        lines = new short[]{ 3, 2 };
                                        lineCount = 1;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  0  0
                                        // -1  1
                                        lines = new short[]{ 3, 2, 2, 1, };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  0  0
                                        //  0  1
                                        lines = new short[]{ 2, 1 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  0  0
                                        //  1  1
                                        lines = new short[]{ 3, 1 };
                                        lineCount = 1;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                        }
                        break;
                    case 1:
                        switch (pointValues[2]) {
                            case -1:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  0  1
                                        // -1 -1
                                        lines = new short[]{ 3, 1, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  0  1
                                        //  0 -1
                                        lines = new short[]{ 2, 1, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  0  1
                                        //  1 -1
                                        lines = new short[]{ 3, 2, 2, 1, 1, 0 };
                                        lineCount = 3;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  0  1
                                        // -1  0
                                        lines = new short[]{3, 2, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  0  1
                                        //  0  0
                                        lines = new short[]{ 1, 0 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  0  1
                                        //  1  0
                                        lines = new short[]{ 3, 2, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  0  1
                                        // -1  1
                                        lines = new short[]{ 3, 2, 2, 0 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  0  1
                                        //  0  1
                                        lines = new short[]{ 2, 0 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  0  1
                                        //  1  1
                                        lines = new short[]{ 3, 0 };
                                        lineCount = 1;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                }
                break;
            case 1:
                switch (pointValues[1]) {
                    case -1:
                        switch (pointValues[2]) {
                            case -1:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  1 -1
                                        // -1 -1
                                        lines = new short[]{ 3, 0, 0, 3 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  1 -1
                                        //  0 -1
                                        lines = new short[]{ 2, 0, 0, 3 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  1 -1
                                        //  1 -1
                                        lines = new short[]{ 2, 0, 0, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  1 -1
                                        // -1  0
                                        lines = new short[]{ 1, 0, 0, 3, 3, 2 };
                                        lineCount = 3;
                                        break;
                                    case 0:
                                        //  1 -1
                                        //  0  0
                                        lines = new short[]{ 1, 0, 0, 3 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  1 -1
                                        //  1  0
                                        lines = new short[]{ 1, 0, 0, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  1 -1
                                        // -1  1
                                        lines = new short[]{ 2, 1, 1, 0, 0, 3, 3, 2 };
                                        lineCount = 4;
                                        break;
                                    case 0:
                                        //  1 -1
                                        //  0  1
                                        lines = new short[]{ 2, 1, 1, 0, 0, 3 };
                                        lineCount = 3;
                                        break;
                                    case 1:
                                        //  1 -1
                                        //  1  1
                                        lines = new short[]{ 1, 0, 0, 1 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                        }
                        break;
                    case 0:
                        switch (pointValues[2]) {
                            case -1:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  1  0
                                        // -1 -1
                                        lines = new short[]{ 0, 3, 3, 1 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  1  0
                                        //  0 -1
                                        lines = new short[]{ 0, 3, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  1  0
                                        //  1 -1
                                        lines = new short[]{ 0, 2, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  1  0
                                        // -1  0
                                        lines = new short[]{ 0, 3, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  1  0
                                        //  0  0
                                        lines = new short[]{ 0, 3 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  1  0
                                        //  1  0
                                        lines = new short[]{ 0, 2 };
                                        lineCount = 1;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  1  0
                                        // -1  1
                                        lines = new short[]{ 0, 3, 3, 2, 2, 1 };
                                        lineCount = 3;
                                        break;
                                    case 0:
                                        //  1  0
                                        //  0  1
                                        lines = new short[]{ 0, 3, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  1  0
                                        //  1  1
                                        lines = new short[]{ 0, 1 };
                                        lineCount = 1;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                        }
                        break;
                    case 1:
                        switch (pointValues[2]) {
                            case -1:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  1  1
                                        // -1 -1
                                        lines = new short[]{ 1, 3, 3, 1 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  1  1
                                        //  0 -1
                                        lines = new short[]{ 2, 1, 1, 3 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  1  1
                                        //  1 -1
                                        lines = new short[]{ 2, 1, 1, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  1  1
                                        // -1  0
                                        lines = new short[]{ 1, 3, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  1  1
                                        //  0  0
                                        lines = new short[]{ 1, 3 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  1  1
                                        //  1  0
                                        lines = new short[]{ 1, 2 };
                                        lineCount = 1;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (pointValues[3]) {
                                    case -1:
                                        //  1  1
                                        // -1  1
                                        lines = new short[]{ 2, 3, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  1  1
                                        //  0  1
                                        lines = new short[]{ 2, 3 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  1  1
                                        //  1  1
                                        lines = new short[0];
                                        lineCount = 0;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            default:
                                throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                }
                break;
            default:
                throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
        }

    }
}
