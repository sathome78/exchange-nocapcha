package me.exrates.service.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by maks on 04.07.2017.
 */
public class IpUtils {

    private IpUtils() {
    }

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };

    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip.length() > 100 ? ip.substring(0, 99) : ip;
            }
        }
        return request.getRemoteAddr().length() > 99 ? request.getRemoteAddr().substring(0, 99) :  request.getRemoteAddr();
    }

    public static String getClientIpAddress(HttpServletRequest request, int maxLength) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip.length() > 100 ? ip.substring(0, 99) : ip;
            }
        }
        return request.getRemoteAddr().length() > maxLength ? request.getRemoteAddr().substring(0, maxLength) :  request.getRemoteAddr();
    }
}
