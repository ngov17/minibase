/*-------------------------------------------------------------------------
 *
 * Meta.java
 *	  Access methods for meta files
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/access/Meta.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.access;

import com.minibase.Util;
import com.minibase.storage.BufferManager;
import com.minibase.storage.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Meta {

    static BufferManager bm = BufferManager.BufferManager(4);



    /**
     * Initializes meta files which are tables that contain meta data on relations
     *
     *  - meta_table - info on tables
     *  - meta_att - info on att
     *
     */
    public static void initMetaFiles() {

        Page p = bm.createFile("meta_table"); // creating a file automatically pins the page
        HFPage.writePageHeader(p);  // writes the initial page header
        bm.updatePage(0, "meta_table");    // update page for file. Default page index is 0.

    }

    /**
     * Inserts a record to the meta_table table
     * A record in the meta_table table has the following attributes:
     *      -table_name
     *      -num_pages, for now this will always be 1 for each table for simplicity
     *      - num_att - int
     *      - att_data - text - "att1_name-0,att2_name-1,.." where 0: int and 1: text
     */
    public static void insertMetaFile(Schema schema) {
        String filename = schema.getTableName();
        String[] att_names = schema.getAttNames();
        String[] att_types = schema.getAttTypes();

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

    /**
     *  Loads meta data to memory
     */
    public static void loadMeta() {
        HeapFile meta = new HeapFile("meta_table");



        HashMap<String, Schema> schemas = new HashMap<>();

        while (meta.hasNext()) {
            Record rec = meta.getNextRecord();
            Schema s = recordToSchema(rec);
            schemas.put(s.getTableName(), s);
            //rec = meta.getNextRecord();
        }

        bm.loadSchemas(schemas);
        meta.close();

    }

    private static Schema recordToSchema(Record rec) {
            String table_name = (String) rec.values[0];
            String[][] table_props = attToNamesValues((String) rec.values[3]) ;
            Schema s = new Schema(table_name, table_props[0], table_props[1]);

            return s;
    }


    private static String namesValuesToAtt_data(String[] att_names, String[] att_types) {
        StringBuilder att_data = new StringBuilder();
        for(int i = 0; i < att_names.length; i++) {
            att_data.append(att_names[i]);
            att_data.append('^');
            att_data.append(Util.encodeAttType(att_types[i]));
            att_data.append('~');
        }
        return att_data.toString();
    }

    private static String[][] attToNamesValues(String att_data) {

        StringTokenizer tokenizer = new StringTokenizer(att_data, "~\\^");
        int i = 0;
        int att_i = 0;
        int att_t = 0;
        String[] att_names = new String[tokenizer.countTokens()/2];
        String[] att_types = new String[tokenizer.countTokens()/2];
        while (tokenizer.hasMoreTokens())
        {
            if (i%2 == 0) {
                att_names[att_i] = tokenizer.nextToken();
                att_i++;
            } else {
                att_types[att_t] = Util.decodeAttType(tokenizer.nextToken());
                att_t++;
            }
            i++;
        }

        String[][] res = {att_names, att_types};
        return res;
    }

}

class Test {
    public static void main(String[] args) {

        switch(args[0]) {
            case "init":
                String[] n_1 = {"col1", "col2"};
                String[] t_1 = {"int", "text"};

                Schema test_1 = new Schema("test_1", n_1, t_1);

                String[] n_2 = {"col-1", "col-2", "col-3"};
                String[] t_2 = {"int", "text", "text"};

                Schema test_2 = new Schema("test_1", n_2, t_2);

                Meta.initMetaFiles();

                Meta.insertMetaFile(test_1);
                Meta.insertMetaFile(test_2);
                break;

            case "test":
                Meta.loadMeta();

                for(Schema s: Meta.bm.getSchemas()) {
                    s.printSchema();
                }




        }



    }
}