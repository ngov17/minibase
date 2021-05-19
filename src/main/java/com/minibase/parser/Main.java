package com.minibase.parser;


/**
 *
 *
 * users: username password email
 * donors: facility location age email
 *
 * select email from users, donors
 *  => 1 SelNode sel:
 *          * from_list = [users, donors]
 *          * att_list = [email]
 *        Check if elements in att_list are attributes in from_list
 *        Map<string, list(tables) ) map;  // att to tables it exists in
 *        for att in att_list:
 *          for table in from_list:
 *              if att exists in table:
 *                  if att not in map:
 *                      map.add(att, new list([table]))
 *                  else:
 *                      att_tab = map.get(att);
 *                      if (att_tab.size() == 1) {
 *                          att_list.remove(att);
 *                          String att_add;
 *                          for t_name in att_tab:
 *                              att_add = t_name + "." + att;
 *                              att_list.add(att_add)
 *                      } else {
 *                          att_list.add(table + "." + att);
 *
 *                      }
 *
 *
 *          -> from_list => [users,
 *  [ 1 1 1]
 *
 * select a, b, jum  from (select c, d from users, pop),  numbo
 *   [0 0 0]
 *  name age useremail a b => users
 *
 *  location no_people size c, d +> pop
 *
 *  jum, lum, gum => numbo
 *
 */

import com.minibase.access.Meta;

import java.util.ArrayList;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {
        Meta.loadMeta();
        // select col1 from (select * from users, donors);

        //String x = "select col1, col2, col3 from users, user1, user2 where ((a = 5 or  ((a = 5) and (((a = 5) and (b = 6))) or a = 5 and b = 6 or c = 6)) and (((a = 5 or (a = 4 and b = 10 or (a = 4 and f =5 and h = 9 or g = 5 and (b = 5 or c = 7 and d = 10))) and (b = 5 or c = 10)) and (b = 6))));";
        //String x = "select a, b from (select * from users), test, (select c, d, e from users1, (select a1, b1 from (select fg, ug from uduah where a = 5 or b = 8), te21, (select * from users121)));";
//      select * from (select * from users) where name = age;
        //String x = "select * from ((a"
        //System.out.println(x);
        //Meta.initMetaFiles();
        String x = "select c1, age from test, (select age, c2 from (select c1 from test), users);";
        // tab_map => [users -> [c1 . c2] f -> [. f1 .]];
        // stores the result for each table in from list
        /**
         *
         *
         *
         * Project Algorithm:
         * Object[] res = new Object[sel.schema.size()]
         *  for i, att, table_name in sel.schema:
         *      Object o = tab_map.get(table_name)[bm.getInd(table_name, att)]
         *                  OR tab_map.get(table_name)[aliases.get(table_name).indexOf(att)]
         *      res[i] = o;
         *
         * select age from users;
         */
        // sel.schema = [users.c1 -> users, users.c2 -> users, f.f1 ->f]
        x = "select age, name, u.c2 from (select * from test) as u, users where (c1 = 5 and (name = yo or age = 10));";
        // [u.c1 -> u.c2 -> u, u.f1 -> u, test.ct1-> test]
        // tab_map : [u ->selNode, test -> null]
        // att_list [
        // from_list: [test]





        try {
            System.out.println("yO");
            ASTNode s = Parser.parse(x);
            System.out.println("QUERY PARSED");
            ASTSelect n = (ASTSelect) s;
            System.out.println(n.att_list.toString());
            System.out.println(n.from_list.toString());
            ASTSelect c = (ASTSelect) n.children.get(0);
            System.out.println(c.att_list);
            System.out.println("-----------");
            ArrayList<String> atts = new ArrayList<>();
//            System.out.println(n.att_list.toString());
//            System.out.println(n.from_list.toString());
///             printTree(n.cond, 0);
            String[] err = new String[1];
//            boolean test = Validator.validateSelectAtts(n, err);
//            System.out.println(test);
//            if(!test) System.out.println(err[0]);
//
//            System.out.println("YO");
//            System.out.println(n.aliases.get(c.alias));
//            System.exit(0);


        } catch (Exception e) {
            System.out.println("excp");
            System.out.println(e.getMessage());
        }
    }

    public static void print(ASTSelect n, int level) {
        System.out.println("------");
        boolean b = n.isTerminal();
        System.out.println("LEVEL:");
        System.out.println(level);
        System.out.println("WHETHER NODE IS TERMINAL:");
        System.out.println(b);
        System.out.println("FROM LIST AND ATT LIST OF ROOT:");
        System.out.println(n.from_list.toString()); System.out.println(n.att_list.toString());
        if (!n.cond.isTerminal()) {System.out.println("COND"); printTree(n.cond, 0);}

        if(level > 0) {
            System.out.println("PARENT ATT_LIST");
            ASTSelect p = (ASTSelect) n.parent;
            System.out.println(p.att_list.toString());
        }

        if(!b) {
            for(int i = 0; i < n.children.size(); i++) {
                ASTSelect ch = (ASTSelect) n.getChild(i);
                print(ch, level + 1);
            }
        }
    }


    public static void printTree(Condition c, int level) {

        String filled = String.join("", Collections.nCopies(level*10, String.valueOf(" ")));


        System.out.println(level);
        if(c.isTerminal()) {
            System.out.println("----LEAF----" + ": " + String.valueOf(level));
            System.out.print("y" + filled);
            System.out.print(c.left_hand + " ");
            System.out.print(c.operator + " ");
            System.out.print(c.right_hand + " ");
            System.out.println();
            return;

        }
        System.out.println("--------");
        if (c.child_relation != null){ System.out.print("y" + filled); System.out.print(c.child_relation + ": " + String.valueOf(level)); System.out.println();}
        printTree(c.left_cond, level + 1);
        printTree(c.right_cond, level + 1);

    }

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$"+length+ "s", string);
    }
}
