/*-------------------------------------------------------------------------
 *
 * FileManager.java
 *	  Reads and Writes pages to files on disk. Provides an abstraction to
 *    treat a file as a collection of pages.
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/storage/file/FileManager.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.storage;

import com.minibase.Util;

import java.io.*;
import java.util.*;

public class FileManager {

    /** INSTANCE VARIABLES **/
    private File file;
    private FileInputStream f_in;
    private FileOutputStream f_out;
    private int num_pages;
    // an array list pointing to the position of each Page in the file:
    private ArrayList<Integer> page_pointers = new ArrayList<Integer>();
    private int file_end;   // eof position
    private static final String ROOT_PATH = "./data/";

    public boolean exists;

    /**
     * Constructor
     *  Opens a file manager for a given filename
     *
     * @param filename  the file to manage
     */
    public FileManager(String filename) {
        filename = ROOT_PATH + filename;
        this.file = new File(filename);
        this.exists = this.file.exists();


        if (this.exists) readHeader();

    }

    /** Getters **/
    public int getPageCount() {
        return this.num_pages;
    }

    /**
     * Reads a page from the disk
     *
     * @param page  an already allocated page object to read into
     * @param page_index    the index of the page, which is used to retrieve
     *                      the file pointer of the page in disk
     */
    public void readPage(Page page, int page_index) {


        if (page_index > this.num_pages) {
            throw new IndexOutOfBoundsException("invalid page index");
        }

        try {
            FileInputStream f_in = new FileInputStream(this.file);
            f_in.getChannel().position(this.page_pointers.get(page_index));
            f_in.read(page.data);
            f_in.close();

        } catch (Exception e) {
            System.err.println(e);
        }


    }


    /**
     * Writes a Page to Disk
     *
     * @param page  the page to write to the disk
     * @param page_index    the index of the page, which is used to retrieve
     *                      the file pointer of the page in disk
     *
     */
    public void writePage(Page page, int page_index) {
        if (page_index > this.num_pages) {
            throw new IndexOutOfBoundsException("invalid page index");
        }

        try {
            RandomAccessFile f_out = new RandomAccessFile(this.file, "rw");

            f_out.getChannel().position(this.page_pointers.get(page_index));

            f_out.write(page.data);
            f_out.close();

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Allocates additional pages to the file
     *
     * @param num_pages  number of pages to add
     * @return number of pages in this file after allocation
     */
    public int allocatePage(int num_pages) {

        // update on disk
        writeHeader(this.num_pages + num_pages, this.file);

        // update in memory
        this.num_pages = this.num_pages + num_pages;
        updateFileEnd();
        fillPagePointers();

        return this.num_pages;

    }

    /**
     * Creates a file with one page (default)
     *
     */
    public static void createFile(String filename) {
        try {
            File file = new File(ROOT_PATH + filename);
            file.createNewFile();
            System.out.println("Created file");
            /* File created, load header and initial page onto file */
            writeHeader(1, file);

        } catch (Exception e) {
            System.err.println(e);
        }


        //readHeader();
    }

    /**
     * Deletes the file and consequently all pages in it
     */
    public void deleteFile() {
        close();
        try {
            this.file.delete();
            //System.out.println("File Deleted, creating new FileManager with the new file will create a new " +
                    //"file with default contents");
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     *  Closes all system resources associated with the input and output streams
     */
    public void close() {
        try {
            this.f_in.close();
            this.f_out.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }


    /** HELPER FUNCTIONS **/

    /* Utility */
    private void updateFileEnd() {
        // file_end is position 4 + (num_pages - 1) * page_size
        this.file_end = 4 + this.num_pages * Page.PAGE_SIZE;
    }

    /* File Header Management */

    /**
     * Writes a header with info in parameters
     *
     * @param num_pages
     */
    private static void writeHeader(int num_pages, File f) {
        byte[] n_pages = Util.intToByteArray(num_pages);



        // write to file
        try {
            System.out.println("Writing header...");
            FileOutputStream f_out = new FileOutputStream(f);
            f_out.getChannel().position(0);    // set position to start of file
            f_out.write(n_pages);
            f_out.flush();
            f_out.close();

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Reads file header
     */
    private void readHeader() {
        byte[] n_pages = new byte[4];

        // read header
        try {
            FileInputStream f_in = new FileInputStream(this.file);
            f_in.read(n_pages);
            f_in.close();
        } catch (Exception e) {
            System.err.println(e);
        }

        this.num_pages = Util.byteArrayToInt(n_pages);

        updateFileEnd();

        // fill page pointers
        fillPagePointers();

    }

    private void fillPagePointers() {
        int curr_len = this.page_pointers.size();
        for(int i = curr_len; i < this.num_pages; i++) {
            this.page_pointers.add(4 + i*Page.PAGE_SIZE);
        }
    }
}

class unitTestFile {
    public static void main(String[] args) {

    }
}
