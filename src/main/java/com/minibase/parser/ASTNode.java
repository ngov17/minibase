/*-------------------------------------------------------------------------
 *
 * ASTNode.java
 *  Abstract class for Abstract Syntax Tree
 *
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/parser/ASTNode.java
 *
 *-------------------------------------------------------------------------
 */


package com.minibase.parser;

import java.util.ArrayList;

public abstract class ASTNode {

    public ArrayList<ASTNode> children;
    public ASTNode parent;
    public String table_name;
    public String id;

    public void addChild(ASTNode n) {
        n.parent = this;
        this.children.add(n);
    }

    public ASTNode getChild(int ind){return this.children.get(ind);}

    public boolean isTerminal() {return this.children.isEmpty();}
}
