package com.abs.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public final class IOUtils {
    private static final int BUFF = 1024;
    private static final int STREAM_END = -1;
    private static final int OFF = 0;

    private static boolean isEnd(int offset) {
        return offset == STREAM_END;
    }

    public static OutputStream createOutputStream(File file) throws IOException {
        return new FileOutputStream(file);
    }

    public static InputStream createInputStream(URL url) throws IOException {
        return url.openStream();
    }

    private static int getOffset(byte[] bytes, InputStream in) throws IOException {
        return in.read(bytes);
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        final byte[] bytes = getBuffByte();
        while (Boolean.TRUE) {
            final int offset = getOffset(bytes, in);
            if (!isEnd(offset)) {
                out.write(bytes, OFF, offset);
            } else {
                break;
            }
        }
    }

    private static byte[] getBuffByte() {
        return new byte[BUFF];
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ex) {
            }
        }
    }
}
