package com.zeroq6.blog.operate.domain;

import java.util.List;
import java.util.Map;

/**
 * @author
 * @date 2018/8/14
 */
public class BackupConfigDomain {


    // 执行命令的存储目录
    private String baseDir;

    private Map<String, String> cmdMap;

    private List<String> backupFolders;

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public Map<String, String> getCmdMap() {
        return cmdMap;
    }

    public void setCmdMap(Map<String, String> cmdMap) {
        this.cmdMap = cmdMap;
    }

    public List<String> getBackupFolders() {
        return backupFolders;
    }

    public void setBackupFolders(List<String> backupFolders) {
        this.backupFolders = backupFolders;
    }
}
