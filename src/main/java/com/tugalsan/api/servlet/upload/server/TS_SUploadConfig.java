package com.tugalsan.api.servlet.upload.server;

import com.tugalsan.api.file.json.server.TS_FileJsonUtils;
import com.tugalsan.api.file.server.TS_DirectoryUtils;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.file.txt.server.TS_FileTxtUtils;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.union.client.TGS_UnionExcuse;
import com.tugalsan.api.function.client.maythrow.checkedexceptions.TGS_FuncMTCEUtils;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Properties;

public class TS_SUploadConfig implements Serializable {

    final private static TS_Log d = TS_Log.of(TS_SUploadConfig.class);
    final private static boolean DEFAULT_ENABLE_TIMEOUT = true;

    private TS_SUploadConfig() {//DTO
    }

    private TS_SUploadConfig(boolean enableTimeout) {
        this.enableTimeout = enableTimeout;
    }
    public boolean enableTimeout;

    public static TS_SUploadConfig of() {
        return new TS_SUploadConfig(DEFAULT_ENABLE_TIMEOUT);
    }

    public static TS_SUploadConfig of(boolean enableTimeout) {
        return new TS_SUploadConfig(enableTimeout);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TS_SUploadConfig other = (TS_SUploadConfig) obj;
        return this.enableTimeout == other.enableTimeout;
    }

    @Override
    public String toString() {
        return TS_SUploadConfig.class.getSimpleName() + "{" + "enableTimeout=" + enableTimeout + '}';
    }

    public Properties toProps() {
        var prop = new Properties();
        prop.put("enableTimeout", enableTimeout);
        return prop;
    }

    public void loadProps(Properties prop) {
        enableTimeout = (Boolean) prop.getOrDefault("enableTimeout", enableTimeout);
    }

    public static TGS_UnionExcuse<TS_SUploadConfig> of(Path dir, String appName) {
        TS_DirectoryUtils.assureExists(dir);
        var filePath = dir.resolve(TS_SUploadConfig.class.getSimpleName() + "." + appName + ".json");
        d.cr("of", filePath);

        if (!TS_FileUtils.isExistFile(filePath)) {
            TS_DirectoryUtils.createDirectoriesIfNotExists(filePath.getParent());
            var tmp = TS_SUploadConfig.of();
            var jsonString = TS_FileJsonUtils.toJSON(tmp, true);
            TS_FileJsonUtils.toFile(jsonString, filePath, false, true);
        }

        var jsonString = TGS_FuncMTCEUtils.call(() -> TS_FileTxtUtils.toString(filePath), e -> {
            d.ct("of", e);
            d.cr("of", "writing default file");
            var tmp = TS_SUploadConfig.of();
            var jsonString0 = TS_FileJsonUtils.toJSON(tmp, true);
            TS_FileTxtUtils.toFile(jsonString0, filePath, false);
            return jsonString0;
        });
        d.ci("of", jsonString);

        return TS_FileJsonUtils.toObject(jsonString, TS_SUploadConfig.class);
    }
}
