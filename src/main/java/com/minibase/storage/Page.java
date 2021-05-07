/*-------------------------------------------------------------------------
 *
 * Page.java
 *	  A class to represent a page (or block), the smallest logical unit of storge.
 *    The page size is set to 4096 (cannot be changed)
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/storage/Page.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.storage;

public class Page {

    public static final int PAGE_SIZE = 4096;
    public byte[] data = new byte[PAGE_SIZE];

}