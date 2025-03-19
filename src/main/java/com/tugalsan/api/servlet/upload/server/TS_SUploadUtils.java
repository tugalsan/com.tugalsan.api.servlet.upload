package com.tugalsan.api.servlet.upload.server;

import com.tugalsan.api.function.client.maythrow.uncheckedexceptions.TGS_FuncMTUCE_OutTyped_In3;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.url.client.TGS_Url;
import java.nio.file.Path;
import javax.servlet.http.HttpServletRequest;

public class TS_SUploadUtils {

    final private static TS_Log d = TS_Log.of(TS_SUploadUtils.class);

    private TS_SUploadUtils() {

    }

    public static void bind(TS_ThreadSyncTrigger killTrigger, TGS_FuncMTUCE_OutTyped_In3<Path, String, String, HttpServletRequest> target_by_formField_and_fileName_and_request) {
        TS_SUploadWebServlet.warmUp(killTrigger.newChild(d.className).newChild("bind").newChild("warmpUp"));
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
