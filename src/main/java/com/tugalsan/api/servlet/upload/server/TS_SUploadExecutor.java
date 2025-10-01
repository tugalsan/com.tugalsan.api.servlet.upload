package com.tugalsan.api.servlet.upload.server;

import module com.tugalsan.api.function;
import module javax.servlet.api;
import java.time.*;

abstract public class TS_SUploadExecutor implements TGS_FuncMTU_In3<HttpServlet, HttpServletRequest, HttpServletResponse> {

    public Duration timeout() {
        return Duration.ofMinutes(1);
    }
}
