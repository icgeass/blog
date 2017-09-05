package com.zeroq6.common.storage;

import java.io.File;
import java.io.InputStream;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public interface ICloudStorageServiceApi {
    String uploadFile(File fileSource, String fileKey) throws Exception;

    String uploadStreamFile(InputStream is, String fileKey) throws Exception;

    String downCloudFile(String cloudUrl, String localPath) throws Exception;
}
