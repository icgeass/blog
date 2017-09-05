package com.zeroq6.common.ftp;

import java.util.List;

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public interface FileTransferServiceApi {

    void put(String src, String des) throws Exception;

    void get(String src, String des) throws Exception;

    void mkdir(String path) throws Exception;

    void rm(String path) throws Exception;

    void rmdir(String path) throws Exception;

    void rename(String oldPath, String newPath) throws Exception;

    List<String> ls(String src) throws Exception;
}
