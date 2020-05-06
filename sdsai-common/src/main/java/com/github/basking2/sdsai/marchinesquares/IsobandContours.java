package com.github.basking2.sdsai.marchinesquares;

/**
 * Contours are lines in a square.
 */
public class IsobandContours {
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
    protected final byte[] lines;

    protected final int lineCount;

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
    public static IsobandContours build(final byte[] pointValues) {
        if (pointValues.length != 4) {
            throw new IllegalArgumentException("The pointValues array must be 4 elements long.");
        }
        return new IsobandContours(pointValues[0], pointValues[1], pointValues[2], pointValues[3]);
    }


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
     *
     * @param p1 The north-west point.
     * @param p2 The north-east pont.
     * @param p3 The south-east point.
     * @param p4 The sout-west point.
     */
    public IsobandContours(final byte p1, final byte p2, final byte p3, final byte p4) {
        switch (p1) {
            case -1:
                switch (p2) {
                    case -1:
                        switch (p3) {
                            case -1:
                                switch (p4) {
                                    case -1:
                                        // -1 -1 All below. No contours.
                                        // -1 -1
                                        lines = new byte[0];
                                        lineCount = 0;
                                        break;
                                    case 0:
                                        // -1 -1
                                        //  0 -1
                                        lines = new byte[]{ 2, 3 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        // -1 -1
                                        //  1 -1
                                        lines = new byte[]{ 2, 3, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (p4) {
                                    case -1:
                                        // -1 -1
                                        // -1  0
                                        lines = new byte[]{ 1, 2 };
                                        lineCount = 1;
                                        break;
                                    case 0:
                                        // -1 -1
                                        //  0  0
                                        lines = new byte[]{ 1, 3 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        // -1 -1
                                        //  1  0
                                        lines = new byte[]{ 1, 3, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (p4) {
                                    case -1:
                                        // -1 -1
                                        // -1  1
                                        lines = new byte[]{ 1, 2, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        // -1 -1
                                        //  0  1
                                        lines = new byte[]{ 3, 1, 1, 2 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        // -1 -1
                                        //  1  1
                                        lines = new byte[]{ 1, 3, 3, 1 };
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
                        switch (p3) {
                            case -1:
                                switch (p4) {
                                    case -1:
                                        // -1  0
                                        // -1 -1
                                        lines = new byte[]{ 0, 1 };
                                        lineCount = 1;
                                        break;
                                    case 0:
                                        // -1  0
                                        //  0 -1
                                        lines = new byte[]{ 0, 3, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        // -1  0
                                        //  1 -1
                                        lines = new byte[]{ 0, 3, 3, 2, 2, 1 };
                                        lineCount = 3;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (p4) {
                                    case -1:
                                        // -1  0
                                        // -1  0
                                        lines = new byte[]{ 0, 2 };
                                        lineCount = 1;
                                        break;
                                    case 0:
                                        // -1  0
                                        //  0  0
                                        lines = new byte[]{ 0, 3 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        // -1  0
                                        //  1  0
                                        lines = new byte[]{ 0, 3, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (p4) {
                                    case -1:
                                        // -1  0
                                        // -1  1
                                        lines = new byte[]{ 0, 2, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        // -1  0
                                        //  0  1
                                        lines = new byte[]{ 0, 3, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        // -1  0
                                        //  1  1
                                        lines = new byte[]{ 0, 3, 3, 1 };
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
                        switch (p3) {
                            case -1:
                                switch (p4) {
                                    case -1:
                                        // -1  1
                                        // -1 -1
                                        lines = new byte[]{ 0, 1, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        // -1  1
                                        //  0 -1
                                        lines = new byte[]{ 2, 1, 1, 0, 0, 3 };
                                        lineCount = 3;
                                        break;
                                    case 1:
                                        // -1  1
                                        //  1 -1
                                        lines = new byte[]{ 2, 1, 1, 0, 0, 3, 3, 2 };
                                        lineCount = 4;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (p4) {
                                    case -1:
                                        // -1  1
                                        // -1  0
                                        lines = new byte[]{ 1, 0, 0, 2 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        // -1  1
                                        //  0  0
                                        lines = new byte[]{ 1, 0, 0, 3};
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        // -1  1
                                        //  1  0
                                        lines = new byte[]{ 1, 0, 0, 3, 3, 2 };
                                        lineCount = 3;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (p4) {
                                    case -1:
                                        // -1  1
                                        // -1  1
                                        lines = new byte[]{ 2, 0, 0, 2 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        // -1  1
                                        //  0  1
                                        lines = new byte[]{ 2, 0, 0, 3 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        // -1  1
                                        //  1  1
                                        lines = new byte[]{ 0, 3, 3, 0 };
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
                switch (p2) {
                    case -1:
                        switch (p3) {
                            case -1:
                                switch (p4) {
                                    case -1:
                                        //  0 -1
                                        // -1 -1
                                        lines = new byte[]{ 3, 0 };
                                        lineCount = 1;
                                        break;
                                    case 0:
                                        //  0 -1
                                        //  0 -1
                                        lines = new byte[]{ 2, 0 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  0 -1
                                        //  1 -1
                                        lines = new byte[]{ 3, 2, 2, 0 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (p4) {
                                    case -1:
                                        //  0 -1
                                        // -1  0
                                        lines = new byte[]{ 1, 0, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  0 -1
                                        //  0  0
                                        lines = new byte[]{ 1, 0 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  0 -1
                                        //  1  0
                                        lines = new byte[]{ 1, 0, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (p4) {
                                    case -1:
                                        //  0 -1
                                        // -1  1
                                        lines = new byte[]{ 3, 2, 2, 1, 1, 0 };
                                        lineCount = 3;
                                        break;
                                    case 0:
                                        //  0 -1
                                        //  0  1
                                        lines = new byte[]{ 2, 1, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  0 -1
                                        //  1  1
                                        lines = new byte[]{ 3, 1, 1, 0 };
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
                        switch (p3) {
                            case -1:
                                switch (p4) {
                                    case -1:
                                        //  0  0
                                        // -1 -1
                                        lines = new byte[]{ 3, 1 };
                                        lineCount = 1;
                                        break;
                                    case 0:
                                        //  0  0
                                        //  0 -1
                                        lines = new byte[]{ 2, 1 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  0  0
                                        //  1 -1
                                        lines = new byte[]{ 3, 2, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (p4) {
                                    case -1:
                                        //  0  0
                                        // -1  0
                                        lines = new byte[]{ 3, 2 };
                                        lineCount = 1;
                                        break;
                                    case 0:
                                        //  0  0
                                        //  0  0
                                        lines = new byte[0];
                                        lineCount = 0;
                                        break;
                                    case 1:
                                        //  0  0
                                        //  1  0
                                        lines = new byte[]{ 3, 2 };
                                        lineCount = 1;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (p4) {
                                    case -1:
                                        //  0  0
                                        // -1  1
                                        lines = new byte[]{ 3, 2, 2, 1, };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  0  0
                                        //  0  1
                                        lines = new byte[]{ 2, 1 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  0  0
                                        //  1  1
                                        lines = new byte[]{ 3, 1 };
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
                        switch (p3) {
                            case -1:
                                switch (p4) {
                                    case -1:
                                        //  0  1
                                        // -1 -1
                                        lines = new byte[]{ 3, 1, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  0  1
                                        //  0 -1
                                        lines = new byte[]{ 2, 1, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  0  1
                                        //  1 -1
                                        lines = new byte[]{ 3, 2, 2, 1, 1, 0 };
                                        lineCount = 3;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (p4) {
                                    case -1:
                                        //  0  1
                                        // -1  0
                                        lines = new byte[]{3, 2, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  0  1
                                        //  0  0
                                        lines = new byte[]{ 1, 0 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  0  1
                                        //  1  0
                                        lines = new byte[]{ 3, 2, 1, 0 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (p4) {
                                    case -1:
                                        //  0  1
                                        // -1  1
                                        lines = new byte[]{ 3, 2, 2, 0 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  0  1
                                        //  0  1
                                        lines = new byte[]{ 2, 0 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  0  1
                                        //  1  1
                                        lines = new byte[]{ 3, 0 };
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
                switch (p2) {
                    case -1:
                        switch (p3) {
                            case -1:
                                switch (p4) {
                                    case -1:
                                        //  1 -1
                                        // -1 -1
                                        lines = new byte[]{ 3, 0, 0, 3 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  1 -1
                                        //  0 -1
                                        lines = new byte[]{ 2, 0, 0, 3 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  1 -1
                                        //  1 -1
                                        lines = new byte[]{ 2, 0, 0, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (p4) {
                                    case -1:
                                        //  1 -1
                                        // -1  0
                                        lines = new byte[]{ 1, 0, 0, 3, 3, 2 };
                                        lineCount = 3;
                                        break;
                                    case 0:
                                        //  1 -1
                                        //  0  0
                                        lines = new byte[]{ 1, 0, 0, 3 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  1 -1
                                        //  1  0
                                        lines = new byte[]{ 1, 0, 0, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (p4) {
                                    case -1:
                                        //  1 -1
                                        // -1  1
                                        lines = new byte[]{ 2, 1, 1, 0, 0, 3, 3, 2 };
                                        lineCount = 4;
                                        break;
                                    case 0:
                                        //  1 -1
                                        //  0  1
                                        lines = new byte[]{ 2, 1, 1, 0, 0, 3 };
                                        lineCount = 3;
                                        break;
                                    case 1:
                                        //  1 -1
                                        //  1  1
                                        lines = new byte[]{ 1, 0, 0, 1 };
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
                        switch (p3) {
                            case -1:
                                switch (p4) {
                                    case -1:
                                        //  1  0
                                        // -1 -1
                                        lines = new byte[]{ 0, 3, 3, 1 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  1  0
                                        //  0 -1
                                        lines = new byte[]{ 0, 3, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  1  0
                                        //  1 -1
                                        lines = new byte[]{ 0, 2, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (p4) {
                                    case -1:
                                        //  1  0
                                        // -1  0
                                        lines = new byte[]{ 0, 3, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  1  0
                                        //  0  0
                                        lines = new byte[]{ 0, 3 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  1  0
                                        //  1  0
                                        lines = new byte[]{ 0, 2 };
                                        lineCount = 1;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (p4) {
                                    case -1:
                                        //  1  0
                                        // -1  1
                                        lines = new byte[]{ 0, 3, 3, 2, 2, 1 };
                                        lineCount = 3;
                                        break;
                                    case 0:
                                        //  1  0
                                        //  0  1
                                        lines = new byte[]{ 0, 3, 2, 1 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  1  0
                                        //  1  1
                                        lines = new byte[]{ 0, 1 };
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
                        switch (p3) {
                            case -1:
                                switch (p4) {
                                    case -1:
                                        //  1  1
                                        // -1 -1
                                        lines = new byte[]{ 1, 3, 3, 1 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  1  1
                                        //  0 -1
                                        lines = new byte[]{ 2, 1, 1, 3 };
                                        lineCount = 2;
                                        break;
                                    case 1:
                                        //  1  1
                                        //  1 -1
                                        lines = new byte[]{ 2, 1, 1, 2 };
                                        lineCount = 2;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 0:
                                switch (p4) {
                                    case -1:
                                        //  1  1
                                        // -1  0
                                        lines = new byte[]{ 1, 3, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  1  1
                                        //  0  0
                                        lines = new byte[]{ 1, 3 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  1  1
                                        //  1  0
                                        lines = new byte[]{ 1, 2 };
                                        lineCount = 1;
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Point values must be -1, 0, or 1.");
                                }
                                break;
                            case 1:
                                switch (p4) {
                                    case -1:
                                        //  1  1
                                        // -1  1
                                        lines = new byte[]{ 2, 3, 3, 2 };
                                        lineCount = 2;
                                        break;
                                    case 0:
                                        //  1  1
                                        //  0  1
                                        lines = new byte[]{ 2, 3 };
                                        lineCount = 1;
                                        break;
                                    case 1:
                                        //  1  1
                                        //  1  1
                                        lines = new byte[0];
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

    @Override
    public String toString() {
        if (lines.length == 0){
            return "{0}";
        }
        String s = "";
        for (int i = 0; i < lines.length; i+=2) {
            s += "("+lines[i]+"->"+lines[i+1]+")";
        }
        return s;
    }
}
