package com.abs.action;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteFileServlet extends HttpServlet {
    /** */
    private static final long serialVersionUID = -2966696004724141940L;
    private static final String UPLOAD_FILE_CODE = "uploadFileCode";
    private static final String PATH_ARRAY_PARAM_NAME = "pathArray";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String uploadFileCode = request.getParameter(UPLOAD_FILE_CODE);
        String pathArray = request.getParameter(PATH_ARRAY_PARAM_NAME);
        com.abs.action.context.File.removeFile(uploadFileCode, pathArray, request.getSession().getServletContext());
    }
}
