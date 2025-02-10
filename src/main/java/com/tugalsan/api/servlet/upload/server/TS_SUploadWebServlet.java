package com.tugalsan.api.servlet.upload.server;

import com.tugalsan.api.function.client.maythrow.checkedexceptions.TGS_FuncMTCEUtils;
import javax.servlet.http.*;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.servlet.upload.client.TGS_SUploadUtils;
import com.tugalsan.api.thread.server.async.await.TS_ThreadAsyncAwait;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;

import java.io.File;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

@WebServlet("/" + TGS_SUploadUtils.LOC_NAME)//AS IN "/u"
@MultipartConfig(//for TS_LibFileUploadUtils.upload that uses Apache.commons
        fileSizeThreshold = 1024 * 1024 * TS_SUploadWebServlet.UPLOAD_MB_LIMIT_MEMORY,
        maxFileSize = 1024 * 1024 * TS_SUploadWebServlet.UPLOAD_MB_LIMIT_FILE,
        maxRequestSize = 1024 * 1024 * TS_SUploadWebServlet.UPLOAD_MB_LIMIT_REQUESTBALL,
        location = "/" + TGS_SUploadUtils.LOC_NAME//means C:/bin/tomcat/home/work/Catalina/localhost/spi-xxx/upload (do create it)
)
public class TS_SUploadWebServlet extends HttpServlet {

    public static volatile TS_SUploadExecutor SYNC = null;
    final public static int UPLOAD_MB_LIMIT_MEMORY = 10;
    final public static int UPLOAD_MB_LIMIT_FILE = 25;
    final public static int UPLOAD_MB_LIMIT_REQUESTBALL = 50;
    final private static TS_Log d = TS_Log.of(false, TS_SUploadWebServlet.class);
    public static volatile TS_ThreadSyncTrigger killTrigger = null;
    public static volatile TS_SUploadConfig config = TS_SUploadConfig.of();

    @Override
    public void doGet(HttpServletRequest rq, HttpServletResponse rs) {
        call(this, rq, rs);
    }

    @Override
    protected void doPost(HttpServletRequest rq, HttpServletResponse rs) {
        call(this, rq, rs);
    }

    public static void call(HttpServlet servlet, HttpServletRequest rq, HttpServletResponse rs) {
        TGS_FuncMTCEUtils.run(() -> {
            var appPath = rq.getServletContext().getRealPath("");// constructs path of the directory to save uploaded file
            var savePath = appPath + File.separator + TGS_SUploadUtils.LOC_NAME;// creates the save directory if it does not exists
            var fileSaveDir = new File(savePath);
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdir();
            }
            //    public static TGS_UnionExcuseVoid preRequest(Path tmp) {
            //        return TS_DirectoryUtils.createDirectoriesIfNotExists(tmp);
            //    }
            //
            //    public static Path tmp(String appName) {
            //        return TS_TomcatPathUtils.getPathTomcat().resolve("work")
            //                .resolve("Catalina").resolve("localhost").resolve(appName)
            //                .resolve(TGS_SUploadUtils.LOC_NAME);
            //    }

            var servletPack = SYNC; 
            if (servletPack != null) { 
                if (config.enableTimeout) {
                    var await = TS_ThreadAsyncAwait.runUntil(killTrigger, servletPack.timeout(), exe -> {
                        TGS_FuncMTCEUtils.run(() -> {
                            servletPack.run(servlet, rq, rs);
                        }, e -> d.ct("call.await", e));
                    });
                    if (await.timeout()) {
                        var errMsg = "ERROR(AWAIT) timeout";
                        d.ce("call", errMsg);
                        return;
                    }
                    if (await.hasError()) {
                        d.ce("call", "ERROR(AWAIT)", await.exceptionIfFailed.get().getMessage());
                        return;
                    }
                } else {
                    TGS_FuncMTCEUtils.run(() -> {
                        servletPack.run(servlet, rq, rs);
                    }, e -> d.ct("call", e));
                }
                d.ci("call", "executed", "config.enableTimeout", config.enableTimeout);
                return;
            }
        }, e -> d.ct("call", e));
    }
}
