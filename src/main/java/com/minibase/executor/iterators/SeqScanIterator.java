/*-------------------------------------------------------------------------
 *
 * SeqScanIterator.java
 *	  A sequential scan iterator. Uses a heap file underneath to retrieve records.
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/executor/iterators/SeqScanIterator.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.executor.iterators;

import com.minibase.access.HeapFile;
import com.minibase.access.Record;
import com.minibase.executor.iterators.Iterator;

public class SeqScanIterator extends Iterator {

    private HeapFile file;

    public SeqScanIterator(String filename) {
        this.file = new HeapFile(filename);
    }

    public Record getNext() {
        return this.file.getNextRecord();
    }

    public boolean hasNext() {
        return this.file.hasNext();
    }

    public boolean isEmpty() {
        return this.file.isEmpty();
    }

    public void reset() {
        this.file.reset();
    }

    public void close() {
        this.file.close();
    }

}

