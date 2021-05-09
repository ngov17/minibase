package com.minibase;
import java.util.ArrayList;

public class Table {

    String[] columns;
    String table_name;
    int num_rows;
    ArrayList<Node> rows = new ArrayList<Node>();

    public Table(String[] columns, String table_name) {
        this.columns = columns;
        this.num_rows = this.columns.length;
        this.table_name = table_name;

    }

    public String insert(String[] values, String[] cols) {
        String ret;

        Node n = new Node(this.num_rows);
        int i = 0;
        for(String col: cols) {
            int ind = get_index(col);
            if(ind == -1) {ret = "Column " + col + " does not exist"; return ret;}
            n.values[ind] = values[i];
            i++;
        }
        this.rows.add(n);
        ret = "INSERT " + String.valueOf(values.length) + " VALUES";
        return ret;

    }

    public void print() {
        final Object[][] table = new String[this.rows.size() + 2][];
        table[0] = this.columns;
        String[] delimiter = new String[this.columns.length];
        for(int i = 0; i < this.columns.length; i++) {
            delimiter[i] = "---";
        }
        table[1] = delimiter;
        for(int i = 0; i < this.rows.size(); i++) {
            table[i + 2] = this.rows.get(i).values;
        }
        String format = "";
        for(int i = 0; i < this.columns.length; i++) {
            format += "%15s";
        }
        format += "%n";
        for(final Object[] row: table) {
            System.out.format(format, row);
        }
    }

    private int get_index(String col) {
        for(int i = 0; i < this.columns.length; i++) {
            if(this.columns[i].equals(col)) {
                return i;
            }
        }
        return -1;
    }

}