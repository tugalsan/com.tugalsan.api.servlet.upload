package com.tugalsan.api.servlet.upload.server;


import com.tugalsan.api.callable.client.TGS_CallableType3_Run;
import java.time.Duration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

abstract public class TS_SUploadExecutor implements TGS_CallableType3_Run<HttpServlet, HttpServletRequest, HttpServletResponse> {

    public Duration timeout() {
        return Duration.ofMinutes(1);
    }
}
