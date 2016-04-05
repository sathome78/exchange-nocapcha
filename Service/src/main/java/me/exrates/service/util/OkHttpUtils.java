package me.exrates.service.util;

import com.squareup.okhttp.Request;
import okio.Buffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class OkHttpUtils {

    private static final Logger logger = LogManager.getLogger(OkHttpUtils.class);

    public static String stringifyBody(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException ignore) {
            return null; // it will never happen
        }
    }
}