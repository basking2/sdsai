package com.github.basking2.sdsai.marchinesquares;

/**
 * To track when a point has been visited, when a node is visited it is given a color.
 *
 * Points are colored "0" at initialization. When they are collected into a feature they
 * are colored "1" and when they are stitched they are colored to "2".
 *
 * Colors are a feature of the LinkedList.Node implementation and are kept out of the
 * Point declaration. The color of a point has no meaning apart from its connectivity
 * to the rest of the Points by collection or stitching.
 */
public class Colors {

    // Colors is just a namespace.
    private Colors(){}

    /**
     * When the tile is built point colors are initially 0.
     * When we detect loops to build polygons we set those values to COLLECT_COLOR.
     * When tiles are zipped together, another color should be used to detect when connecting a tile completes a
     * loop and reset otherwise.
     */
    public static final byte COLLECT_COLOR = 1;

}
