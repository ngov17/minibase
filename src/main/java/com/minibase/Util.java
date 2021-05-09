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

}