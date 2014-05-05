package com.abs.transfer.file;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;

import com.abs.property.PathProperties;

public final class FileFactory {
    private static final String SLASH = "/";
    private static final String DOT = ".";
    private static final int ZERO = 0;
    private static final int ONE = 1;

    private static String getFileName(URL url) {
        return url.getFile().substring(url.getFile().lastIndexOf(SLASH) + ONE);
    }

    private static String getOppositePath(String[] pathArray) {
        final StringBuilder pathBuilder = new StringBuilder();
        for (String path : pathArray) {
            pathBuilder.append(path).append(File.separatorChar);
        }
        if (pathBuilder.length() > ZERO)
            pathBuilder.delete(pathBuilder.length() - ONE, pathBuilder.length());
        return pathBuilder.toString();
    }

    private static String getFilePath(String[] pathArray, URL url, ServletContext context) throws IOException {
        return new StringBuilder().append(createDir(getOppositePath(pathArray), context)).append(File.separatorChar)
                .append(getFileName(url)).toString();
    }

    public static File createFile(String[] pathArray, URL url, ServletContext context) throws IOException {
        return create(getFilePath(pathArray, url, context));
    }

    private static String getAbsolutePath(ServletContext context) {
        return context.getRealPath("/") + PathProperties.getInstance().getOppositePath() + (File.separatorChar);
    }

    private static String createDir(String oppositePath, ServletContext context) throws IOException {
        return create(new StringBuilder(getAbsolutePath(context)).append(oppositePath).toString()).getAbsolutePath();
    }

    private static File create(String path) throws IOException {
        final File file = new File(path);
        if (!file.exists()) {
            String lastName = path.substring(path.lastIndexOf(File.separatorChar)
                    + (String.valueOf(File.separatorChar).length()));
            if (lastName.indexOf(DOT) != -1) {
                new File(path.substring(0, path.lastIndexOf(lastName))).mkdirs();
                file.createNewFile();
            } else {
                file.mkdirs();
            }
        }
        return file;
    }
}
