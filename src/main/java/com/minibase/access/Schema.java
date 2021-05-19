/*-------------------------------------------------------------------------
 *
 * Schema.java
 *	  Represents the schema of a table.
 *    Includes
 *    - table name
 *    - att names and types
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/access/Schema.java
 *
 *-------------------------------------------------------------------------
 */

package com.minibase.access;

public class Schema {
    private String table_name;

    private String[] att_names;
    private String[] att_types;
    private String[] att_names_qualified;

    public Schema(String table_name, String[] att_names, String[] att_types) {
        this.att_names = att_names;
        this.att_names_qualified = new String[att_names.length];
        this.att_types = att_types;
        this.table_name = table_name;
        fill_qualified();
    }

    public String[] getAttNames() {
        return this.att_names;
    }

    public String[] getQualifiedAttNames() {
        return this.att_names_qualified;
    }

    public String[] getAttTypes() {
        return this.att_types;
    }

    public String getTableName() {
        return this.table_name;
    }

    public int attIndex(String att_name) {
            for (int i = 0; i < att_names.length; i++) {
                if (att_names[i].equals(att_name) | att_names_qualified[i].equals(att_name)) return i;
            }
            return -1;
    }


    public String attType(String att_name) {
        int ind = attIndex(att_name);
        return  this.att_types[ind];
    }

    public String qualifiedName(String att_name) {
        return this.att_names_qualified[attIndex(att_name)];
    }


    /**
     * Prints the schema
     *
     */
    public void printSchema() {
        System.out.println("-------------------------------------------------------------");
        System.out.println("                               " + this.table_name);
        //System.out.println("ATT NAMES");
        print(0);
        //System.out.println("ATT Values");
        print(1);

    }

    private void fill_qualified() {
        for(int i = 0; i < this.att_names.length; i++) {
            this.att_names_qualified[i] = this.table_name + "." + this.att_names[i];
        }
    }

    private void print(int type) {
        String[] arr;
        switch(type) {
            case 0:
                arr = this.att_names;
                break;
            default:
                arr = this.att_types;
        }
        for(int i = 0; i < arr.length; i++) {
            System.out.print(" " + arr[i] + " | ");
        }
    }
}
