/***
 Copyright (c) 2016 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package info.guardianproject.netcipher.hurl;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// inspired by https://github.com/explodes/easy-android/blob/master/src/io/explod/android/collections/weak/WeakList.java

/**
 * Weak implementation of a set. Elements are held weakly and
 * therefore may vanish due to GC, but that is all hidden by the
 * implementation.
 *
 * @param <T> The type of data that the set "holds"
 */
public class WeakSet<T> implements Iterable<T> {
  private final Set<WeakReference<T>> items=
    new HashSet<WeakReference<T>>();

  /**
   * Add an item to the set. Under the covers, this gets wrapped
   * in a WeakReference.
   *
   * @param item item to add
   * @return true if added successfully, false otherwise
   */
  public boolean add(T item) {
    return(items.add(new WeakReference<T>(item)));
  }

  /**
   * Removes an item from the set. Under the covers, uses
   * the WeakIterator to find this, also cleaning out dead
   * wood along the way.
   *
   * @param item item to remove
   * @return true if item removed successfully, false otherwise
   */
  public boolean remove(T item) {
    final Iterator<T> iterator=iterator();

    while (iterator.hasNext()) {
      if (iterator.next()==item) {
        iterator.remove();

        return(true);
      }
    }

    return(false);
  }

  /**
   * Used to support Iterable, so a WeakSet can be used in
   * Java enhanced for syntax
   *
   * @return a WeakIterator on the set contents
   */
  @Override
  public Iterator<T> iterator() {
    return(new WeakIterator());
  }

  // inspired by https://github.com/explodes/easy-android/blob/master/src/io/explod/android/collections/weak/WeakIterator.java

  /**
   * Iterator over the contents of the WeakSet, skipping over
   * GC'd items
   */
  class WeakIterator implements Iterator<T> {
    private final Iterator<WeakReference<T>> itemIterator;
    private T nextItem=null;

    /**
     * Constructor. Creates the itemIterator that is the
     * "real" iterator for the underlying collection. Calls
     * moveToNext() to set the iterator (and nextItem) to the
     * first non-GC'd entry.
     */
    WeakIterator() {
      itemIterator=items.iterator();
      moveToNext();
    }

    /**
     * @return true if we have data, false otherwise
     */
    @Override
    public boolean hasNext() {
      return(nextItem!=null);
    }

    /**
     * Moves to the next item, skipping over GC'd items.
     *
     * @return the current item before the move
     */
    @Override
    public T next() {
      T result=nextItem;

      moveToNext();

      return(result);
    }

    /**
     * Removes whatever was last returned by next()
     */
    @Override
    public void remove() {
      itemIterator.remove();
    }

    private void moveToNext() {
      nextItem=null;

      while (nextItem==null && itemIterator.hasNext()) {
        nextItem=itemIterator.next().get();

        if (nextItem==null) {
          remove();
        }
      }
    }
  }
}
