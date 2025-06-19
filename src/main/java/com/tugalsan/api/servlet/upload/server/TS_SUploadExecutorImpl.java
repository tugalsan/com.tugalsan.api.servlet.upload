package com.tugalsan.api.servlet.upload.server;

import com.tugalsan.api.file.server.TS_DirectoryUtils;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.log.server.*;
import com.tugalsan.api.servlet.upload.client.TGS_SUploadUtils;
import com.tugalsan.api.url.client.TGS_Url;
import com.tugalsan.api.url.client.TGS_UrlUtils;
import java.nio.file.Path;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import com.tugalsan.api.function.client.maythrowexceptions.checked.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.*;

/*can be renamed from TS_LibFileUploadExecutor to TS_SUploadExecutor_ImplementationWithProfile */
public class TS_SUploadExecutorImpl extends TS_SUploadExecutor {

    final private static TS_Log d = TS_Log.of(true, TS_SUploadImplDefault.class);

    protected TS_SUploadExecutorImpl(TGS_FuncMTU_OutTyped_In3<Path, String, String, HttpServletRequest> target_by_profile_and_filename_and_request) {
        this.target_by_profile_and_filename_and_request = target_by_profile_and_filename_and_request;
    }
    final public TGS_FuncMTU_OutTyped_In3<Path, String, String, HttpServletRequest> target_by_profile_and_filename_and_request;

    @WebListener
    public static class ApacheFileCleanerCleanup extends FileCleanerCleanup {

    }

    @Override
    public void run(HttpServlet servlet, HttpServletRequest rq, HttpServletResponse rs) {
        TGS_FuncMTCUtils.run(() -> {
            if (!ServletFileUpload.isMultipartContent(rq)) {
                println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_NOT_MULTIPART());
                return;
            }

//            //SINGLEPART
//            for (var part : servletUrlHandler.rq.getParts()) {
//                var fileName = extractFileName(part);
//                fileName = new File(fileName).getName();// refines the fileName in case it is an absolute path
//                part.write(savePath + File.separator + fileName);
//            }
//    private static String extractFileName(Part part) {
//        var contentDisp = part.getHeader("content-disposition");
//        var items = contentDisp.split(";");
//        for (var s : items) {
//            if (s.trim().startsWith("filename")) {
//                return s.substring(s.indexOf("=") + 2, s.length() - 1);
//            }
//        }
//        return "";
//    }
            //GETING ITEMS
            //WARNING: Dont touch request before this, like execution getParameter or such!
            var items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(rq);

            if (d.infoEnable) {
                if (items.isEmpty()) {
                    d.ce("run", "items.isEmpty()");
                }
                items.forEach(item -> {
                    d.ci("run", "items.forEach... BEGIN");
                    if (item.isFormField()) {
                        d.ci("run", "field", "name", item.getFieldName());
                        d.ci("run", "field", "value", item.getString());
                    } else {
                        d.ci("run", "file", "fieldName", item.getFieldName());
                        d.ci("run", "file", "fileName", item.getName());
                        d.ci("run", "file", "contentType", item.getContentType());
                        d.ci("run", "file", "isInMemory", item.isInMemory());
                        d.ci("run", "file", "sizeInBytes", item.getSize());
                        /*
                        Path uploadedFile = Paths.get(...);
                            item.write(uploadedFile);
                         */
                    }
                    d.ci("run", "items.forEach... END");
                });
            }

            //GETTING PROFILE
            var profile = items.stream().filter(item -> item.isFormField()).findFirst().orElse(null);
            if (profile == null) {
                println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_PROFILE_NULL());
                return;
            }
            d.ci("run", "profile", "selected");

            var profileValue = profile.getString();
            if (profileValue == null) {
                println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_PROFILEVALUE_NULL());
                return;
            }
            d.ci("run", "profileValue", profileValue);

            //CHECK PROFILE HACK
            if (TGS_UrlUtils.isHackedUrl(TGS_Url.of(profileValue))) {
                println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_PROFILEVALUE_NULL());
                return;
            }
            d.ci("run", "profileValue", "hack check successfull");

            //GETING SOURCEFILE
            var sourceFile = items.stream().filter(item -> !item.isFormField()).findFirst().orElse(null);
            if (sourceFile == null) {
                println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_SOURCEFILE_NULL());
                return;
            }
            d.ci("run", "sourceFile", "selected");

            var sourceFileName = sourceFile.getName();
            if (sourceFileName == null) {
                println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_SOURCEFILENAME_NULL());
                return;
            }
            d.ci("run", "sourceFileName", sourceFileName);

            //COMPILING TARGET FILE
            var targetFile = target_by_profile_and_filename_and_request.call(profileValue, sourceFileName, rq);
            d.ci("run", "targetFile", targetFile, "profileValue", profileValue, "sourceFileName", sourceFileName);
            if (targetFile == null) {
                println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_TARGETCOMPILED_NULL());
                return;
            }
            d.ci("run", "targetFile", targetFile);

            //ALREADY EXISTS?
            if (TS_FileUtils.isExistFile(targetFile)) {
//                if (overwrite) {
//                    TS_FileUtils.deleteFileIfExists(targetFile);
//                }
                if (TS_FileUtils.isExistFile(targetFile)) {
                    println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_TARGETFILE_EXISTS());
                    return;
                }
            }

            //CREATE DIRECTORIES
            TS_DirectoryUtils.assureExists(targetFile.getParent());

            //UPLOAD FILE
            TS_FileUtils.createFile(targetFile);
            sourceFile.write(targetFile.toFile());

            //SEND SUCCESSFULL
            rs.setStatus(HttpServletResponse.SC_CREATED);
            println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_SUCCESS());
        }, e -> {
            d.ct("upload", e);
            println(rs, e.getMessage());
        });
    }

    private static void println(HttpServletResponse rs, String msg) {
        TGS_FuncMTCUtils.run(() -> {
            d.cr("println", msg);
            rs.getWriter().println(msg);
        }, e -> TGS_FuncMTU.empty.run());
    }

}
