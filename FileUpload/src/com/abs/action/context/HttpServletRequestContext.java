package com.abs.action.context;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;

public class HttpServletRequestContext extends RequestContext {
    private HttpServletRequest request;
    private Map<String, List<String>> fileParam = null;
    private Map<String, List<String>> formParam = null;
    private Map<String, String> requestParamMaps = null;
    public static final boolean autoCommit = false;
    private File file = null;
    private final ServletContext context;
    private static final String MULITPART_FORM_DATA = "multipart/form-data";
    private static final String CHARACTER_CODING = "UTF-8";
    private static final String ERROR_MESSAGE = "The submited way must be multiplepart/form-data,please check your jsp page";
    private static final int ZERO = 0;
    private HttpServletRequest proxyHttpServletRequest;

    /**
     * 设置request字符编码
     * @param request
     * @throws IOException
     */
    private void setCharacterEncoding(HttpServletRequest request) throws IOException {
        request.setCharacterEncoding(CHARACTER_CODING);
    }

    /**
     * 
     * @param req
     * @param rsp
     */
    public HttpServletRequestContext(HttpServletRequest req, ServletContext context) throws IOException {
        this.context = context;
        request = req;
        this.setCharacterEncoding(request);
        this.initHttpServletRequestContext();

    }

    /**
     * 判断是否以文件上传方式(multipart/form-data)
     * @return
     */
    private boolean isFileUpload() {
        if (request.getContentType() == null)
            return false;
        return request.getContentType().contains(MULITPART_FORM_DATA);
    }

    private void initUploadPram() {
        try {
            file = new File(request, context);
            fileParam = file.getFileParamMap();
            formParam = file.getFormParamMap();
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initRequestParamMaps() {
        requestParamMaps = new HashMap<String, String>();
        for (String key : fileParam.keySet()) {
            requestParamMaps.put(key, fileParam.get(key).get(ZERO));
        }
    }

    private void initHttpServletRequestContext() {
        if (isFileUpload()) {
            initUploadPram();
        } else {
            throw new UnsupportedOperationException(ERROR_MESSAGE);
        }
        this.initRequestParamMaps();
        final Map<String, List<String>> requestMap = new HashMap<String, List<String>>();
        requestMap.putAll(fileParam);
        requestMap.putAll(formParam);
        proxyHttpServletRequest = new HttpServletRequestProxy(request, requestParamMaps, requestMap);

    }

    public HttpServletRequest getRequest() {
        return proxyHttpServletRequest;
    }

    public void destroyRequestContext() {
        request = null;
        proxyHttpServletRequest = null;
        fileParam = null;
        if (file != null) {
            file.destroy();
        }
    }

    public HttpServletRequest getServletRequest() {
        return request;
    }

    public Map<String, List<String>> getFileParamMap() {
        return fileParam;
    }

    public Map<String, List<String>> getFormParamMap() {
        return formParam;
    }
}
