module com.tugalsan.api.servlet.upload {
    requires javax.servlet.api;
    requires com.tugalsan.api.runnable;
    requires com.tugalsan.api.callable;
    requires com.tugalsan.api.log;
    requires com.tugalsan.api.file;
    requires com.tugalsan.api.thread;
    requires com.tugalsan.api.file.json;
    requires com.tugalsan.api.file.txt;
    requires com.tugalsan.api.unsafe;
    exports com.tugalsan.api.servlet.upload.client;
    exports com.tugalsan.api.servlet.upload.server;
}
