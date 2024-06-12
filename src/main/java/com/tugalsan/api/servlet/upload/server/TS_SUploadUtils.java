package com.tugalsan.api.servlet.upload.server;

import com.tugalsan.api.callable.client.TGS_CallableType2;
import java.nio.file.Path;

public class TS_SUploadUtils {

    public static void bind(TGS_CallableType2<Path, String, String> target_by_profile_and_filename) {
        TS_SUploadWebServlet.SYNC = new TS_LibFileUploadExecutor(target_by_profile_and_filename);
    }
}
