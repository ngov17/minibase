/*-------------------------------------------------------------------------
 *
 * Util.java
 *	  Utility functions
 *
 *
 * IDENTIFICATION
 *	  src/main/java/com/minibase/storage/Util.java
 *
 *-------------------------------------------------------------------------
 */
package com.minibase.storage;

class Util {
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

        return (int)(
                (0xff & data[0]) << 24  |
                        (0xff & data[1]) << 16  |
                        (0xff & data[2]) << 8   |
                        (0xff & data[3]) << 0
        );
    }

}