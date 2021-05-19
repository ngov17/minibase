/*-------------------------------------------------------------------------
 *
 * SelectionIterator.java
 *	  AN iterator for the "SELECT" statement. Algebraically, project + select on predicate.
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/executor/iterators/SeqScanIterator.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.executor.iterators;

/**
 *
 *
 *
 * [name age ]   [c1 c2 c3]
 *
 *
 *
 * c2  = name
 */

import com.minibase.Util;
import com.minibase.access.Record;
import com.minibase.access.Schema;
import com.minibase.executor.iterators.Iterator;
import com.minibase.parser.ASTSelect;
import com.minibase.parser.Condition;
import com.minibase.storage.BufferManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectionIterator extends Iterator {

    BufferManager bm = BufferManager.BufferManager(4);
    Condition predicate;
    ArrayList<Iterator> iterators;
    ArrayList<String> table_names;   // should be same size as iterators. Each iterator is associated with a table/alias
    HashMap<String, String> schema ;// attribute -> table_name/ alias for nested queries
    HashMap<String, String> types;
    HashMap<String, ArrayList<String>> aliases;
    HashMap<String, Record> tab_map;  // table/alias => Record
    int curr_ind;   // keeps track of the current iterator
    boolean has_next;


    /**
     *
     * [it1 it2] => iters
     * [t2 t2] => relations
     * ind = 0;
     *  1    2
     * [t1 -> null, t2 -> null] => tab_map
     *
     *  curr_ind = 0
     *  boolean has_next = true;
     *

     * t1 t2       t3
     * R1 r1       k1
     * R2 r2       k2
     * R3
     *

     *
     *
     */

    public SelectionIterator(ArrayList<Iterator> iterators, ArrayList<String> table_names, ASTSelect sel) {
        this.schema = sel.schema;
        this.types = sel.types;
        this.aliases = sel.aliases;
        this.predicate = sel.cond;
        this.iterators = iterators;
        this.table_names = table_names;
        this.tab_map = new HashMap<>();
        this.curr_ind = 0;
    }

    public Record getNext () {
        select();
        return project();
    }

    private boolean emptyCheck(ArrayList<Iterator> iterators) {
        for (Iterator it: iterators) {
            if (it.isEmpty()) return true;
        }
        return false;
    }

    private void select() {
        if (curr_ind == -1) {return;}
        while (this.curr_ind < this.iterators.size()) {
            if (this.iterators.get(this.curr_ind).hasNext()) {


                tab_map.put(this.table_names.get(this.curr_ind), this.iterators.get(this.curr_ind).getNext());
                curr_ind++;
            } else {
                this.iterators.get(this.curr_ind).reset();
                curr_ind--;
                return;
            }
        }
        // Inner most iterator
        if (curr_ind == this.iterators.size()) {
            curr_ind--;

            if (this.predicate.isNull()) {
                if (!this.iterators.get(this.curr_ind).hasNext()) {this.iterators.get(this.curr_ind).reset();curr_ind--;}
                return;
            }
            if (!evaluate_cond(this.predicate)) {
                if (!this.iterators.get(this.curr_ind).hasNext()) {this.iterators.get(this.curr_ind).reset();curr_ind--;}

                if (!this.hasNext()) {
                    tab_map.clear();
                    return;
                }
                select();
            } else {
                if (!this.iterators.get(this.curr_ind).hasNext()) {this.iterators.get(this.curr_ind).reset();curr_ind--;}
                return;
            }

        }

    }

    public boolean hasNext(){
        if (curr_ind == 0) return this.iterators.get(curr_ind).hasNext();
        return curr_ind != -1;
    }

    public boolean isEmpty() {
        return emptyCheck(this.iterators);
    }

    public void reset() {
        this.curr_ind = 0;
        for (Iterator it: this.iterators) {
            it.reset();
        }
    }

    public void close() {
        for (Iterator it: this.iterators) {
            it.close();
        }
    }

    private Record project() {
        if (tab_map.isEmpty()) return null;
        Object[] vals = new Object[this.schema.size()];
        int i = 0;

        for (Map.Entry<String,String> entry : this.schema.entrySet()) {
            if (bm.getSchema(entry.getValue()) != null) {
                vals[i] = tab_map.get(entry.getValue()).values[bm.getSchema(entry.getValue()).attIndex(entry.getKey())];
                i++;

            } else {
                vals[i] = tab_map.get(entry.getValue()).values[this.aliases.get(entry.getValue()).indexOf(entry.getKey())];
                i++;
            }
        }

        Record rec = new Record(vals);
        return rec;
    }

    private boolean evaluate_cond(Condition cond) {

        if (cond.isTerminal()) {
            Object left_val = null;
            Object right_val = null;
            if (bm.getSchema(cond.l_tname) != null) {
                left_val = tab_map.get(cond.l_tname).values[bm.getSchema(cond.l_tname).attIndex(cond.left_hand)];
            } else {

                left_val = tab_map.get(cond.l_tname).values[this.aliases.get(cond.l_tname).indexOf(cond.left_hand)];
            }

            if (cond.is_att) {
                if (bm.getSchema(cond.r_tname) != null) {
                    right_val = tab_map.get(cond.r_tname).values[bm.getSchema(cond.r_tname).attIndex(cond.right_hand)];
                } else {
                    right_val = tab_map.get(cond.r_tname).values[this.aliases.get(cond.r_tname).indexOf(cond.right_hand)];
                }
            } else {
                if (cond.l_type == "int") right_val = new Integer(cond.right_hand);
                else right_val = cond.right_hand;
            }

            return Util.compare(left_val, right_val, cond.operator);
        }
        if (!cond.right_cond.isNull()) {
            if (cond.child_relation.equals("and") || cond.child_relation.equals("AND"))
                return evaluate_cond(cond.right_cond) && evaluate_cond(cond.left_cond);
            if (cond.child_relation.equals("or") || cond.child_relation.equals("OR"))
                return evaluate_cond(cond.right_cond) || evaluate_cond(cond.left_cond);
            return true;
        } else {
            return evaluate_cond(cond.left_cond);
        }
    }

}
