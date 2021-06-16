package com.github.basking2.sdsai;

import org.junit.Test;

import org.hamcrest.CoreMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class RTreeTest {

    @Test
    public void testAdd() {
        final RTree<Integer, Integer> rtree = new RTree<>();

        rtree.add(new Integer[][]{ { 0, 10 }, { 0, 10 }}, 0);
        rtree.add(new Integer[][]{ { 0, 10 }, { 0, 10 }}, 1);
        rtree.add(new Integer[][]{ { 1, 9 }, { 1, 9 }}, 2);
        rtree.add(new Integer[][]{ { 0, 10 }, { 1, 11 }}, 3);
        rtree.add(new Integer[][]{ { 0, 10 }, { 0, 11 }}, 4);
    }

    @Test
    public void testFind() {
        final RTree<Integer, Integer> rtree = new RTree<>();

        rtree.add(new Integer[][]{ { 0, 10 }, { 0, 10 }}, 0);
        rtree.add(new Integer[][]{ { 0, 10 }, { 0, 10 }}, 1);
        rtree.add(new Integer[][]{ { 1, 9 }, { 1, 9 }}, 2);
        rtree.add(new Integer[][]{ { 0, 10 }, { 1, 11 }}, 3);
        rtree.add(new Integer[][]{ { 0, 10 }, { 0, 11 }}, 4);

        Integer i = rtree.find(new Integer[][]{ { 0, 10 }, { 0, 10 }}).getT();

        assertThat(i, CoreMatchers.either(CoreMatchers.equalTo(Integer.valueOf(0))).or(CoreMatchers.equalTo(Integer.valueOf(1))));

        assertEquals(Integer.valueOf(2), rtree.find(new Integer[][]{ { 1, 9 }, { 1, 9 }}).getT());
        assertEquals(Integer.valueOf(3), rtree.find(new Integer[][]{ { 0, 10 }, { 1, 11 }}).getT());
        assertEquals(Integer.valueOf(4), rtree.find(new Integer[][]{ { 0, 10 }, { 0, 11 }}).getT());
    }

    @Test
    public void testDelete() {
        final RTree<Integer, Integer> rtree = new RTree<>();

        rtree.add(new Integer[][]{ { 0, 10 }, { 0, 10 }}, 0);
        rtree.add(new Integer[][]{ { 0, 10 }, { 0, 10 }}, 1);
        rtree.add(new Integer[][]{ { 1, 9 }, { 1, 9 }}, 2);
        rtree.add(new Integer[][]{ { 0, 10 }, { 1, 11 }}, 3);
        rtree.add(new Integer[][]{ { 0, 10 }, { 0, 11 }}, 4);

        assertEquals(5, rtree.getSize());

        Integer i = rtree.delete(new Integer[][]{ { 0, 10 }, { 0, 10 }});
        assertThat(i, CoreMatchers.either(CoreMatchers.equalTo(Integer.valueOf(0))).or(CoreMatchers.equalTo(Integer.valueOf(1))));
        assertEquals(4, rtree.getSize());
        i = rtree.delete(new Integer[][]{ { 0, 10 }, { 0, 10 }});
        assertThat(i, CoreMatchers.either(CoreMatchers.equalTo(Integer.valueOf(0))).or(CoreMatchers.equalTo(Integer.valueOf(1))));
        assertEquals(3, rtree.getSize());

        assertEquals(Integer.valueOf(2), rtree.delete(new Integer[][]{ { 1, 9 }, { 1, 9 }}));
        assertEquals(2, rtree.getSize());
        assertEquals(Integer.valueOf(3), rtree.delete(new Integer[][]{ { 0, 10 }, { 1, 11 }}));
        assertEquals(1, rtree.getSize());
        assertEquals(Integer.valueOf(4), rtree.delete(new Integer[][]{ { 0, 10 }, { 0, 11 }}));
        assertEquals(0, rtree.getSize());
    }

    @Test
    public void testFindEnclosing() {
        final RTree<Integer, Integer> rtree = new RTree<>();

        rtree.add(new Integer[][]{{0, 10}, {0, 10}}, 0);
        rtree.add(new Integer[][]{{0, 10}, {0, 10}}, 1);
        rtree.add(new Integer[][]{{1, 9}, {1, 9}}, 2);
        rtree.add(new Integer[][]{{0, 10}, {1, 11}}, 3);
        rtree.add(new Integer[][]{{0, 10}, {0, 11}}, 4);

        final List<Integer> l = new ArrayList<>();

        rtree.findEnclosing(new Integer[][]{{0, 11}, {0,11}}, v -> l.add(v.getT()));
        assertEquals(0, l.size());
        l.clear();

        rtree.findEnclosing(new Integer[][]{{1, 9}, {0,10}}, v -> l.add(v.getT()));
        assertEquals(1, l.size());
        l.clear();

        rtree.findEnclosing(new Integer[][]{{1, 9}, {1,9}}, v -> l.add(v.getT()));
        assertEquals(4, l.size());
        l.clear();

        rtree.findEnclosing(new Integer[][]{{1, 8}, {1,8}}, v -> l.add(v.getT()));
        assertEquals(5, l.size());
        l.clear();
    }

    @Test
    public void testFindEnclosed() {
        final RTree<Integer, Integer> rtree = new RTree<>();

        rtree.add(new Integer[][]{{0, 10}, {0, 10}}, 0);
        rtree.add(new Integer[][]{{0, 10}, {0, 10}}, 1);
        rtree.add(new Integer[][]{{1, 9}, {1, 9}}, 2);
        rtree.add(new Integer[][]{{0, 10}, {1, 11}}, 3);
        rtree.add(new Integer[][]{{0, 10}, {0, 11}}, 4);

        final List<Integer> l = new ArrayList<>();

        rtree.findEnclosed(new Integer[][]{{1, 8}, {1,8}}, v -> l.add(v.getT()));
        assertEquals(0, l.size());
        l.clear();

        rtree.findEnclosed(new Integer[][]{{0, 10}, {0,10}}, v -> l.add(v.getT()));
        assertEquals(3, l.size());
        l.clear();

        rtree.findEnclosed(new Integer[][]{{0, 10}, {0,11}}, v -> l.add(v.getT()));
        assertEquals(5, l.size());
        l.clear();
    }
}
