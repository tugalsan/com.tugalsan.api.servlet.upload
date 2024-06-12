package com.tugalsan.api.servlet.upload.server;

import com.tugalsan.api.callable.client.TGS_CallableType3;
import java.nio.file.Path;
import javax.servlet.http.HttpServletRequest;

public class TS_SUploadUtils {

    public static void bind(TGS_CallableType3<Path, String, String, HttpServletRequest> target_by_profile_and_filename_and_request) {
        TS_SUploadWebServlet.SYNC = new TS_LibFileUploadExecutor(target_by_profile_and_filename_and_request);
    }

    public static String url(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }
}
