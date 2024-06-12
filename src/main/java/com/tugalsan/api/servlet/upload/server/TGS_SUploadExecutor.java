package com.tugalsan.api.servlet.upload.server;

import com.tugalsan.api.runnable.client.*;
import java.time.Duration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

abstract public class TGS_SUploadExecutor implements TGS_RunnableType3<HttpServlet, HttpServletRequest, HttpServletResponse> {

    abstract public String name();

    public Duration timeout() {
        return Duration.ofMinutes(1);
    }
}
