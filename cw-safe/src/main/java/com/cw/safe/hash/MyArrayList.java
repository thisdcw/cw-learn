package com.cw.safe.hash;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * @author thisdcw
 * @date 2025年05月26日 10:38
 */
public class MyArrayList<T> implements Iterable<T> {

    private T[] elements;

    private int size;

    private static final int DEFAULT_SIZE = 3;

    private int modCount = 0;

    public MyArrayList() {
        elements = (T[]) new Object[DEFAULT_SIZE];
        modCount++;
    }

    public void add(T element) {
        if (size == (DEFAULT_SIZE * modCount) - 1) {
            T[] oldElements = elements;
            elements = (T[]) new Object[DEFAULT_SIZE * (++modCount)];
            System.arraycopy(oldElements, 0, elements, 0, oldElements.length);
            System.out.println("扩容" + elements.length);
        }
        elements[size++] = element;
    }

    public T get(int index) {
        return elements[index];
    }

    public int size() {
        return size;
    }

    public void remove(int index) {
        for (int i = index; i < elements.length - 1; i++) {
            elements[i] = elements[i + 1];
        }
        size--;
    }

    @Override
    public String toString() {

        StringBuilder msg = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            msg.append(",").append(elements[i]);
        }
        msg.append("]");

        return msg.toString();
    }


    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int cursor = 0;
            private final int expectedMod = modCount;

            @Override
            public boolean hasNext() {
                if (expectedMod != modCount) {
                    throw new ConcurrentModificationException();
                }
                return cursor < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return elements[cursor++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
