/*-------------------------------------------------------------------------
 *
 * ASTInsert.java

 * IDENTIFICATION
 *	  src/main/java/com/minibase/parser/ASTInsert.java
 *
 *-------------------------------------------------------------------------
 */

package com.minibase.parser;

import com.minibase.access.Schema;
import java.util.HashMap;



public class ASTInsert extends ASTNode {

    private Object[] values;
    private String table_name;

    public ASTInsert(Object[] values, String table_name) {
        this.values = values;
        this.table_name = table_name;
        this.id = "INSERT";
    }

    public Object[] getValues() {
        return values;
    }

    public String getTableName() {
        return this.table_name;
    }
}