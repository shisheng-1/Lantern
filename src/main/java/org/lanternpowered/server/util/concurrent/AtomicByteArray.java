package org.lanternpowered.server.util.concurrent;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicByteArray implements Serializable {

    private static final long serialVersionUID = 3434275139515033068L;

    // The amount of bytes packed in one integer
    private static final byte PACKED_VALUES = 4;
    // The amount of bits in an integer
    private static final byte PACKED_BITS = 32;

    private static final byte VALUE_BITS = PACKED_BITS / PACKED_VALUES;
    private static final byte VALUE_MASK = (VALUE_BITS << 1) - 1;

    private static final byte INDEX_MASK = PACKED_VALUES - 1;
    private static final byte INDEX_BITS = PACKED_VALUES >> 1;

    private static int key(int combined, int index, byte value) {
        index *= VALUE_BITS;
        // Reset the bits first
        combined &= ~(VALUE_MASK << index);
        // Apply the new content if needed
        if (value != 0) {
            combined |= value << index;
        }
        return combined;
    }

    private static byte key(int combined, int index) {
        return (byte) ((combined >> (index * VALUE_BITS)) & VALUE_MASK);
    }

    private final int length;
    private final int backingArraySize;
    private final AtomicIntegerArray backingArray;

    /**
     * Creates a new {@link AtomicByteArray} of the given length, with all
     * elements initially zero.
     *
     * @param length the length of the array
     */
    public AtomicByteArray(int length) {
        this.length = length;
        this.backingArraySize = (length & INDEX_MASK) + (length >> INDEX_BITS);
        this.backingArray = new AtomicIntegerArray(this.backingArraySize);
    }

    /**
     * Creates a new {@link AtomicByteArray} of the given length, with all
     * elements copied from the initial content array. The lengths don't have
     * to match, if it's shorter then the remaining content be set to zero,
     * and if it's longer then will the rest of the content be ignored.
     *
     * @param length the length of the array
     * @param initialContent the initial content
     */
    public AtomicByteArray(int length, byte[] initialContent) {
        this.length = length;
        this.backingArraySize = (length & INDEX_MASK) + (length >> INDEX_BITS);

        int[] array = new int[this.backingArraySize];
        for (int i = 0; i < this.backingArraySize; i++) {
            boolean flag = false;
            int value = 0;
            for (int j = 0; j < PACKED_VALUES; j++) {
                int k = i + j;
                if (k >= initialContent.length || k >= length) {
                    flag = true;
                    break;
                }
                value = key(value, j, initialContent[k]);
            }
            array[i] = value;
            if (flag) {
                break;
            }
        }

        this.backingArray = new AtomicIntegerArray(array);
    }

    private int getPacked(int index) {
        return this.backingArray.get(index >> INDEX_BITS);
    }

    /**
     * Gets the length of the array
     *
     * @return the length
     */
    public int length() {
        return this.length;
    }

    /**
     * Gets an element from the array at a given index
     *
     * @param index the index
     * @return the element
     */
    public byte get(int index) {
        return key(this.getPacked(index), index & INDEX_BITS);
    }

    /**
     * Sets an element in the array at a given index and returns the old value.
     *
     * @param index the index
     * @param value the new value
     * @return the old value
     */
    public byte getAndSet(int index, byte value) {
        boolean success = false;
        byte oldValue = 0;
        int backingIndex = index >> INDEX_BITS;
        int valueIndex = index & INDEX_MASK;
        while (!success) {
            int oldPacked = this.backingArray.get(backingIndex);
            oldValue = key(oldPacked, valueIndex);
            int newPacked = key(oldPacked, backingIndex, value);
            success = this.backingArray.compareAndSet(backingIndex, oldPacked, newPacked);
        }
        return oldValue;
    }

    /**
     * Sets the element at the given index, but only if the previous value was the expected value.
     *
     * @param index the index
     * @param expected the expected value
     * @param newValue the new value
     * @return true on success
     */
    public boolean compareAndSet(int index, byte expected, byte newValue) {
        boolean success = false;
        byte oldValue = 0;
        int backingIndex = index >> INDEX_BITS;
        int valueIndex = index & INDEX_MASK;
        while (!success) {
            int oldPacked = this.backingArray.get(backingIndex);
            oldValue = key(oldPacked, valueIndex);
            if (oldValue != expected) {
                return false;
            }
            int newPacked = key(oldPacked, backingIndex, newValue);
            success = this.backingArray.compareAndSet(backingIndex, oldPacked, newPacked);
        }
        return true;
    }

    private byte addAndGet(int index, byte delta, boolean old) {
        boolean success = false;
        byte newValue = 0;
        byte oldValue = 0;
        while (!success) {
            oldValue = this.get(index);
            newValue = (byte) (oldValue + delta);
            success = this.compareAndSet(index, oldValue, newValue);
        }
        return old ? oldValue : newValue;
    }

    /**
     * Gets an array containing all the values in the array. The returned values are
     * not guaranteed to be from the same time instant.
     *
     * If an array is provided and it is the correct length, then
     * that array will be used as the destination array.
     *
     * @param array the provided array
     * @return an array containing the values in the array
     */
    public byte[] getArray(byte[] array) {
        if (array == null || array.length != this.length) {
            array = new byte[this.length];
        }
        for (int i = 0; i < this.length; i += PACKED_VALUES) {
            int packed = this.getPacked(i);
            for (int j = 0; j < PACKED_VALUES; j++) {
                if (i + j >= this.length) {
                    break;
                }
                array[i + j] = key(packed, j);
            }
        }
        return array;
    }

    /**
     * Sets an element to the given value.
     *
     * @param index the index
     * @param value the new value
     */
    public void set(int index, byte value) {
        this.getAndSet(index, value);
    }

    /**
     * Sets an element to the given value, but the update may not happen immediately.
     *
     * @param index the index
     * @param value the new value
     */
    public void lazySet(int index, byte value) {
        this.set(index, value);
    }

    /**
     * Sets the element at the given index, but only if the previous value was the expected value. This may fail spuriously.
     *
     * @param index the index
     * @param expected the expected value
     * @param newValue the new value
     * @return true on success
     */
    public boolean weakCompareAndSet(int index, byte expected, byte newValue) {
        return this.compareAndSet(index, expected, newValue);
    }

    /**
     * Atomically adds a delta to an element, and gets the new value.
     *
     * @param index the index
     * @param delta the delta to add to the element
     * @return the new value
     */
    public byte addAndGet(int index, byte delta) {
        return this.addAndGet(index, delta, false);
    }

    /**
     * Atomically adds a delta to an element, and gets the old value.
     *
     * @param index the index
     * @param delta the delta to add to the element
     * @return the old value
     */
    public byte getAndAdd(int index, byte delta) {
        return this.addAndGet(index, delta, true);
    }

    /**
     * Atomically increments an element and returns the old value.
     *
     * @param index the index
     * @return the old value
     */
    public byte getAndIncrement(int index) {
        return this.getAndAdd(index, (byte) 1);
    }

    /**
     * Atomically decrements an element and returns the old value.
     *
     * @param index the index
     * @return the old value
     */
    public byte getAndDecrement(int index) {
        return this.getAndAdd(index, (byte) -1);
    }

    /**
     * Atomically increments an element and returns the new value.
     *
     * @param index the index
     * @return the new value
     */
    public byte incrementAndGet(int index) {
        return this.addAndGet(index, (byte) 1);
    }

    /**
     * Atomically decrements an element and returns the new value.
     *
     * @param index the index
     * @return the new value
     */
    public byte decrementAndGet(int index) {
        return this.addAndGet(index, (byte) -1);
    }

    /**
     * Gets an array containing all the values in the array.
     *
     * The returned values are not guaranteed to be from the same time instant.
     *
     * @return the array
     */
    public byte[] getArray() {
        return this.getArray(null);
    }

    /**
     * Returns a string representation of the array.
     *
     * The returned values are not guaranteed to be from the same time instant.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return Arrays.toString(this.getArray());
    }
}