/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.maps.android.quadtree;

import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.geometry.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A quad tree which tracks items with a Point geometry.
 * See http://en.wikipedia.org/wiki/Quadtree for details on the data structure.
 * This class is not thread safe.
 */
@Deprecated // Experimental.
public class PointQuadTree<T extends PointQuadTree.Item> {
    public interface Item {
        Point getPoint();
    }

    /**
     * The bounds of this quad.
     */
    private final Bounds mBounds;

    /**
     * The depth of this quad in the tree.
     */
    private final int mDepth;

    /**
     * Maximum number of elements to store in a quad before splitting.
     */
    private final static int MAX_ELEMENTS = 60;

    /**
     * The elements inside this quad, if any.
     */
    private List<T> mItems;

    /**
     * Maximum depth.
     */
    private final static int MAX_DEPTH = 30;

    /**
     * Child quads.
     */
    private PointQuadTree[] mChildren = null;

    /**
     * Creates a new quad tree with specified bounds.
     *
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     */
    public PointQuadTree(double minX, double maxX, double minY, double maxY) {
        this(new Bounds(minX, maxX, minY, maxY));
    }

    public PointQuadTree(Bounds bounds) {
        this(bounds, 0);
    }

    private PointQuadTree(double minX, double maxX, double minY, double maxY, int depth) {
        this(new Bounds(minX, maxX, minY, maxY), depth);
    }

    private PointQuadTree(Bounds bounds, int depth) {
        mBounds = bounds;
        mDepth = depth;
    }

    /**
     * Insert an item.
     */
    public void add(T item) {
        Point point = item.getPoint();
        insert(point.x, point.y, item);
    }

    private boolean insert(double x, double y, T item) {
        if (!this.mBounds.contains(x, y)) {
            return false;
        }
        if (this.mChildren != null) {
            for (PointQuadTree<T> quad : mChildren) {
                if (quad.insert(x, y, item)) {
                    return true;
                }
            }
            return false; // should not happen
        }

        if (mItems == null) {
            mItems = new ArrayList<T>();
        }
        mItems.add(item);
        if (mItems.size() > MAX_ELEMENTS && mDepth < MAX_DEPTH) {
            split();
        }
        return true;
    }

    /**
     * Split this quad.
     */
    private void split() {
        mChildren = new PointQuadTree[]{
                new PointQuadTree(mBounds.minX, mBounds.midX, mBounds.minY, mBounds.midY, mDepth + 1),
                new PointQuadTree(mBounds.midX, mBounds.maxX, mBounds.minY, mBounds.midY, mDepth + 1),
                new PointQuadTree(mBounds.minX, mBounds.midX, mBounds.midY, mBounds.maxY, mDepth + 1),
                new PointQuadTree(mBounds.midX, mBounds.maxX, mBounds.midY, mBounds.maxY, mDepth + 1)
        };

        List<T> items = mItems;
        mItems = null;

        for (T item : items) {
            // re-insert items into child quads.
            add(item);
        }
    }

    /**
     * Remove the given item from the set.
     *
     * @return whether the item was removed.
     */
    public boolean remove(T item) {
        Point point = item.getPoint();
        return remove(point.x, point.y, item);
    }

    private boolean remove(double x, double y, T item) {
        if (!this.mBounds.contains(x, y)) {
            return false;
        }
        if (mChildren != null) {
            for (PointQuadTree<T> quad : mChildren) {
                if (quad.remove(x, y, item)) {
                    return true;
                }
            }
            return false;
        } else {
            return mItems.remove(item);
        }
    }

    public void clear() {
        mChildren = null;
        if (mItems != null) {
            mItems.clear();
        }
    }

    /**
     * Search for all items within a given bounds.
     */
    public Collection<T> search(Bounds searchBounds) {
        final List<T> results = new ArrayList<T>();
        search(searchBounds, results);
        return results;
    }

    private void search(Bounds searchBounds, Collection<T> results) {
        if (!mBounds.intersects(searchBounds)) {
            return;
        }

        if (this.mChildren != null) {
            for (PointQuadTree<T> quad : mChildren) {
                quad.search(searchBounds, results);
            }
        } else if (mItems != null) {
            for (T item : mItems) {
                if (searchBounds.contains(item.getPoint())) {
                    results.add(item);
                }
            }
        }
    }
}
