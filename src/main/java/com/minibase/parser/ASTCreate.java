/*-------------------------------------------------------------------------
 *
 * ASTCreate.java

 * IDENTIFICATION
 *	  src/main/java/com/minibase/parser/ASTCreate.java
 *
 *-------------------------------------------------------------------------
 */

package com.minibase.parser;

import com.minibase.access.Schema;
import java.util.HashMap;



public class ASTCreate extends ASTNode {

    private Schema schema;


    ASTCreate(String table_name, HashMap<String, String> table_props) {
        String[] att_names = new String[table_props.size()];
        String[] att_types = new String[table_props.size()];
        int ind = 0;
        for(String att_name: table_props.keySet()) {att_names[ind] = att_name; ind++;}
        ind = 0;
        for(String att_type: table_props.values()) {att_types[ind] = att_type; ind++;}

        this.schema = new Schema(table_name, att_names, att_types);

        this.id = "CREATE";
    }

    public Schema getSchema() {
        return this.schema;
    }
}