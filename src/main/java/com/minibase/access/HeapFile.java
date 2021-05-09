/*-------------------------------------------------------------------------
 *
 * HeapFile.java
 *	  Provides an abstraction for a heap file, which is an unordered set of
 *    records. Supports insertions, deletion, and retrieval of records.
 *    A file represents a table in this design.
 *
 *    The only module that makes calls to the buffer manager
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
import com.minibase.storage.Page;

public class HeapFile {

    static BufferManager bm;
    Page p;
    String filename;
    //ArrayList<HFPage> pages = new ArrayList<HFPage>();  TODO: Update page selection using free-space map and more

    /**
     * Opens a HeapFile
     * @param filename
     */
    public HeapFile(String filename) {
        bm = BufferManager.BufferManager(2);
        p = bm.pin(0, filename);
        this.filename = filename;

    }

    /**
     *  CLoses a heap file
     */
    public void close() {
        bm.unPin(0, this.filename);
    }



    public static void createFile(String filename, String[] att_names, String[] att_types) {
        insertMetaFile(filename, att_names, att_types);
        // create a bm,
        bm = BufferManager.BufferManager(2);

        Page p = bm.createFile(filename);
        bm.unPin(0, filename);


    }


    /**
     * Initializes meta files which are tables that contain meta data on relations
     *
     *  - meta_table - info on tables
     *  - meta_att - info on att
     *
     */
    public static void initMetaFiles() {

        // create a bm,
        bm = BufferManager.BufferManager(2);

        Page p = bm.createFile("meta_table"); // creating a file automatically pins the page
        HFPage.writePageHeader(p);  // writes the initial page header
        bm.updatePage(0, "meta_table");    // update page for file. Default page index is 0.

    }

    //public void insert(Record )

    public Record getFirstRecord() {
        HFPage p = new HFPage(bm.pin(0, filename));
        Record rec = p.firstRecord();
        bm.unPin(0, filename);

        return rec;
    }

    public Record getNextRecord() {
        HFPage p = new HFPage(bm.pin(0, filename));
        Record rec = p.nextRecord();

        bm.unPin(0, filename);

        return rec;
    }

    /**
     * Inserts a record to the meta_table table
     * A record in the meta_table table has the following attributes:
     *      -table_name
     *      -num_pages, for now this will always be 1 for each table for simplicity
     *      - num_att - int
     *      - att_data - text - "att1_name-0,att2_name-1,.." where 0: int and 1: text
     */
    static void insertMetaFile(String filename, String[] att_names, String[] att_types) {
        bm = BufferManager.BufferManager(2);
        Object[] record = new Object[4];
        record[0] = filename;
        record[1] = new Integer(1);
        record[2] = new Integer(att_names.length);
        record[3] = namesValuesToAtt_data(att_names, att_types);

        Record rec = new Record(record);

        Page p = bm.pin(0, "meta_table");
        HFPage m_page = new HFPage(p);
        m_page.insertRecord(rec);
        bm.updatePage(0, "meta_table");
    }


    private static String namesValuesToAtt_data(String[] att_names, String[] att_types) {
        StringBuilder att_data = new StringBuilder();
        for(int i = 0; i < att_names.length; i++) {
            att_data.append(att_names[i]);
            att_data.append('-');
            att_data.append(Util.encodeAttType(att_types[i]));
            att_data.append(',');
        }
        return att_data.toString();
    }

    /** TEST METHODS **/

}

class unitTestHeap {
    public static void main(String[] args) {
                switch (args[0]) {

                    case "init_meta":
                        HeapFile.initMetaFiles();
                        System.out.println("intialized files");
                        break;
                    case "insert_meta":
                        String tab_name = "test";
                        String[] att_names = {"col1", "col2"};
                        String[] att_types = {"int", "text"};
                        HeapFile.insertMetaFile(tab_name, att_names, att_types);

                        System.out.println("inserted meta records");
                        break;
                    case "test":
                        HeapFile f = new HeapFile("meta_table");
                        Record rec = f.getFirstRecord();
                        for (int i = 0; i < rec.values.length; i++) {
                            System.out.println(rec.values[i]);
                        }
                        break;
                    case "test_2":
                        HeapFile.insertMetaFile("users", new String[]{"username", "password"}, new String[]{"text", "text"});
                        HeapFile.insertMetaFile("recipients", new String[]{"col1", "u_id", "col2"}, new String[]{"int", "int", "text"});
                        HeapFile.insertMetaFile("donors", new String[]{"requests", "u_id", "col4", "col5"}, new String[]{"int", "int", "int", "text"});

                        HeapFile.insertMetaFile("donors", new String[]{"requests", "u_id"}, new String[]{"int", "int"});
                        System.out.println("inserted meta records");
                        break;
                    case "test_4":
                        f = new HeapFile("meta_table");
                        rec = f.getFirstRecord();
                        for (int i = 0; i < rec.values.length; i++) {
                            System.out.println(rec.values[i]);
                        }
                        System.exit(0);
                        rec = f.getNextRecord();
                        for (int i = 0; i < rec.values.length; i++) {
                            System.out.println(rec.values[i]);
                        }
                        rec = f.getNextRecord();
                        for (int i = 0; i < rec.values.length; i++) {
                            System.out.println(rec.values[i]);
                        }
                        rec = f.getNextRecord();
                        for (int i = 0; i < rec.values.length; i++) {
                            System.out.println(rec.values[i]);
                        }
                        rec = f.getNextRecord();
                        System.out.println(rec);
                        break;
                    case "test_5":
                        HeapFile.createFile("users", new String[]{"username", "password"}, new String[]{"text", "text"});


        }
    }
}

