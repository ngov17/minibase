/*-------------------------------------------------------------------------
 *
 * Record.java
 *	  Storage format of a record in a relation.
 *
 *    A record is an array of Objects
 *    Supported types are "int" (Integer) and "text" (String)
 *
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/access/Record.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.access;

import java.io.Serializable;

public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    public Object[] values;

    public Record(Object[] values) {
        this.values = values;
    }

}