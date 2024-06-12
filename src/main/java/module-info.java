module com.tugalsan.api.servlet.upload {
    requires javax.servlet.api;
    requires commons.fileupload;
    requires com.tugalsan.api.runnable;
    requires com.tugalsan.api.callable;
    requires com.tugalsan.api.log;
    requires com.tugalsan.api.url;
    requires com.tugalsan.api.union;
    requires com.tugalsan.api.file;
    requires com.tugalsan.api.thread;
    requires com.tugalsan.api.file.json;
    requires com.tugalsan.api.file.txt;
    requires com.tugalsan.api.unsafe;
    requires com.tugalsan.api.stream;
    requires com.tugalsan.api.tomcat;
    exports com.tugalsan.api.servlet.upload.client;
    exports com.tugalsan.api.servlet.upload.server;
}
