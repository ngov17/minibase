/*-------------------------------------------------------------------------
 *
 * Util.java
 *	  Utility functions
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/Util.java
 *
 *-------------------------------------------------------------------------
 */

package com.minibase;

import com.minibase.access.Record;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class Util {

    public String[] operators = {"<", ">", "=", "!=", "<=", ">="};

    public static byte[] intToByteArray(int data) {
        return new byte[] {
                (byte)((data >> 24) & 0xff),
                (byte)((data >> 16) & 0xff),
                (byte)((data >> 8) & 0xff),
                (byte)((data >> 0) & 0xff),
        };
    }

    public static int byteArrayToInt(byte[] data) {
        if (data == null || data.length != 4) return 0x0;

        return ((0xff & data[0]) << 24  |
                        (0xff & data[1]) << 16  |
                        (0xff & data[2]) << 8   |
                        (0xff & data[3]) << 0);
    }

    /**
     * Convert a object into stream of bytes.
     *
     * @param rec
     *            An object.
     * @return stream of bytes
     */
    public static byte[] objectToByteArray(Object rec) {
        byte[] stream = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos);) {
            oos.writeObject(rec);
            stream = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stream;
    }

    /**
     * Convert stream of bytes to an object.
     *
     * @param stream
     *            byte array
     * @return Record
     */
    public static Object byteArrayToObject(byte[] stream) {
        Object rec = null;

        try (ByteArrayInputStream bais = new ByteArrayInputStream(stream);
             ObjectInputStream ois = new ObjectInputStream(bais);) {
            rec = ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return rec;
    }

    /**
     *
     * @param att_type
     *          "int" or "text"
     */
    public static char encodeAttType(String att_type) {
        switch (att_type) {
            case "int":
                return '0';
            case "text":
                return '1';
            default:
                throw new IllegalArgumentException("att_type must be int or text only");
        }

    }

    public static String decodeAttType(String att_type) {
        switch (att_type) {
            case "0":
                return "int";
            case "1":
                return "text";
            default:
                throw new IllegalArgumentException("att_type must be '0' or text '1' ");
        }

    }

    public static String[] objectArrayToStringArray(Object[] obj_arr) {
        String[] res = new String[obj_arr.length];
        for(int i = 0; i < obj_arr.length; i++) {
            res[i] = (String) obj_arr[i];
        }

        return res;
    }

    public static boolean compare(Object l_hand, Object r_hand, String operator) {
        if (l_hand instanceof String) {
            String l = (String) l_hand;
            String r = (String ) r_hand;
            return compare(l, r, operator);
        } else {
            Integer l = (Integer) l_hand;
            Integer r = (Integer) r_hand;
            return compare(l, r, operator);
        }
    }

    public static boolean compare(String l_hand, String r_hand, String operator) {

        switch (operator) {
            case "=":
                return l_hand.equals(r_hand);
            case "!=":
                return !l_hand.equals(r_hand);
            case ">":
                return l_hand.compareTo(r_hand) > 0;
            case "<":
                return l_hand.compareTo(r_hand) < 0;
            case ">=":
                return l_hand.compareTo(r_hand) >= 0;
            case "<=":
                return l_hand.compareTo(r_hand) <= 0;
        }
        return false;
    }

    public static boolean compare(Integer l_hand, Integer r_hand, String operator) {

        switch (operator) {
            case "=":
                return l_hand.equals(r_hand);
            case "!=":
                return !l_hand.equals(r_hand);
            case ">":
                return l_hand.compareTo(r_hand) > 0;
            case "<":
                return l_hand.compareTo(r_hand) < 0;
            case ">=":
                return l_hand.compareTo(r_hand) >= 0;
            case "<=":
                return l_hand.compareTo(r_hand) <= 0;
        }
        return false;
    }

}