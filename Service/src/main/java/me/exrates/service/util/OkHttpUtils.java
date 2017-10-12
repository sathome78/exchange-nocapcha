package me.exrates.service.util;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import me.exrates.service.exception.RestRetrievalException;
import okio.Buffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class OkHttpUtils {

    private static final Logger logger = LogManager.getLogger(OkHttpUtils.class);

    private OkHttpUtils(){

    }

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

    public static String sendGetRequest(String url, Map<String, String> params) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        params.forEach(httpUrlBuilder::addEncodedQueryParameter);

        Request request = new Request.Builder()
                .url(httpUrlBuilder.build())
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                String errorMessage = response.body().string();
                logger.error(errorMessage);
                throw new RestRetrievalException(errorMessage);
            }
            return response.body().string();
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RestRetrievalException(e.getMessage());
        }
    }

    public static String sendGetRequest(String url) {
        return sendGetRequest(url, Collections.emptyMap());
    }


}