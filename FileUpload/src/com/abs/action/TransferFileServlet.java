package com.abs.action;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.abs.action.context.HttpServletRequestContext;

public class TransferFileServlet extends HttpServlet {

    //http://192.168.1.15/absfts/transferServlet?downloadURL=本地网站下载路径&pathArray=aaaa&pathArray=bbbb
    //pathArray：自动生成的文件夹路径 aaaa/bbbb/相应的文件
    /** */
    private static final long serialVersionUID = -3694637656501945798L;
    private static final String CLIENT_URL_PARAM_NAME = "clientURL";
    private static final int ZERO = 0;

    //从客户端抓取图片
    private HttpServletRequestContext createContext(HttpServletRequest request) throws IOException {
        return new HttpServletRequestContext(request, getServletContext());
    }

    //获得客户短的URL 注: 客户端表单需要添加一个隐藏标签
    private void setClientURL(Map<String, List<String>> formParamMaps, HttpServletRequest request) {
        request.setAttribute("clientURL", formParamMaps.remove(CLIENT_URL_PARAM_NAME).get(ZERO));
    }

    private StringBuilder getParamHTML(Map<String, List<String>> paramMaps, HttpServletRequest request) {
        StringBuilder htmlBuilder = new StringBuilder();
        for (String key : paramMaps.keySet()) {
            for (String v : paramMaps.get(key)) {
                htmlBuilder.append("<input type='hidden' name='").append(key).append("'").append("value='").append(v)
                        .append("'/></br>");
            }
        }
        return htmlBuilder;
    }

    //文件上传方式传输文件	
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        // 设置requestSErvletRequest上下文
        try {
            final HttpServletRequestContext context = this.createContext(request);
            //跳转到客户端
            StringBuilder htmlBuilder = new StringBuilder();
            this.setClientURL(context.getFormParamMap(), request);
            htmlBuilder.append(this.getParamHTML(context.getFileParamMap(), request));
            htmlBuilder.append(this.getParamHTML(context.getFormParamMap(), request));
            request.setAttribute("htmlBuilder", htmlBuilder.toString());
            request.getRequestDispatcher("/forwardPage.jsp").forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

}
