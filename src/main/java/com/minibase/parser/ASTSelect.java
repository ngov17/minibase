/*-------------------------------------------------------------------------
 *
 * ASTSelect.java

 * IDENTIFICATION
 *	  src/main/java/com/minibase/parser/ASTSelect.java
 *
 *-------------------------------------------------------------------------
 */

package com.minibase.parser;

import com.minibase.access.Schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 *
 *
 *      * select:
 *      *  while (curr_ind <= iters.length -1)
 *      *      if (iterators[curr_ind].hasNext()){
 *      *              tab_map.put(iterators[curr_ind].getNext());
 *      *              curr_ind++;
 *      *            } else {
 *      *                curr_ind--;
 *      *                if (curr_ind == -1) curr_ind = 0; // reset
 *      *            }
 *      *            if (curr_ind = iters.length - 1) {
 *      *              if (!evaluate_cond) select();
 *      *              if (!iters[curr_ind].hasNext()) cur_ind--;
 *      *              return;
 *      *             }
 *      *
 *      * hasNext:
 *      *  return curr_ind == 0 and iterators[0].hasNext()
 */


public class ASTSelect extends ASTNode {

     ArrayList<String> att_list;  // attributes to be projected
     public ArrayList<String> from_list;  // tables to select from
     public Condition cond; // the condition
     private boolean terminal = true;
     public HashMap<String, String> schema = new LinkedHashMap<>();      // attribute -> table_name/ alias for nested queries
     public HashMap<String, String> types = new LinkedHashMap<>();
     public String alias;  // the alias if it is a sub select
     public HashMap<String, ArrayList<String>> aliases = new HashMap<>();  // to get info on aliases
    //
    public ASTSelect(ArrayList<String> att_list, ArrayList<String> from_list, Condition cond) {
        this.att_list = att_list;
        this.from_list = from_list;
        this.cond = cond;
        this.id = "SELECT";
        this.children = new ArrayList<>();
    }

    public void createAlias(String a_name, ArrayList<String> columns) {

        this.aliases.put(a_name, columns);
    }

    public void addChild(ASTNode n, String alias) {
        n.parent = this;
        ASTSelect x = (ASTSelect) n;
        this.children.add(n);
        x.alias = alias;
    }
}
/**
 *
 *
 * Relational Algebra: SQL SELECT -> Project + Filter
 *
 * selIt(Iter iter, Cond c);
 *
 * SelNode s;
 * while (!s.terminal) {
 *     s = s.from_list;
 * }
 * Iter it = new selIt(seqIt(fnm),s.c)
 * while(s.parent != null) {
 *     s = s.parent;
 *     it = new SelIt(it, s.c);
 * }
 *
 *
 *
 *
 *
 * SelNode
 *    {
 *        - att_list    -> set of atts
 *        - from_list -> SelNode table_list or set of tables
 *        - cond -> condition, could be recursive or xomplex
 *        - terminal -> true if final selnode
 *        - SelNode parent = null or parent if exists
 *    }
 *
 *
 *    Condition:
 *      {
 *          - terminal: bool
 *          - left_hand
 *          - right_hand
 *          - operator
 *          - cond_l
 *          - cond_r
 *      }
 *
 * select * from table_list where cond
 * - tables exist
 *  - all of em are valid atts in context of tables OR lhs valid att and matches d_type of rhs
 *
 *    att_name = val    => type of att_name = val (relevant for int)
 *    att_name1 = att_name2 => type ott_name1 = att_name2
 *    condition and condtion
 *    condition or condition
 *    ( condition or condition )
 *    ( condition and condition )
 *    (a = b or c = d or (e = f and g = h)) and b = c
 *
 *                         c1
 *                     and  b = c
 *     (a = b or c = d) or
 *                      (e = f and g = h)
 *
 *
 *
 *
 *
 */

