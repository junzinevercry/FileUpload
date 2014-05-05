package com.abs.action.context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.abs.property.PathProperties;

@SuppressWarnings("rawtypes")
public class File {
    private Map<String, InputStream> fileStreamMap = null;
    private Map<String, List<String>> fileParameterMap = null;
    private Map<String, List<String>> formParameterMap = null;
    private HttpServletRequest request = null;
    private static int MAX_MEMORY_SIZE = 10240000 * 2;
    private static int MAX_REQUEST_SIZE = 10240000 * 2;
    private static int MAX_FILE_SIZE = 10240000 * 2;
    private List<String> fileNameList = null;
    private String folderPath = null;
    private java.io.File temporaryFile = null;
    private List<String> oppositePathList;
    private boolean isSave = false;
    private static final String TEMPORARY_FILE = "/temporaryFile";
    private static final String SEPARATOR = java.io.File.separator;
    private static final String OPPOSITE_PATH = "oppositePath";
    private static final int ZERO = 0;
    private static final String PATH_ARRAY = "pathArray";

    public File(HttpServletRequest req, ServletContext context) throws FileUploadException, IOException {
        request = req;
        fileParameterMap = new HashMap<String, List<String>>();
        fileStreamMap = new HashMap<String, InputStream>();
        formParameterMap = new HashMap<String, List<String>>();
        folderPath = context.getRealPath("/") + PathProperties.getInstance().getOppositePath();
        fileNameList = new ArrayList<String>();
        initFile();
        oppositePathList = formParameterMap.get(OPPOSITE_PATH);
        if (oppositePathList != null) {
            for (String oppositePath : oppositePathList) {
                folderPath += SEPARATOR + oppositePath;
            }
        }
        folderPath += SEPARATOR;
        java.io.File file = new java.io.File(folderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        this.saveFile();
        //writeToSWF();
        clearFileNameList();
    }

    private void initFile() throws FileUploadException, IOException {
        List items = getFileUploadItems();
        Iterator iter = items.iterator();
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            if (item.isFormField()) {
                processFormField(item);
            } else {
                processUploadedFile(item);
            }
        }
    }

    Map<String, List<String>> getFileParamMap() {
        return fileParameterMap;
    }

    Map<String, List<String>> getFormParamMap() {
        return formParameterMap;
    }

    private List getFileUploadItems() throws FileUploadException {
        return initServletFileUpload().parseRequest(request);
    }

    private ServletFileUpload initServletFileUpload() {
        ServletFileUpload upload = new ServletFileUpload(initDiskFileItemFactory());
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);
        return upload;
    }

    private FileItemFactory initDiskFileItemFactory() {
        return new DiskFileItemFactory(MAX_MEMORY_SIZE, temporaryFile);
    }

    private void processFormField(FileItem item) throws UnsupportedEncodingException {
        String fieldName = item.getFieldName();
        if (formParameterMap.containsKey(fieldName)) {
            formParameterMap.get(fieldName).add(new String(item.getString().getBytes("ISO-8859-1"), "UTF-8"));
        } else {
            final List<String> parameterValueList = new ArrayList<String>();
            parameterValueList.add(new String(item.getString().getBytes("ISO-8859-1"), "UTF-8"));
            formParameterMap.put(fieldName, parameterValueList);
        }
    }

    private void processUploadedFile(FileItem item) throws IOException {
        if (item.getSize() == 0) {
            return;
        }
        final String fileName = item.getName();
        final String generatedFileName = getSystemAllotedFileName(fileName);
        fileStreamMap.put(generatedFileName, item.getInputStream());
        fileParameterMap.put(item.getFieldName(), Arrays.asList(new String[] { fileName, generatedFileName }));
        fileNameList.add(generatedFileName);
    }

    private void createFolders() {
        temporaryFile = new java.io.File(folderPath + TEMPORARY_FILE);
        if (!temporaryFile.exists()) {
            temporaryFile.mkdirs();
        }
    }

    private String getSystemAllotedFileName(String fileName) {
        final String newFileName = String.valueOf(UUID.randomUUID()
                + fileName.substring(fileName.lastIndexOf("."), fileName.length()));
        return newFileName;
    }

    private static void makeRemoveDir(ServletContext context) {
        String movePath = context.getRealPath("/") + PathProperties.getInstance().getProperty("move_path") + SEPARATOR;
        java.io.File movedir = new java.io.File(movePath);
        if (!movedir.exists()) {
            movedir.mkdirs();
        }
    }

    private static void moveFile(final String code, String oppositePath, ServletContext context) throws IOException {
        String movePath = context.getRealPath("/") + PathProperties.getInstance().getProperty("move_path") + SEPARATOR;
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(java.io.File dir, String name) {
                if (name.contains(code)) {
                    return true;
                }
                return false;
            }
        };

        java.io.File[] removeFileList = new java.io.File(context.getRealPath("/")
                + PathProperties.getInstance().getOppositePath() + oppositePath).listFiles(filter);
        if (removeFileList == null) {
            return;
        }
        for (java.io.File removeFile : removeFileList) {
            InputStream in = new FileInputStream(removeFile);
            OutputStream out = new FileOutputStream(new java.io.File(movePath + removeFile.getName()));
            try {
                IOUtils.copy(in, out);
            } finally {
                try {
                    IOUtils.closeQuietly(out);
                } finally {
                    IOUtils.closeQuietly(in);

                }
                removeFile.delete();
            }
        }
    }

    public static void removeFile(String code, String oppositePath, ServletContext context) throws IOException {
        makeRemoveDir(context);
        oppositePath += "/" + code.substring(0, code.lastIndexOf("/"));
        code = code.substring(code.lastIndexOf("/") + 1);
        moveFile(code, oppositePath, context);
    }

    private void clearFileNameList() {
        fileNameList.clear();
    }

    private void saveFile() throws IOException {
        if (isSave) {
            return;
        }
        final String oppositePath = formParameterMap.remove(PATH_ARRAY).get(ZERO);
        folderPath = folderPath + oppositePath;
        this.createFolders();
        for (String fileName : fileNameList) {
            final StringBuilder filePathBuilder = new StringBuilder();
            filePathBuilder.append(folderPath).append(SEPARATOR).append(fileName);
            final String filePath = filePathBuilder.toString();
            final InputStream in = new BufferedInputStream(fileStreamMap.get(fileName));
            final OutputStream out = new BufferedOutputStream(new FileOutputStream(new java.io.File(filePath)));
            try {
                IOUtils.copy(in, out);
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
                fileStreamMap.remove(fileName);
            }
        }
        isSave = true;
    }

    void destroy() {
        fileParameterMap = null;
        folderPath = null;
        for (String fileName : fileNameList) {
            IOUtils.closeQuietly(fileStreamMap.get(fileName));
        }
        fileNameList = null;
        fileStreamMap = null;
        this.delTemporaryFile(temporaryFile);
    }

    /**
     * @param file
     */
    private void delTemporaryFile(java.io.File file) {
        if (file.isDirectory()) {
            java.io.File[] delFileArray = file.listFiles();
            for (java.io.File delFile : delFileArray) {
                this.delTemporaryFile(delFile);
            }
        } else {
            file.delete();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.destroy();
    }
}
