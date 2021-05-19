/*-------------------------------------------------------------------------
 *
 * CreateTable.java
 *  Executes "CREATE TABLE" command
 *
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/executor/CreateTable.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.executor;

import com.minibase.access.HeapFile;
import com.minibase.access.Record;
import com.minibase.parser.ASTCreate;
import com.minibase.parser.ASTInsert;

public class Insert implements Plan {

    ASTInsert plan_tree;

    public Insert(ASTInsert plan_tree) {
        this.plan_tree = plan_tree;
    }

    public void execute() {

        Record rec = new Record(this.plan_tree.getValues());
        HeapFile table = new HeapFile(plan_tree.getTableName());    // open heap file

        table.insertRecord(rec);
        table.close();  // close file

    }

}