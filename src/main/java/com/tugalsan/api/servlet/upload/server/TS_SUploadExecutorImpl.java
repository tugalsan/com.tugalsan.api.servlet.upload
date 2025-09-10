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
import com.tugalsan.api.function.client.maythrowexceptions.checked.*;
import com.tugalsan.api.function.client.maythrowexceptions.unchecked.*;
//import java.util.List;
//import org.apache.commons.fileupload2.core.FileItem;
//import org.apache.commons.fileupload2.core.DiskFileItemFactory;
//import org.apache.commons.fileupload2.javax.JavaxFileCleaner;
//import org.apache.commons.fileupload2.javax.JavaxServletFileUpload;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;

/*can be renamed from TS_LibFileUploadExecutor to TS_SUploadExecutor_ImplementationWithProfile */
public class TS_SUploadExecutorImpl extends TS_SUploadExecutor {

    final private static TS_Log d = TS_Log.of(TS_SUploadExecutorImpl.class);

    protected TS_SUploadExecutorImpl(TGS_FuncMTU_OutTyped_In3<Path, String, String, HttpServletRequest> target_by_profile_and_filename_and_request) {
        this.target_by_profile_and_filename_and_request = target_by_profile_and_filename_and_request;
    }
    final public TGS_FuncMTU_OutTyped_In3<Path, String, String, HttpServletRequest> target_by_profile_and_filename_and_request;

    @WebListener
//    public static class ApacheFileCleanerCleanup extends JavaxFileCleaner {
    public static class ApacheFileCleanerCleanup extends FileCleanerCleanup {

    }

    @Override
    public void run(HttpServlet servlet, HttpServletRequest rq, HttpServletResponse rs) {
        TGS_FuncMTCUtils.run(() -> {
            //CHECK IF REQUEST IS MULTIPART
//            if (!JavaxServletFileUpload.isMultipartContent(rq)) {
            if (!ServletFileUpload.isMultipartContent(rq)) {
                println(rs, "USER_NOT_MULTIPART");
                return;
            }

            //GETING ITEMS
            //WARNING: Dont touch request before this, like execution getParameter or such!
//            var fileFactory = DiskFileItemFactory.builder().get();
//            var fileUpload = new JavaxServletFileUpload(fileFactory);
//            List<FileItem> fileItems = fileUpload.parseRequest(rq);
            var fileItems = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(rq);

            //DEBUG
            if (d.infoEnable) {
                if (fileItems.isEmpty()) {
                    d.ce("run", "items.isEmpty()");
                }
                fileItems.forEach(item -> {
                    TGS_FuncMTCUtils.run(() -> {
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
                    }, e -> {
                        d.ct("upload.forEach", e);
                        println(rs, e.getMessage());
                    });
                });
            }
            //GETTING PROFILE OBJECT
            var profile = fileItems.stream().filter(item -> item.isFormField()).findFirst().orElse(null);
            if (profile == null) {
                println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_PROFILE_NULL());
                return;
            }
            d.ci("run", "profile", "selected");

            //GETTING PROFILE VALUE
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

            //GETING SOURCEFILE OBJECT
            var sourceFile = fileItems.stream().filter(item -> !item.isFormField()).findFirst().orElse(null);
            if (sourceFile == null) {
                println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_SOURCEFILE_NULL());
                return;
            }
            d.ci("run", "sourceFile", "selected");

            //GETING SOURCEFILE NAME
            var sourceFileName = sourceFile.getName();
            if (sourceFileName == null) {
                println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_SOURCEFILENAME_NULL());
                return;
            }
            d.ci("run", "sourceFileName", sourceFileName);

            //COMPILING TARGET FILE
            var pathFileTarget = target_by_profile_and_filename_and_request.call(profileValue, sourceFileName, rq);
            d.ci("run", "pathFileTarget", pathFileTarget, "profileValue", profileValue, "sourceFileName", sourceFileName);
            if (pathFileTarget == null) {
                println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_TARGETCOMPILED_NULL());
                return;
            }
            d.ci("run", "pathFileTarget", pathFileTarget);

            //CHECK IF TARGET FILE ALREADY EXISTS
            if (TS_FileUtils.isExistFile(pathFileTarget)) {
                if (TS_FileUtils.isExistFile(pathFileTarget)) {
                    println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_TARGETFILE_EXISTS());
                    return;
                }
            }

            //SAVE FILE
            TS_DirectoryUtils.assureExists(pathFileTarget.getParent());
            TS_FileUtils.createFile(pathFileTarget);
//            sourceFile.write(pathFileTarget);
            sourceFile.write(pathFileTarget.toFile());

            //SEND SUCCESSFULL FLAG
            rs.setStatus(HttpServletResponse.SC_CREATED);
            println(rs, TGS_SUploadUtils.RESULT_UPLOAD_USER_SUCCESS());
        }, e -> {
            d.ct("upload", e);
            println(rs, e.getMessage());
        }
        );
    }

    private static void println(HttpServletResponse rs, String msg) {
        TGS_FuncMTCUtils.run(() -> {
            d.cr("println", msg);
            rs.getWriter().println(msg);
        }, e -> TGS_FuncMTU.empty.run());
    }

}
