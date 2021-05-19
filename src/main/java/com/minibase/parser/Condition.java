/*-------------------------------------------------------------------------
 *
 * Condition.java
 *      Binary tree that handles nested conditions. Contains only a single node if one condition.
 *    This will be evaluated on a tuple during execution.

 * IDENTIFICATION
 *	  src/main/java/com/minibase/parser/Condition.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.parser;

public class Condition {


    public Condition left_cond;
    public Condition right_cond;
    public String child_relation; // can be (and) or (or)

    public String left_hand;
    public String right_hand;
    public String operator;



    public String l_tname;
    public String r_tname;
    public String l_type;
    public String r_type;
    public boolean is_att = false;

    public Condition(){};

    public Condition(String l_hand, String r_hand, String operator) {this.initCond(l_hand, r_hand, operator);}

    public void initCond(String l_hand, String r_hand, String operator) {
        this.left_hand = l_hand;
        this.right_hand = r_hand;
        this.operator = operator;
    }

    public void setCond(Condition left_cond, Condition right_cond, String child_relation) {
        this.left_cond = left_cond;
        this.right_cond = right_cond;
        this.child_relation = child_relation;
    }

    public boolean isNull() {
        return isTerminal() && this.left_hand == null;
    }


    public boolean isTerminal() {
        return this.left_cond == null && this.right_cond == null;
    }
}
