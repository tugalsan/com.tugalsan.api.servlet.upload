package com.tugalsan.api.servlet.upload.server;

import module com.tugalsan.api.url;
import module com.tugalsan.api.log;
import module com.tugalsan.api.thread;
import module com.tugalsan.api.function;
import module javax.servlet.api;
import java.nio.file.*;

public class TS_SUploadUtils {

    final private static TS_Log d = TS_Log.of(TS_SUploadUtils.class);

    private TS_SUploadUtils() {

    }

    public static void bind(TS_ThreadSyncTrigger killTrigger, TGS_FuncMTU_OutTyped_In3<Path, String, String, HttpServletRequest> target_by_formField_and_fileName_and_request) {
        TS_SUploadWebServlet.warmUp(killTrigger.newChild(d.className()).newChild("bind").newChild("warmpUp"));
        TS_SUploadWebServlet.SYNC = new TS_SUploadExecutorImpl(target_by_formField_and_fileName_and_request);
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
