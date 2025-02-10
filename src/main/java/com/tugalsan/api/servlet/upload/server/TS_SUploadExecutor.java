package com.tugalsan.api.servlet.upload.server;


import com.tugalsan.api.function.client.maythrow.uncheckedexceptions.TGS_FuncMTUCE_In3;
import java.time.Duration;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

abstract public class TS_SUploadExecutor implements TGS_FuncMTUCE_In3<HttpServlet, HttpServletRequest, HttpServletResponse> {

    public Duration timeout() {
        return Duration.ofMinutes(1);
    }
}
