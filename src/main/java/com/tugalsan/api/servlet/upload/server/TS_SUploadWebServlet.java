package com.tugalsan.api.servlet.upload.server;

import module com.tugalsan.api.file;
import module com.tugalsan.api.log;
import module com.tugalsan.api.function;
import module com.tugalsan.api.thread;
import module javax.servlet.api;
import module com.tugalsan.api.servlet.upload;
import java.nio.file.*;

@WebServlet("/" + TGS_SUploadUtils.LOC_NAME)//AS IN "/u"
@MultipartConfig(//for TS_LibFileUploadUtils.upload that uses Apache.commons
        fileSizeThreshold = 1024 * 1024 * TS_SUploadWebServlet.UPLOAD_MB_LIMIT_MEMORY,
        maxFileSize = 1024 * 1024 * TS_SUploadWebServlet.UPLOAD_MB_LIMIT_FILE,
        maxRequestSize = 1024 * 1024 * TS_SUploadWebServlet.UPLOAD_MB_LIMIT_REQUESTBALL,
        location = "/" + TGS_SUploadUtils.LOC_NAME//means C:/bin/tomcat/home/work/Catalina/localhost/spi-xxx/upload (do create it when implementing)
)
public class TS_SUploadWebServlet extends HttpServlet {

    public static volatile TS_SUploadExecutor SYNC = null;
    final public static int UPLOAD_MB_LIMIT_MEMORY = 10;
    final public static int UPLOAD_MB_LIMIT_FILE = 25;
    final public static int UPLOAD_MB_LIMIT_REQUESTBALL = 50;
    final private static TS_Log d = TS_Log.of(false, TS_SUploadWebServlet.class);
    private static volatile TS_ThreadSyncTrigger killTrigger = null;
    public static volatile TS_SUploadConfig config = TS_SUploadConfig.of();

    public static void warmUp(TS_ThreadSyncTrigger killTrigger) {
        TS_SUploadWebServlet.killTrigger = killTrigger;
    }

    @Override
    public void doGet(HttpServletRequest rq, HttpServletResponse rs) {
        call(this, rq, rs);
    }

    @Override
    protected void doPost(HttpServletRequest rq, HttpServletResponse rs) {
        call(this, rq, rs);
    }

    public static void call(HttpServlet servlet, HttpServletRequest rq, HttpServletResponse rs) {
        TGS_FuncMTCUtils.run(() -> {
            //PREREQUEST - CREATE SAVE DIR
            var pathDirApp = Path.of(rq.getServletContext().getRealPath(""));
            var pathDirSave = pathDirApp.resolve(TGS_SUploadUtils.LOC_NAME);//TS_TomcatPathUtils.getPathTomcat().resolve("work").resolve("Catalina").resolve("localhost").resolve(appName).resolve(TGS_SUploadUtils.LOC_NAME);
            TS_DirectoryUtils.assureExists(pathDirSave);

            var servletPack = SYNC;
            if (servletPack != null) {
                if (config.enableTimeout) {
                    var await = TS_ThreadAsyncAwait.runUntil(killTrigger.newChild(d.className()), servletPack.timeout(), exe -> {
                        TGS_FuncMTCUtils.run(() -> {
                            servletPack.run(servlet, rq, rs);
                        }, e -> d.ct("call.await", e));
                    });
                    if (await.timeout()) {
                        var errMsg = "ERROR(AWAIT) timeout";
                        d.ce("call", errMsg);
                        return;
                    }
                    if (await.hasError()) {
                        d.ce("call", "ERROR(AWAIT)", await.exceptionIfFailed().get().getMessage());
                        return;
                    }
                } else {
                    TGS_FuncMTCUtils.run(() -> {
                        servletPack.run(servlet, rq, rs);
                    }, e -> d.ct("call", e));
                }
                d.ci("call", "executed", "config.enableTimeout", config.enableTimeout);
                return;
            }
        }, e -> d.ct("call", e));
    }
}
