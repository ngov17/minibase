/*-------------------------------------------------------------------------
 *
 * Iterator.java
 *	  An abstract class that defines an iterator for query execution.
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/executor/iterators/Iterator.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.executor.iterators;

import com.minibase.access.Record;

public abstract class Iterator {

    // TODO: add non-abstract methods

    /**
     * Abstract Methods
     **/

    public abstract void close();

    public abstract Record getNext();

    public abstract boolean hasNext();

    public abstract boolean isEmpty();

    /** Resets the iterator**/
    public abstract void reset();

}