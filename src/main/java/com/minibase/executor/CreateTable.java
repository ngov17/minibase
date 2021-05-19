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
import com.minibase.parser.ASTCreate;

public class CreateTable implements Plan {

    ASTCreate plan_tree;

    public CreateTable(ASTCreate plan_tree) {
        this.plan_tree = plan_tree;
    }

    public void execute() {
        HeapFile.createFile(plan_tree.getSchema());
    }
}
