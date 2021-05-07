package database;
import java.util.*;
public class Parser {

    static ArrayList<Table> database = new ArrayList<Table>();

    static void parse(String s) {
        String[] tokens = s.split(" ");
        String table_name;
        Table table;
        int ind;
        switch(tokens[0]) {
            case "create":
                table_name = tokens[1];
                String[] columns = new String[tokens.length - 4];
                int j = 0;
                for(int i = 3; i < tokens.length - 1; i++) {
                    columns[j] = tokens[i];
                    j++;
                }
                Table t = new Table(columns, table_name);
                database.add(t);
                System.out.println("TABLE " + table_name + " successfully created");
                break;
            case "insert":
                table_name = tokens[2];
                String[][] data = parse_insert(tokens);

                // error checking
                if(data.length == 2 && data[0][0].equals("ERROR")) {
                    System.out.println(data[1][0]);
                    break;
                }

                ind = get_table_ind(table_name);
                if(ind == -1){System.out.println("Table " + table_name + " does not exist"); return;}
                table = database.get(ind);

                System.out.println(table.insert(data[0], data[1]));
                break;
            case "print":
                table_name = tokens[1];
                ind = get_table_ind(table_name);
                if(ind == -1){System.out.println("Table " + table_name + " does not exist"); return;}
                table = database.get(ind);
                table.print();
                break;
            case "show":
                System.out.println("TABLE NAME");
                System.out.println("---");
                for(int i = 0; i < database.size(); i++) {
                    System.out.println(database.get(i).table_name);
                    System.out.println("---");
                }
                break;
            case "select":

        }
        return;
    }

    // HELPER FUNCTIONS
    static int get_table_ind(String table_name) {
        int i = 0;
        for (Table table: database) {
            if (table.table_name.equals(table_name)) return i;
            i++;
        }
        return -1;
    }

    static String[][] parse_insert(String[] tokens) {

        int length = 0;
        int i = 4;
        String col = tokens[i];
        while(!col.equals(")")) {
            length++;
            i++;
            col = tokens[i];
        }
        String[] cols = Arrays.copyOfRange(tokens, 4, 4 + length);
        i = 7 + length;
        col = tokens[i];
        int length_val = 0;
        while(!col.equals(")")) {
            length_val++;
            i++;
            col = tokens[i];
        }

        // error checking
        if (length_val < length) {
            String[][] ret = {{"ERROR"}, {"INSERT has more target columns than expressions"}};
            return ret;

        } else if (length_val > length) {
            String[][] ret = {{"ERROR"}, {"INSERT has more expressions than target columns"}};
            return ret;
        }


        String[] vals = Arrays.copyOfRange(tokens, 7 + length, 7 + 2*length);

        String[][] ret = {vals, cols};

        return ret;


    }
}