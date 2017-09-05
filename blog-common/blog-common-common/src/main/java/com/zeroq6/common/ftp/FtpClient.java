package com.zeroq6.common.ftp;

import org.apache.commons.net.ftp.*;
import org.apache.commons.net.util.TrustManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ftp，ftp(e)s客户端，支持并发调用，线程结束自动释放资源
 */
/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public class FtpClient implements FileTransferServiceApi {



    private final static ThreadLocal<FTPClient> FTP_CLIENT_THREAD_LOCAL = new ThreadLocal<FTPClient>();

    private final static String REMOTE_FILE_SEPARATOR = "/";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String host;
    private Integer port;
    private String username;
    private String password;
    private boolean useFtps;


    public FtpClient(){

    }

    public FtpClient(String host, Integer port, String username, String password, boolean useFtps) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.useFtps = useFtps;
    }


    /**
     * 将远端文件src写到本地
     *
     * @param src
     * @param des
     * @throws Exception
     */
    @Override
    public void get(String src, String des) throws IOException {
        src = remoteAbsolutePath(src);
        File local = new File(des);
        // 如果传入的des存在并且是文件夹则为des添加远程文件名作为绝对路径，否则des表示将要写入的文件的路径
        if (local.exists() && local.isDirectory()) {
            des = local.getCanonicalPath() + File.separator + src.substring(src.lastIndexOf(REMOTE_FILE_SEPARATOR) + REMOTE_FILE_SEPARATOR.length(), src.length());
            local = new File(des);
        }
        // 传输
        FTPClient client = getFtpClient();
        OutputStream os = null;
        try {
            os = new FileOutputStream(local);
            if (!client.retrieveFile(src, os)) {
                throw new RuntimeException(client.getReplyString());
            }
        } finally {
            if (null != os) {
                os.close();
            }
        }
    }

    /**
     * 将本地文件src写到远端des
     *
     * @param src
     * @param des
     * @throws Exception
     */
    @Override
    public void put(String src, String des) throws IOException {
        File local = new File(src);
        if (!local.exists() || local.isDirectory()) {
            throw new RuntimeException("本地" + local.getCanonicalPath() + "不存在或不是文件");
        }
        des = remoteAbsolutePath(des);
        FTPClient client = getFtpClient();
        // 如果des存在且是文件夹则des=des+原文件名
        FTPFile[] ftpFiles = client.listFiles(des);
        if(!FTPReply.isPositiveCompletion(client.getReplyCode())){ // 需要判断，因为出错和不存在都会返回空数组
            throw new RuntimeException(client.getReplyString());
        }
        if (null != ftpFiles && ftpFiles.length > 1) { //必须显示隐藏>1判断才生效
            src = local.getCanonicalPath(); //末尾不含分隔
            des = des + REMOTE_FILE_SEPARATOR + src.substring(src.lastIndexOf(File.separator) + File.separator.length(), src.length());
        }
        // 传输
        InputStream is = null;
        try {
            is = new FileInputStream(local);
            if (!client.storeFile(remoteAbsolutePath(des), is)) {
                throw new RuntimeException(client.getReplyString());
            }
        } finally {
            if (null != is) {
                is.close();
            }
        }
    }


    @Override
    public void mkdir(String path) throws IOException {
        FTPClient client = getFtpClient();
        if (!client.makeDirectory(remoteAbsolutePath(path))) {
            throw new RuntimeException(client.getReplyString());
        }

    }

    @Override
    public void rm(String path) throws IOException {
        FTPClient client = getFtpClient();
        if (!client.deleteFile(remoteAbsolutePath(path))) {
            throw new RuntimeException(client.getReplyString());
        }
    }

    @Override
    public void rmdir(String path) throws IOException {
        FTPClient client = getFtpClient();
        if (!client.removeDirectory(remoteAbsolutePath(path))) {
            throw new RuntimeException(client.getReplyString());
        }
    }

    @Override
    public void rename(String oldPath, String newPath) throws IOException {
        FTPClient client = getFtpClient();
        if (!client.rename(remoteAbsolutePath(oldPath), remoteAbsolutePath(newPath))) {
            throw new RuntimeException(client.getReplyString());
        }
    }

    @Override
    public List<String> ls(String src) throws IOException{
        List<String> re = new ArrayList<String>();
        FTPClient client = getFtpClient();
        FTPFile[] ftpFiles = client.listFiles(remoteAbsolutePath(src));
        if (!FTPReply.isPositiveCompletion(client.getReplyCode())) { // 需要判断，因为出错和不存在都会返回空数组
            throw new RuntimeException(client.getReplyString());
        }
        for(FTPFile ftpFile : ftpFiles){
            re.add(ftpFile.getName());
        }
        return re;
    }


    /**
     * 获取远端绝对路径，最后不含REMOTE_FILE_SEPARATOR
     *
     * @param path
     * @return
     */
    private String remoteAbsolutePath(String path) {
        // 兼容远端路径分隔符\
        path = path.replace("\\", REMOTE_FILE_SEPARATOR).trim();
        //  去除最后分隔符，如果同时path长度为1，则不处理
        if (path.endsWith(REMOTE_FILE_SEPARATOR) && path.length() > 1) {
            path = path.substring(0, path.length() - REMOTE_FILE_SEPARATOR.length());
        }
        // 如果
        if (path.charAt(0) == REMOTE_FILE_SEPARATOR.charAt(0)) {
            return path;
        }
        String rwd = rwd();
        if (rwd.endsWith(REMOTE_FILE_SEPARATOR)) {
            return rwd + path;
        }
        return rwd + REMOTE_FILE_SEPARATOR + path;
    }

    /**
     * 获取远端当前工作目录
     *
     * @return
     */
    private String rwd() {
        try {
            return getFtpClient().printWorkingDirectory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 一次方法中（原子操作中，事务中）不要调用两次getFtpClient，防止得到错误的返回状态字符串，如果方法执行中途断开则自行异常即可
     *
     * @return
     */
    private FTPClient getFtpClient() {
        try {
            FTPClient currentFtpClient = FTP_CLIENT_THREAD_LOCAL.get();
            if (null == currentFtpClient) {
                if (useFtps) {
                    currentFtpClient = new FTPSClient(false); // 使用显示ftps
                    ((FTPSClient) currentFtpClient).setTrustManager(TrustManagerUtils.getAcceptAllTrustManager()); //不认证服务端证书
                } else {
                    currentFtpClient = new FTPClient();
                }
            }
            final FTPClient ftpClient = currentFtpClient;
            final Thread targetThread = Thread.currentThread();
            if (!ftpClient.isAvailable()) {
                if(ftpClient.isConnected()){
                    ftpClient.disconnect();
                }
                // 连接
                ftpClient.connect(host, port);
                if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                    throw new RuntimeException(ftpClient.getReplyString());
                }
                if (!ftpClient.login(username, password)) {
                    throw new RuntimeException(ftpClient.getReplyString());
                }
                ftpClient.enterLocalPassiveMode(); // 使用消极模式，服务端监听
                ftpClient.setListHiddenFiles(true); // 列出隐藏文件（上传文件使用该选项判断给定远端路径是否是已存在的文件夹，不能修改）
                ftpClient.setKeepAlive(true);// 使用持久连接
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // 使用二进制传输
                if(useFtps){
                    ((FTPSClient) ftpClient).execPROT("P"); // 使用加密连接传输数据
                }
                // 保存
                FTP_CLIENT_THREAD_LOCAL.set(ftpClient);
                // 线程关闭时释放资源
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            targetThread.join();
                        } catch (Exception e) {
                            logger.error("加入线程阻塞失败", e);
                        } finally {
                            try{
                                ftpClient.disconnect();
                            }catch (Exception e){
                                logger.error("断开ftp连接失败", e);
                            }
                        }
                    }
                }.start();
            }
            return FTP_CLIENT_THREAD_LOCAL.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isUseFtps() {
        return useFtps;
    }

    public void setUseFtps(boolean useFtps) {
        this.useFtps = useFtps;
    }
}
