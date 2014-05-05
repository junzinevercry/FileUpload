package com.abs.action.context;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

@SuppressWarnings("rawtypes")
public class HttpServletRequestProxy extends HttpServletRequestWrapper {
    private Map<String, String> requestParamMaps;
    private Map<String, List<String>> paramMap;
    private static final int ZERO = 0;
    private static final String[] NULL_STERING_ARRAY_VALUE = new String[ZERO];

    public HttpServletRequestProxy(HttpServletRequest req, Map<String, String> requestParamMaps,
            Map<String, List<String>> paramMap) {
        super(req);
        this.requestParamMaps = requestParamMaps;
        this.paramMap = paramMap;
    }

    @Override
    public String getParameter(String paramName) {
        return requestParamMaps.get(paramName);
    }

    @Override
    public Map getParameterMap() {
        return requestParamMaps;
    }

    @Override
    public Enumeration getParameterNames() {
        return new EnumerationAdapter(requestParamMaps.keySet());
    }

    @Override
    public String[] getParameterValues(String paramName) {
        return this.paramMap.get(paramName) == null ? NULL_STERING_ARRAY_VALUE : this.paramMap.get(paramName).toArray(
                NULL_STERING_ARRAY_VALUE);
    }

    /////////////////////////////////////////////////////////////
    private class EnumerationAdapter implements Enumeration {
        private final Iterator _values;

        public EnumerationAdapter(Set setValues) {
            _values = setValues.iterator();
        }

        public boolean hasMoreElements() {
            return _values.hasNext();
        }

        public Object nextElement() {
            return _values.next();
        }
    }
    //////////////////////////////////////////////////////////////
}
