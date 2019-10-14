/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.util;

/**
 * This class provides a skeletal implementation of the <tt>Collection</tt>
 * interface, to minimize the effort required to implement this interface. <p>
 *
 * To implement an unmodifiable collection, the programmer needs only to
 * extend this class and provide implementations for the <tt>iterator</tt> and
 * <tt>size</tt> methods.  (The iterator returned by the <tt>iterator</tt>
 * method must implement <tt>hasNext</tt> and <tt>next</tt>.)<p>
 *
 * To implement a modifiable collection, the programmer must additionally
 * override this class's <tt>add</tt> method (which otherwise throws an
 * <tt>UnsupportedOperationException</tt>), and the iterator returned by the
 * <tt>iterator</tt> method must additionally implement its <tt>remove</tt>
 * method.<p>
 *
 * The programmer should generally provide a void (no argument) and
 * <tt>Collection</tt> constructor, as per the recommendation in the
 * <tt>Collection</tt> interface specification.<p>
 *
 * The documentation for each non-abstract method in this class describes its
 * implementation in detail.  Each of these methods may be overridden if
 * the collection being implemented admits a more efficient implementation.<p>
 *
 * This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see Collection
 * @since 1.2
 */

// 集合类的公有抽象父类
public abstract class AbstractCollection<E> implements Collection<E> {
    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    // 默认构造函数 方便子类调用
    protected AbstractCollection() {
    }

    // Query Operations

    /**
     * Returns an iterator over the elements contained in this collection.
     *
     * @return an iterator over the elements contained in this collection
     */
    // 迭代器
    public abstract Iterator<E> iterator();
    // size
    public abstract int size();

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns <tt>size() == 0</tt>.
     */
    // size == 0
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over the elements in the collection,
     * checking each element in turn for equality with the specified element.
     *
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    // 用迭代器实现遍历 时间复杂度 O(n)
    public boolean contains(Object o) {
        Iterator<E> it = iterator();
        if (o==null) {
            while (it.hasNext())
                if (it.next()==null)
                    return true;
        } else {
            while (it.hasNext())
                if (o.equals(it.next()))
                    return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns an array containing all the elements
     * returned by this collection's iterator, in the same order, stored in
     * consecutive elements of the array, starting with index {@code 0}.
     * The length of the returned array is equal to the number of elements
     * returned by the iterator, even if the size of this collection changes
     * during iteration, as might happen if the collection permits
     * concurrent modification during iteration.  The {@code size} method is
     * called only as an optimization hint; the correct result is returned
     * even if the iterator returns a different number of elements.
     *
     * <p>This method is equivalent to:
     *
     *  <pre> {@code
     * List<E> list = new ArrayList<E>(size());
     * for (E e : this)
     *     list.add(e);
     * return list.toArray();
     * }</pre>
     */
    // 链表结构转成数组
    public Object[] toArray() {
        // Estimate size of array; be prepared to see more or fewer elements
        Object[] r = new Object[size()];
        Iterator<E> it = iterator();
        for (int i = 0; i < r.length; i++) {
            if (! it.hasNext()) // fewer elements than expected
                return Arrays.copyOf(r, i);
            r[i] = it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns an array containing all the elements
     * returned by this collection's iterator in the same order, stored in
     * consecutive elements of the array, starting with index {@code 0}.
     * If the number of elements returned by the iterator is too large to
     * fit into the specified array, then the elements are returned in a
     * newly allocated array with length equal to the number of elements
     * returned by the iterator, even if the size of this collection
     * changes during iteration, as might happen if the collection permits
     * concurrent modification during iteration.  The {@code size} method is
     * called only as an optimization hint; the correct result is returned
     * even if the iterator returns a different number of elements.
     *
     * <p>This method is equivalent to:
     *
     *  <pre> {@code
     * List<E> list = new ArrayList<E>(size());
     * for (E e : this)
     *     list.add(e);
     * return list.toArray(a);
     * }</pre>
     *
     * @throws ArrayStoreException  {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    // 转换成数组  带类型
    public <T> T[] toArray(T[] a) {
        // Estimate size of array; be prepared to see more or fewer elements
        int size = size();
        T[] r = a.length >= size ? a :
                  (T[])java.lang.reflect.Array
                  .newInstance(a.getClass().getComponentType(), size);
        Iterator<E> it = iterator();

        // 具体 转化成数组
        for (int i = 0; i < r.length; i++) {
            // 若是迭代器放完了，数组还有剩余空间
            if (! it.hasNext()) { // fewer elements than expected
                if (a == r) {
                    // 数组长度完全足够，且当初给定的数组是足够的
                    r[i] = null; // null-terminate
                } else if (a.length < i) {
                    // 给定数组长度不够，自己扩容的情况
                    // 所以r的大小其实变大了所以需要重新copy一份扩容的的数组
                    return Arrays.copyOf(r, i);
                } else {
                    // TODO: 2019/10/14 这里可能是因为size和迭代器的不一致造成的情况
                    // 将r copy 到a 将末位置空
                    System.arraycopy(r, 0, a, 0, i);
                    if (a.length > i) {
                        a[i] = null;
                    }
                }
                return a;
            }
            // 首先将迭代器中有的 放进去
            r[i] = (T)it.next();
        }
        // more elements than expected
        // TODO: 2019/10/14 难道是因为size不靠谱？？？
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Reallocates the array being used within toArray when the iterator
     * returned more elements than expected, and finishes filling it from
     * the iterator.
     *
     * @param r the array, replete with previously stored elements
     * @param it the in-progress iterator over this collection
     * @return array containing the elements in the given array, plus any
     *         further elements returned by the iterator, trimmed to size
     */
    // 将it后面的再继续赋值给r
    @SuppressWarnings("unchecked")
    private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
        int i = r.length;
        while (it.hasNext()) {
            int cap = r.length;
            // 需要扩容
            if (i == cap) {
                int newCap = cap + (cap >> 1) + 1;
                // overflow-conscious code
                if (newCap - MAX_ARRAY_SIZE > 0)
                    // 如果有溢出风险
                    newCap = hugeCapacity(cap + 1);
                r = Arrays.copyOf(r, newCap);
            }
            r[i++] = (T)it.next();
        }
        // trim if overallocated
        return (i == r.length) ? r : Arrays.copyOf(r, i);
    }

    // TODO: 2019/10/14  也就多了8个元素，有必要这样做吗
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError
                ("Required array size too large");
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }

    // Modification Operations

    /**
     * {@inheritDoc}
     *
     * <p>This implementation always throws an
     * <tt>UnsupportedOperationException</tt>.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IllegalStateException         {@inheritDoc}
     */
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over the collection looking for the
     * specified element.  If it finds the element, it removes the element
     * from the collection using the iterator's remove method.
     *
     * <p>Note that this implementation throws an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by this
     * collection's iterator method does not implement the <tt>remove</tt>
     * method and this collection contains the specified object.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     */
    public boolean remove(Object o) {
        Iterator<E> it = iterator();
        if (o==null) {
            while (it.hasNext()) {
                // TODO: 2019/10/14   为什么有会存在hasNext 但是next为null
                if (it.next()==null) {
                    it.remove();
                    return true;
                }
            }
        } else {
            while (it.hasNext()) {
                if (o.equals(it.next())) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }


    // Bulk Operations

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over the specified collection,
     * checking each element returned by the iterator in turn to see
     * if it's contained in this collection.  If all elements are so
     * contained <tt>true</tt> is returned, otherwise <tt>false</tt>.
     *
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @see #contains(Object)
     */
    public boolean containsAll(Collection<?> c) {
        for (Object e : c)
            if (!contains(e))
                return false;
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over the specified collection, and adds
     * each object returned by the iterator to this collection, in turn.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> unless <tt>add</tt> is
     * overridden (assuming the specified collection is non-empty).
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IllegalStateException         {@inheritDoc}
     *
     * @see #add(Object)
     */
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c)
            if (add(e))
                modified = true;
        return modified;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over this collection, checking each
     * element returned by the iterator in turn to see if it's contained
     * in the specified collection.  If it's so contained, it's removed from
     * this collection with the iterator's <tt>remove</tt> method.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by the
     * <tt>iterator</tt> method does not implement the <tt>remove</tt> method
     * and this collection contains one or more elements in common with the
     * specified collection.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     *
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<?> it = iterator();
        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over this collection, checking each
     * element returned by the iterator in turn to see if it's contained
     * in the specified collection.  If it's not so contained, it's removed
     * from this collection with the iterator's <tt>remove</tt> method.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by the
     * <tt>iterator</tt> method does not implement the <tt>remove</tt> method
     * and this collection contains one or more elements not present in the
     * specified collection.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     *
     * @see #remove(Object)
     * @see #contains(Object)
     */
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over this collection, removing each
     * element using the <tt>Iterator.remove</tt> operation.  Most
     * implementations will probably choose to override this method for
     * efficiency.
     *
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the iterator returned by this
     * collection's <tt>iterator</tt> method does not implement the
     * <tt>remove</tt> method and this collection is non-empty.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     */
    public void clear() {
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }


    //  String conversion

    /**
     * Returns a string representation of this collection.  The string
     * representation consists of a list of the collection's elements in the
     * order they are returned by its iterator, enclosed in square brackets
     * (<tt>"[]"</tt>).  Adjacent elements are separated by the characters
     * <tt>", "</tt> (comma and space).  Elements are converted to strings as
     * by {@link String#valueOf(Object)}.
     *
     * @return a string representation of this collection
     */
    public String toString() {
        Iterator<E> it = iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            E e = it.next();
            sb.append(e == this ? "(this Collection)" : e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }

}
