package com.abs.util;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class URLConnectionDemo {
    //    public static void main(String[] args) {
    //        String sourceURL = "http://192.168.1.154:8090/uploadfile/convertswf/researcherInfo_4655.swf";
    //        String targetURL = "http://192.168.1.210/absfts/transferServlet";
    //        String[] pathArray = new String[] { "ctims", "resource" };
    //        sendGet(sourceURL, targetURL, Arrays.asList(pathArray));
    //    }

    public static void sendGet(String sourceURL, String targetURL, List<String> oppositePathList) {
        try {
            StringBuilder urlbuilder = new StringBuilder(targetURL).append("?downloadURL=").append(sourceURL);
            for (int i = 0, n = oppositePathList.size(); i < n; i++) {
                urlbuilder.append("&pathArray=").append(oppositePathList.get(i));
            }
            System.out.println("===" + urlbuilder.toString());
            URL realURL = new URL(urlbuilder.toString());
            URLConnection conn = realURL.openConnection();
            conn.connect();
            System.out.println(conn.getHeaderFields());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
