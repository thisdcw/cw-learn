package com.cw.framework.utils;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求工具类
 *
 * @author thisdcw
 * @date 2025年04月29日 21:41
 */
public class HttpServletUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpServletUtils.class);
    private static final String LOCAL_IP = "127.0.0.1";
    private static final String AGENT_SOURCE_IP = "Agent-Source-Ip";
    private static final String LOCAL_REMOTE_HOST = "0:0:0:0:0:0:0:1";
    private static final String USER_AGENT_HTTP_HEADER = "User-Agent";
    private static final String ACCEPT_HTTP_HEADER = "Accept";

    private HttpServletUtils() {
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new RuntimeException();
        } else {
            return requestAttributes.getRequest();
        }
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new RuntimeException();
        } else {
            return requestAttributes.getResponse();
        }
    }

    public static String getCurrentClientIp() {
        HttpServletRequest request = getRequest();
        return getRequestClientIp(request);
    }

    public static String getRequestClientIp(HttpServletRequest request) {
        if (ObjectUtil.isEmpty(request)) {
            return LOCAL_IP;
        } else {
            String remoteHost = ServletUtil.getClientIP(request, AGENT_SOURCE_IP);
            return LOCAL_REMOTE_HOST.equals(remoteHost) ? LOCAL_IP : remoteHost;
        }
    }

    public static UserAgent getUserAgent(HttpServletRequest request) {
        String userAgentStr = ServletUtil.getHeaderIgnoreCase(request, USER_AGENT_HTTP_HEADER);
        return ObjectUtil.isNotEmpty(userAgentStr) ? UserAgentUtil.parse(userAgentStr) : null;
    }

    public static Boolean getNormalRequestFlag(HttpServletRequest request) {
        return request.getHeader(ACCEPT_HTTP_HEADER) == null || request.getHeader(ACCEPT_HTTP_HEADER).toLowerCase().contains("text/html");
    }

    public static Boolean getJsonRequestFlag(HttpServletRequest request) {
        return request.getHeader(ACCEPT_HTTP_HEADER) == null || request.getHeader(ACCEPT_HTTP_HEADER).toLowerCase().contains("application/json");
    }

}
