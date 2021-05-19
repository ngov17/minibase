/*-------------------------------------------------------------------------
 *
 * HeapFile.java
 *	  Provides an abstraction for a heap file, which is an unordered set of
 *    records. Supports insertions, deletion, and retrieval of records.
 *    A file represents a table in this design.
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/access/HeapFile.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.access;

import java.util.*;


import com.minibase.Util;
import com.minibase.storage.BufferManager;
import com.minibase.storage.FileManager;
import com.minibase.storage.Page;

public class HeapFile {

    static BufferManager bm = BufferManager.BufferManager(4);
    Page p;
    HFPage hp;
    String filename;
    private boolean read_only = true;
    //ArrayList<HFPage> pages = new ArrayList<HFPage>();  TODO: Update page selection and insertion using free-space map

    /**
     * Opens a HeapFile
     * @param filename
     */
    public HeapFile(String filename) {
        this.p = bm.pin(0, filename);
        this.hp = new HFPage(this.p);
        this.filename = filename;
    }

    /**
     *  Closes a heap file
     */
    public void close() {
        if(read_only) {
            bm.unPin(0, this.filename);
        } else {

            bm.updatePage(0, this.filename);
        }
    }

    public static void createFile(Schema s) {
        Meta.insertMetaFile(s);

        Page p = bm.createFile(s.getTableName());
        HFPage.writePageHeader(p);
        // update meta data in buffer (memory)
        Meta.loadMeta();
        bm.updatePage(0, s.getTableName());    // update page for file. Default page index is 0.

    }

    public static void deleteFile(String filename) {
        FileManager fm = new FileManager(filename);
        fm.deleteFile();
        fm.close();
    }

    public Record getFirstRecord() {
        //HFPage hp = new HFPage(this.p);
        Record rec = hp.firstRecord();

        return rec;
    }

    public Record getNextRecord() {
        //HFPage hp = new HFPage(this.p);
        Record rec = hp.nextRecord();

        return rec;
    }

    public RID insertRecord(Record rec) {
        read_only = false;
        //HFPage hp = new HFPage(this.p);
        RID rid = hp.insertRecord(rec);
        return rid;
    }

    public Record getRecord(RID rid) {
        //HFPage hp = new HFPage(this.p);
        Record rec = hp.get(rid);
        return rec;
    }

    public boolean isEmpty() {
        //HFPage hp = new HFPage(this.p);
        return hp.isEmpty();
    }

    public boolean hasNext() {
        //HFPage hp = new HFPage(this.p);
        return hp.hasNext();
    }

    public void reset() {
        //HFPage hp = new HFPage(this.p);
        hp.reset();
    }
}

class unitTestHeap {
    public static void main(String[] args) {
                switch (args[0]) {

                    case "init_meta":
                        Meta.initMetaFiles();
                        System.out.println("intialized files");
                        break;


        }
    }
}

