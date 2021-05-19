/*-------------------------------------------------------------------------
 *
 * Validator.java
 *  Validates the query, that is makes sure the tables, attributes etc exist
 *  All methods in this class return an empty string if the query is valid, otherwise
 *  a string containing the error message.
 *
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/validates/Validator.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.parser;

import com.minibase.Util;
import com.minibase.access.Schema;
import com.minibase.storage.BufferManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Validator {

    static BufferManager bm = BufferManager.BufferManager(4);

    public static String validateCreate(Schema schema) {
        if (bm.getSchema(schema.getTableName()) == null) {
            return "";
        } else { return "Table Already Exists"; }
    }

    public static String validateInsert(Object[] values, String table_name) {
        int col;
        if (bm.getSchema(table_name) == null) {
            return "Table " + table_name + " does not exist";
        } if(values.length != bm.getSchema(table_name).getAttTypes().length) {

            return "Provided " + String.valueOf(values.length) + " values. Table " + table_name + " has " +
                    String.valueOf(bm.getSchema(table_name).getAttTypes().length) + " columns";
        }  else if((col = validateType(values, table_name) )!= 0) {
            return "Column " + String.valueOf(col) + " is " + bm.getSchema(table_name).getAttTypes()[col - 1];
        } else return "";
    }

    public static String validateTable(ArrayList<String> table_names) {
        for (String table_name: table_names) {
            if (bm.getSchema(table_name) == null) {
                return "Table " + table_name + " does not exist";
            }
        }
        return "";
    }

    /**
     *
     * Recursive function to validate attributes of a select statement. A running list of attributes to be validated is kept for
     *  nested selects
     */
    public static boolean validateSelectAtts(ASTSelect sel, String[] err) {
        ArrayList<String> atts = new ArrayList<>();
        int[] validated_atts = new int[sel.att_list.size()];

        Arrays.fill(validated_atts, 1);
        int i = 0;
        if (sel.att_list.get(0).equals("*")) {
            for (String table: sel.from_list) {
                for (String att: bm.getSchema(table).getAttNames()) {
                    sel.schema.put(att, table);
                    sel.types.put(att, bm.getSchema(table).attType(att));

                }
            }
            validated_atts[i] = 0;
        } else {
            for(String att: sel.att_list) {
                for (String table: sel.from_list) {
                    if (bm.attExists(att, table)) {sel.schema.put(att, table); sel.types.put(att,bm.getSchema(table).attType(att) ); validated_atts[i] = 0;}
                }

                i++;
            }
        }

            if (sel.isTerminal()) {
                int v_sum = Arrays.stream(validated_atts).sum();
                if (v_sum == 0)  {
                    if (!sel.cond.isNull()) {
                        if (!validateCondition(sel.cond, sel, err)) return false;
                    }
                    return true;
                }
                for(i = 0; i < validated_atts.length; i++) {
                    if (validated_atts[i] == 1) {
                        err[0] = "Col " + sel.att_list.get(i) + " does not exist";
                    }
                }
               return false;

            } else {
                for (int j = 0; j < validated_atts.length; j++) {
                    if (validated_atts[j] == 1) {
                        atts.add(sel.att_list.get(j));
                    }
                }

                for (int j = 0; j <sel.children.size(); j++) {
                    ASTSelect child = (ASTSelect) sel.children.get(j);
                      validateSelectAtts(child, err);
                    ArrayList<String> cols = new ArrayList<>();
                      for (String att: child.schema.keySet()) {
                          String old_att = att;
                          att = replaceAtt(att, child.alias);
                          cols.add(att);
                          if (atts.contains(att)) {atts.remove(att);sel.schema.put(att, child.alias); sel.types.put(att, child.types.get(old_att));}
                          if (sel.att_list.get(0).equals("*")) {sel.schema.put(att, child.alias); sel.types.put(att, child.types.get(old_att));}
                      }
                      sel.createAlias(child.alias, cols);
                }

                if (atts.size() != 0) {
                    err[0] = "Col " + atts.get(0) + " does not exist";
                } else {
                    if (!sel.cond.isNull()) {
                        if (!validateCondition(sel.cond, sel, err)) return false;
                    }
                }
                return atts.size() == 0;
            }

    }

    /**
     * Validates a conditions at each level of the select AST.
     */
    private static boolean validateCondition(Condition cond, ASTSelect sel, String[] err) {

        if (cond.isTerminal()) {
            if (isAtt(sel, cond, true)) {

               if (isAtt(sel,cond, false)) {
                   if (!cond.r_type.equals(cond.l_type)) {
                       err[0] = "Types must match for " + cond.left_hand + " and " + cond.right_hand;
                       return false;
                   }
                   cond.is_att = true;
                   return true;
               }


               if (validateType(cond.right_hand, cond.l_type)) return true;
               else {
                   err[0] = "Expected " + cond.l_type + " for " + cond.left_hand;
                   return false;
               }
            }

            err[0] = cond.left_hand + " does not exist";
            return false;

        }
        if (!cond.right_cond.isNull()) {
            return validateCondition(cond.right_cond, sel, err) && validateCondition(cond.left_cond, sel, err);
        } else return validateCondition(cond.left_cond, sel, err);
    }

    private static boolean isAtt(ASTSelect sel, Condition cond, boolean left) {
        String att;
        if (left) att = cond.left_hand;
        else att = cond.right_hand;
        for (String table: sel.from_list) {
            if (bm.attExists(att, table)) {
                if (left) cond.l_tname = table; else cond.r_tname = table;
                if (left) cond.l_type = bm.getSchema(table).attType(att); else cond.r_type = bm.getSchema(table).attType(att);
                return true;
            }
        }

        for (ASTNode c: sel.children) {
            String a;
            if (c.isTerminal()) {a = att.split("\\.")[1];}
            else {
                a = att.split(".")[1];
                a = ((ASTSelect) c.getChild(0)).alias + "." + a;
            }


            //else rep = ((ASTSelect) c.children.get(0)).alias;
            if (((ASTSelect) c).schema.containsKey(a)) {
                if (left) cond.l_tname = ((ASTSelect) c).alias; else cond.r_tname = ((ASTSelect) c).alias;
                if (left) cond.l_type = ((ASTSelect) c).types.get(a); else cond.r_type = ((ASTSelect) c).types.get(a);
                return true;
            }
        }
        return false;
    }

    private static int validateType(Object[] values, String table_name) {
        String[] att_types = bm.getSchema(table_name).getAttTypes();
        for(int i = 0; i < values.length; i++) {
            String t = (String) values[i];
            if(att_types[i].equals("int")) {
                try {
                    int j = Integer.parseInt(t);
                    values[i]  = j;

                } catch (Exception e) {


                    return i + 1;
                }
            }
        }
        return 0;
    }

    private static String replaceAtt(String att, String alias) {
        if (att.contains(".")) {
            String[] split = att.split("\\.");
            return alias + "." + split[1];
        }

        return alias + "." + att;
    }

    private static boolean validateType(String att_val, String att_type) {
        if(att_type.equals("int")) {
            try {
                Integer.parseInt(att_val);
                return true;

            } catch (Exception e) {


                return false;
            }
        }
    return true;
    }

}