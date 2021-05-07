/*-------------------------------------------------------------------------
 *
 * BufferManager.java
 *	  Manages the shared database buffer
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/storage/BufferManager.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.storage;

import java.util.*;

public class BufferManager {

    private int pool_size;  // total number of pages the buffer can hold

    public Buffer buffer = new Buffer(pool_size);

    /**
     * Constructor
     *  Opens a buffer manager for a given filename
     *
     * @param pool_size  number of pages the buffer can hold
     */
    public BufferManager(int pool_size) {
        this.pool_size = pool_size;
    }

    /**
     * Returns buffer index of requested page, -1 if file doesn't exist or any other error
     *
     * @param page_index    the index of the page
     * @param filename
     */
    public int pinPage(int page_index, String filename) {
        // check if page exists in buffer
        int ind = this.buffer.findPage(page_index, filename);

        if (ind == -1)  {
            // Page does not exist in buffer
            FileManager fm = new FileManager(filename);
            if (!fm.exists) { fm.close(); return -1; }
            Page page = new Page();
            try {
                fm.readPage(page, page_index);
                fm.close();
                ind = this.buffer.add(page, page_index, filename);
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        /* Increment Pin Count */
        PageTuple tuple = this.buffer.get(ind);
        tuple.pin_count++;
        this.buffer.pin(ind);

        return ind;
    }

    public void unPin(int page_index, String filename) {
        int ind = this.buffer.findPage(page_index, filename);

        if (ind == -1) throw new IllegalArgumentException("page specified by filename and page_index not found in memory");

        PageTuple tuple = this.buffer.get(ind);
        if (tuple.pin_count > 0) tuple.pin_count--;
        if (tuple.pin_count == 0) this.buffer.unpin(ind);
    }

    public void updatePage(int page_index, String filename) {
        int ind = this.buffer.findPage(page_index, filename);
        Page p = this.buffer.get(ind).page;

        FileManager fm = new FileManager(filename);
        fm.writePage(p, page_index);
        fm.close();

        unPin(page_index, filename);
    }

    /**
     * Creates a new file and loads initial page to the buffer and pins page
     * @param filename
     * @return buffer index of page loaded
     */
    public int loadNewFile(String filename) {
        FileManager fm = new FileManager(filename);

        fm.createFile();

        fm.close();

        return pinPage(0, filename);
    }
}

/**
 * Represents a buffer, which is a list of PageTuples
 */
class Buffer {

    private ArrayList<PageTuple> buffer = new ArrayList<PageTuple>();

    private ArrayList<Integer> replace = new ArrayList<Integer>();  // list of buffer indices that can be replaced


    public int length;

    public Buffer(int length) {
        this.length = length;

        for (int i = 0; i < this.length; i++) {
            replace.add(i);
        }
    }

    public int add(Page p, int page_index, String filename) {
        PageTuple tuple = new PageTuple(p, page_index, filename);
        /* If the buffer is full, use a buffer replacement strategy */
        if (this.buffer.size() > this.length) return replace(tuple);
        this.buffer.add(tuple);

        return buffer.size() - 1;

    }

    public PageTuple get(int ind) {
        return this.buffer.get(ind);
    }

    public int findPage(int page_index, String filename) {
        int ind = 0;
        for(PageTuple t: this.buffer) {
            if (t.page_index == page_index && t.filename.equals(filename)) return ind;
            ind++;
        }

        return -1;
    }

    public void pin(int ind) {
        replace.remove(new Integer(ind));
    }

    public void unpin(int ind) {
        replace.add(ind);
    }

    /**
     * An LRU buffer replacement strategy based on replacement list
     * @param tuple  the PageTuple to insert (and replace with the first element)
     * @return  the buffer index of the inserted tuple, which is 0 always
     */
    private int replace(PageTuple tuple) {
        int ind = replace.remove(0);
        this.buffer.set(ind, tuple);
        return 0;
    }
}

class PageTuple {
    public Page page;
    public int page_index;
    public String filename;

    public int pin_count = 0;

    public PageTuple(Page page, int page_index, String filename) {
        this.page = page;
        this.page_index = page_index;
        this.filename = filename;
    }
}

class unitTestBuffer {
    public static void main(String[] args) {
        BufferManager bm = new BufferManager(2);
        int p_ind = bm.loadNewFile("test"); // should pin page
        //int p_ind = bm.pinPage(0, "test");
        Page p = bm.buffer.get(p_ind).page;
        byte[] test_b = Util.intToByteArray(10561);
        for (int i = 0; i < 4; i++) {
            p.data[i] = test_b[i];
            System.out.println(test_b[i]);
        }
        // should unpin page
        bm.updatePage(0, "test");
        System.out.println("-------");
        // read page (eg: seq scan)
        int y_ind = bm.pinPage(0, "test");
        Page y = bm.buffer.get(y_ind).page;
        for (int i = 0; i < 4; i++) {
            System.out.println(y.data[i]);
        }
        bm.unPin(0, "test");

    }
}