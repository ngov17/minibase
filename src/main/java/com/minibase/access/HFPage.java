/*-------------------------------------------------------------------------
 *
 * HFPage.java
 *	  Manages data in a page from a heap file.
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/access/HFPage.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.access;

import com.minibase.storage.Page;
import com.minibase.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

// Page.bytes => [ 0 1 0 1 1 1 1 1 ...x4096 .. ] => stores your data
//                 table

// num_att num_entries
// firstRecord
// nextRecord

class HFPage {

    private Page page;
    private int num_entries;
    private int f_lower;
    private int f_upper;
    private ArrayList<RID> l_ptrs;

    private static int curr_ptr = 0;

    public HFPage(Page page) {
        this.page = page;
        readPageHeader();
    }

    /**
     *
     *  Page header consists of
     *          - num_entries - 4 bytes, default 0
     *          - free_lower, beginning of unallocated space in a page - 4 bytes, default 12
     *          - free_upper, end of unallocated space in a page - 4 bytes, default page_size -1 ;
     *
     * @param page
     */
    public static void writePageHeader(Page page) {

        copyToPage(0, Util.intToByteArray(0), page);        // num_entries
        copyToPage(8, Util.intToByteArray(Page.PAGE_SIZE - 1), page);     // free_upper

        byte[] serialized_lptr = Util.objectToByteArray(new ArrayList<RID>());
        copyToPage(12, serialized_lptr, page);
        int f_lower = 12 + serialized_lptr.length;
        System.out.println(serialized_lptr.length);
        copyToPage(4, Util.intToByteArray(f_lower), page);       // free_lower

    }

    public RID insertRecord(Record rec) {
        byte[] serialized_record = Util.objectToByteArray(rec);

        int len = serialized_record.length - 1;
        System.out.println(serialized_record.length);
        for(int i = this.f_upper; i > this.f_upper - serialized_record.length; i--) {

            this.page.data[i] = serialized_record[len];
            len--;
        }

        this.f_upper -= serialized_record.length;
        updatePageHeader("f_upper");
        this.num_entries++;
        updatePageHeader("num_entries");
        RID rid = new RID(this.f_upper + 1, serialized_record.length);
        this.l_ptrs.add(rid);
        updateLinePointer(this.page);

        return rid;
    }

    public Record firstRecord() {
        RID rid = this.l_ptrs.get(0);
        curr_ptr = 1;
        return get(rid);
    }

    public Record nextRecord() {
        if (curr_ptr == this.l_ptrs.size()) {curr_ptr = 0; return null;}

        RID rid = this.l_ptrs.get(curr_ptr);
        curr_ptr++;
        return get(rid);
    }

    public Record get(RID rid) {
        System.out.println(rid.offset);
        System.out.println(rid.length);
        return (Record) Util.byteArrayToObject(read(rid.offset, rid.length));
    }

    private byte[] read(int offset, int length) {
        byte[] res = new byte[length];
        int ind = 0;
        for(int i = offset; i < offset + length; i++) {
            res[ind] = this.page.data[i];
            ind++;
        }
        return res;
    }

    private byte[] readLinePointer() {
        byte[] res = new byte[this.f_lower - 12];
        int ind = res.length - 1;
        for(int i = this.f_lower - 1; i > 11; i--) {
            res[ind] = this.page.data[i];
            ind--;
        }
        return res;
    }

    private void readPageHeader() {
        this.num_entries = Util.byteArrayToInt(Arrays.copyOfRange(this.page.data, 0, 4));
        this.f_lower = Util.byteArrayToInt(Arrays.copyOfRange(this.page.data, 4, 8));
        this.f_upper = Util.byteArrayToInt(Arrays.copyOfRange(this.page.data, 8, 12));
        this.l_ptrs = (ArrayList<RID>) Util.byteArrayToObject(readLinePointer());

        // testing
        System.out.println(num_entries);
        System.out.println(f_lower);
        System.out.println(f_upper);
        System.out.println(l_ptrs.size());

    }

    private void updatePageHeader(String header_type) {
        switch (header_type) {
            case "num_entries":
                copyToPage(0, Util.intToByteArray(this.num_entries));
                break;
            case "f_lower":
                copyToPage(4, Util.intToByteArray(this.f_lower));
                break;
            case "f_upper":
                copyToPage(8, Util.intToByteArray(this.f_upper));
                break;
            default:
                break;
        }
    }

    private void updateLinePointer(Page p) {
        byte[] serialized_lptr = Util.objectToByteArray(this.l_ptrs);
        copyToPage(12, serialized_lptr, this.page);
        this.f_lower = 12 + serialized_lptr.length;
        updatePageHeader("f_lower");
    }

    private static void copyToPage(int pos, byte[] data, Page page) {
        int curr = pos;
        for(byte b: data) {
            page.data[curr] = b;
            curr++;
        }
    }

    private void copyToPage(int pos, byte[] data) {
        int curr = pos;
        for(byte b: data) {
            this.page.data[curr] = b;
            curr++;
        }
    }

}

/**
 * Represents a row id to uniquely identify a row. Consists of an (offset, length) pair
 */
class RID implements Serializable {

    private static final long serialVersionUID = 1L;;

    int offset;
    int length;

    public RID(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }
}



