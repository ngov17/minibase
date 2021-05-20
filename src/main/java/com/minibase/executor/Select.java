/*-------------------------------------------------------------------------
 *
 * Select.java
 *  Executes the "SELECT" command
 *
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/executor/Select.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.executor;

import com.minibase.access.Record;
import com.minibase.executor.iterators.Iterator;
import com.minibase.executor.iterators.SelectionIterator;
import com.minibase.executor.iterators.SeqScanIterator;
import com.minibase.parser.ASTNode;
import com.minibase.parser.ASTSelect;
import com.minibase.storage.BufferManager;

import java.util.ArrayList;

public class Select implements Plan {
    ASTSelect select;

    public Select(ASTSelect select) {
        this.select = select;
    }

    public void execute() {

        Iterator sel = buildIterator(select);
        int row_count = 0;
        if (!sel.isEmpty()) {

            for (String att: select.schema.keySet()) {
                BufferManager bm = BufferManager.BufferManager(4);
                att = bm.getSchema(select.schema.get(att)).qualifiedName(att);
                System.out.print(att);
                System.out.print(" | ");
            }
            System.out.println();
            System.out.println("-------");
            while (sel.hasNext()) {
                Record r = sel.getNext();
                if (r == null) {System.out.println("here");break;};

                for (Object o : r.values){
                    System.out.print(o);
                    System.out.print(" | ");
                }
                row_count++;
                System.out.println();
            }
            System.out.println(String.valueOf("(" + row_count) + " rows)");
        } else {
            System.out.println("(0 rows)");
        }
        sel.close();

    }

    private Iterator buildIterator(ASTSelect select) {
        ArrayList<Iterator> iterators = new ArrayList<>();
        if (select.isTerminal()) {
            for (String t_name : select.from_list) {

                SeqScanIterator it = new SeqScanIterator(t_name);

                iterators.add(it);
            }
            Iterator sel = new SelectionIterator(iterators, select.from_list, select);

            return sel;
        } else {
            ArrayList<String> tables = new ArrayList<>();
            for (String t_name : select.from_list) {

                SeqScanIterator it = new SeqScanIterator(t_name);

                iterators.add(it);
                tables.add(t_name);
            }
            for (ASTNode c : select.children) {
                {
                    Iterator it = buildIterator((ASTSelect) c);

                    iterators.add(it);
                    tables.add(((ASTSelect) c).alias);
                }
            }
            SelectionIterator sel = new SelectionIterator(iterators, tables, select);
            return sel;
        }

    }
}
