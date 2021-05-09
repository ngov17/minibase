package com.minibase;
public class Node {

    int num_rows;
    String[] values;

    public Node(int num_rows) {
        this.num_rows = num_rows;
        this.values = new String[this.num_rows];
    }

}