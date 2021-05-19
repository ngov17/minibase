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
import com.minibase.*;
import com.minibase.access.Schema;


/**
 * The buffer manager is implemented as a Singleton class, so every buffer manager instance contains the same buffer
 */
public class BufferManager {

    /** the single instance of BufferManager **/
    private static BufferManager bm = null;

    private int pool_size;  // total number of pages the buffer can hold

    private Buffer buffer;

    /**
     * Constructor
     *  Opens a buffer manager for a given filename. Private because BufferManager is Singleton
     *
     * @param pool_size  number of pages the buffer can hold
     */
    private BufferManager(int pool_size) {
        this.pool_size = pool_size;
        buffer = new Buffer(pool_size);
    }

    public static BufferManager BufferManager(int pool_size) {
        // To ensure only one instance is created
        if (bm == null)
        {
            bm = new BufferManager(pool_size);
        }
        return bm;
    }

    public Page pin(int page_index, String filename) {
        int ind = pinPage(page_index, filename);
        return this.buffer.get(ind).page;
    }

    /**
     * Returns buffer index of requested page, -1 if file doesn't exist or any other error
     *
     * @param page_index    the index of the page
     * @param filename
     */
    private int pinPage(int page_index, String filename) {
        // check if page exists in buffer
        int ind = this.buffer.findPage(page_index, filename);

        if (ind == -1)  {
            // Page does not exist in buffer
            FileManager fm = new FileManager(filename);
            if (!fm.exists) { return -1; }
            Page page = new Page();
            try {
                fm.readPage(page, page_index);
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

        unPin(page_index, filename);
    }

    public Page createFile(String filename) {
        int ind = loadNewFile(filename);
        return this.buffer.get(ind).page;
    }

    public ArrayList<Schema> getSchemas() {
        return new ArrayList<>(this.buffer.schemas.values());
    }

    public Schema getSchema(String table_name) {
        return this.buffer.schemas.get(table_name);
    }

    public boolean attExists(String att, String table_name) {
        return getSchema(table_name).attIndex(att) != -1 || att.equals("*");
    }


    /**
     * loads schemas to buffer
     */
    public void loadSchemas(HashMap<String, Schema> schemas) {
        this.buffer.schemas = schemas;
    }


    /**
     * Creates a new file and loads initial page to the buffer and pins page
     * @param filename
     * @return buffer index of page loaded
     */
    private int loadNewFile(String filename) {
        FileManager.createFile(filename);
        return pinPage(0, filename);
    }
}

/**
 * Represents a buffer, which is a list of PageTuples
 * Also contains meta data information
 */
class Buffer {

    private ArrayList<PageTuple> buffer = new ArrayList<PageTuple>();

    private ArrayList<Integer> replace = new ArrayList<Integer>();  // list of buffer indices that can be replaced

    public HashMap<String, Schema> schemas = new HashMap<>();


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
        if (this.buffer.size() > this.length) {return replace(tuple);}

        this.buffer.add(tuple);
        return buffer.size() - 1;

    }

    /** TODO: **/
    /* 2 [] [0 1], [p1] [1], [p1] [0 1], [p1 p2] [0],  [p3 p2], [] -> deal with buffer replacement problems later.. */

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
        //System.out.println("PIN");
        replace.remove(new Integer(ind));
    }

    public void unpin(int ind) {
        replace.add(ind);
    }

    /**
     * An approximate LRU Buffer Replacement Strategy:
     *          A list (this.replace) maintains a list of all buffers indices that can be replaced
     *          Initially, all indices in the buffer are assumed to be available for replacement
     *          Every time a page is pinned, it is removed from the replace list.
     *          When it is unpinned, it is added back to the replace list
     *          If the buffer needs to replace a page, it removes the page associated with the first buffer index
     *              in the replace list
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
        BufferManager bm = BufferManager.BufferManager(2);
        switch (args[0]) {
            case "create_copy":
                System.out.println("Creating file test");

                Page p = bm.createFile("test");
                //bm.unPin(0, "test");
                byte[] t = Util.intToByteArray(8908);
                System.out.println("copying data to page");
                for(int i = 0; i < 4; i++) {
                    p.data[i] = t[i];
                }
                bm.updatePage(0, "test");
                break;
            case "test":
                System.out.println("testing");
                Page x = bm.pin(0, "test");

                for(int i = 0; i < 4; i++) {
                    System.out.println(x.data[i]);
                }

                t = Util.intToByteArray(11103);
                System.out.println("copying data to page");
                for(int i = 0; i < 4; i++) {
                    x.data[i] = t[i];
                    System.out.println(x.data[i]);
                }
                bm.updatePage(0, "test");
        }
    }
}