package com.tugalsan.api.servlet.upload.server;

import com.tugalsan.api.callable.client.TGS_CallableType3;
import com.tugalsan.api.url.client.TGS_Url;
import java.nio.file.Path;
import javax.servlet.http.HttpServletRequest;

public class TS_SUploadUtils {

    public static void bind(TGS_CallableType3<Path, String, String, HttpServletRequest> target_by_formField_and_fileName_and_request) {
        TS_SUploadWebServlet.SYNC = new TS_LibFileUploadExecutor(target_by_formField_and_fileName_and_request);
    }

    public static TGS_Url url(HttpServletRequest request) {
        var requestURL = new StringBuilder(request.getRequestURL().toString());
        var queryString = request.getQueryString();
        if (queryString == null) {
            return TGS_Url.of(requestURL.toString());
        }
        return TGS_Url.of(requestURL.append('?').append(queryString).toString());
    }
}
