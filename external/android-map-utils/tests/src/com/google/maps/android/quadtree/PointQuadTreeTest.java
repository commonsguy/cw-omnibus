package com.google.maps.android.quadtree;

import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.geometry.Point;

import junit.framework.TestCase;

import java.util.Collection;

public class PointQuadTreeTest extends TestCase {

    private PointQuadTree<Item> mTree;

    public void setUp() {
        mTree = new PointQuadTree<Item>(0, 1, 0, 1);
    }

    public void testEmpty() {
        Collection<Item> items = searchAll();
        assertEquals(0, items.size());
    }

    public void testMultiplePoints() {
        Item item1 = new Item(0, 0);
        mTree.add(item1);
        Item item2 = new Item(.1, .1);
        mTree.add(item2);
        Item item3 = new Item(.2, .2);
        mTree.add(item3);

        Collection<Item> items = searchAll();
        assertEquals(3, items.size());

        assertTrue(items.contains(item1));
        assertTrue(items.contains(item2));
        assertTrue(items.contains(item3));

        mTree.remove(item1);
        mTree.remove(item2);
        mTree.remove(item3);

        assertEquals(0, searchAll().size());
    }

    public void testSameLocationDifferentPoint() {
        mTree.add(new Item(0, 0));
        mTree.add(new Item(0, 0));

        assertEquals(2, searchAll().size());
    }

    public void testClear() {
        mTree.add(new Item(.1, .1));
        mTree.add(new Item(.2, .2));
        mTree.add(new Item(.3, .3));

        mTree.clear();
        assertEquals(0, searchAll().size());
    }

    public void testSearch() {
        for (int i = 0; i < 1000; i++) {
            mTree.add(new Item(i / 2000.0, i / 2000.0));
        }

        assertEquals(1000, searchAll().size());
        assertEquals(1, mTree.search(new Bounds((double) 0, 0.0001, (double) 0, 0.0001)).size());
        assertEquals(0, mTree.search(new Bounds(.7, .8, .7, .8)).size());
    }

    public void testVeryDeepTree() {
        for (int i = 0; i < 3000; i++) {
            mTree.add(new Item(0, 0));
        }

        assertEquals(3000, searchAll().size());
        assertEquals(3000, mTree.search(new Bounds(0, .1, 0, .1)).size());
        assertEquals(0, mTree.search(new Bounds(.1, 1, .1, 1)).size());
    }

    private Collection<Item> searchAll() {
        return mTree.search(new Bounds(0, 1, 0, 1));
    }

    private static class Item implements PointQuadTree.Item {
        private final Point mPoint;

        private Item(double x, double y) {
            this.mPoint = new Point(x, y);
        }

        @Override
        public Point getPoint() {
            return mPoint;
        }
    }
}
