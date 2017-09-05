package com.zeroq6.common.ftp;


import com.jcraft.jsch.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * sftp客户端，支持并发调用，线程结束自动释放资源
 */

/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
public class SftpClient implements FileTransferServiceApi {

    private final static ThreadLocal<JSch> J_SCH_THREAD_LOCAL = new ThreadLocal<JSch>();
    private final static ThreadLocal<Session> SESSION_THREAD_LOCAL = new ThreadLocal<Session>();
    private final static ThreadLocal<ChannelSftp> CHANNEL_SFTP_THREAD_LOCAL = new ThreadLocal<ChannelSftp>();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String idRsaPath;


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

    public String getIdRsaPath() {
        return idRsaPath;
    }

    public void setIdRsaPath(String idRsaPath) {
        this.idRsaPath = idRsaPath;
        try {
            getjSch().addIdentity(this.idRsaPath);
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    public SftpClient() {
    }

    /**
     * 私钥，密码任选其一
     * idRsaPath不为空则使用私钥，否则使用密码
     *
     * @param host
     * @param port
     * @param username
     * @param password
     * @param idRsaPath
     */
    public SftpClient(String host, Integer port, String username, String password, String idRsaPath) {
        this.host = host;
        this.port = port;
        this.username = username;
        if (null == idRsaPath || idRsaPath.trim().length() == 0) {
            this.password = password;
        } else {
            this.setIdRsaPath(idRsaPath);
        }
    }


    /**
     * 传入远程文件src（不能是文件夹），本地路径des写入规则同put方法远程文件规则
     *
     * @param src
     * @param des
     * @throws SftpException
     * @see #put(String, String)
     */
    @Override
    public void get(String src, String des) throws SftpException {
        try {
            des = new File(des).getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getSftpChannel().get(src, des);
    }


    /**
     * 本地文件src（不能是目录）；远程路径des是文件夹则放入该文件夹下，文件名为原始文件名；否则视为文件路径，文件名为'/'最后字符串（文件路径对应的文件夹必须存在）
     *
     * @param src
     * @param des
     * @throws SftpException
     */
    @Override
    public void put(String src, String des) throws SftpException {
        try {
            File f = new File(src);
            src = new File(src).getCanonicalPath();
            if (!f.exists() || f.isDirectory()) {
                throw new RuntimeException("本地" + src + "不存在或不是文件");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getSftpChannel().put(src, des);
    }


    @Override
    public void mkdir(String path) throws SftpException {
        getSftpChannel().mkdir(path);
    }

    @Override
    public void rm(String path) throws SftpException {
        getSftpChannel().rm(path);
    }

    /**
     * 只能删除空文件夹，可以使用命令通道/bin/rm -rf <PATH>，但是不安全，不提供
     *
     * @param path
     * @throws SftpException
     */
    @Override
    public void rmdir(String path) throws SftpException {
        getSftpChannel().rmdir(path);
    }

    @Override
    public void rename(String oldPath, String newPath) throws SftpException {
        getSftpChannel().rename(oldPath, newPath);
    }


    @Override
    public List<String> ls(String path) throws SftpException {
        List<String> re = new ArrayList<String>();
        Vector<ChannelSftp.LsEntry> result = getSftpChannel().ls(path);
        for (ChannelSftp.LsEntry entry : result) {
            re.add(entry.getFilename());
        }
        return re;
    }


    /**
     * 非接口相关方法
     */

    /**
     * 返回文件信息
     *
     * @param remote
     * @return
     * @throws SftpException
     */
    public SftpATTRS lstat(String remote) throws SftpException {
        return getSftpChannel().lstat(remote);
    }


    /**
     * 执行命令，返回输出结果
     *
     * @param command
     * @return
     */
    public String execute(String command) {
        StringBuffer sb = new StringBuffer();
        ChannelExec execChannel = null;
        try {
            execChannel = (ChannelExec) getSession().openChannel("exec");
            execChannel.setCommand(command);

            //
            execChannel.setErrStream(System.err);
            InputStream in = execChannel.getInputStream();
            //
            execChannel.connect();
            //
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    sb.append(new String(tmp, 0, i));
                }
                if (execChannel.isClosed()) {
                    if (in.available() > 0) {
                        continue;
                    }
                    sb.append("exit-status: " + execChannel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != execChannel) {
                execChannel.disconnect();
            }
        }
        return sb.toString();
    }


    /**
     * 获取连接资源相关
     */

    /**
     * 获取可用sftp通道
     *
     * @return
     */
    private ChannelSftp getSftpChannel() {
        try {
            ChannelSftp threadChannelSftp = CHANNEL_SFTP_THREAD_LOCAL.get();
            if (null == threadChannelSftp || !threadChannelSftp.isConnected()) {
                final ChannelSftp channelSftp = (ChannelSftp) getSession().openChannel("sftp");
                final Thread targetThread = Thread.currentThread();
                channelSftp.connect();
                // 保存
                CHANNEL_SFTP_THREAD_LOCAL.set(channelSftp);
                // 释放资源
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            targetThread.join();
                        } catch (Exception e) {
                            logger.error("加入线程阻塞失败, ", e);
                        } finally {
                            try {
                                channelSftp.disconnect();
                            } catch (Exception e) {
                                logger.error("关闭sftp管道失败, ", e);
                            }
                        }
                    }
                }.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return CHANNEL_SFTP_THREAD_LOCAL.get();
    }

    /**
     * 获取可用session
     *
     * @return
     */
    private Session getSession() {
        try {
            Session threadSession = SESSION_THREAD_LOCAL.get();
            if (null == threadSession || !threadSession.isConnected()) {
                final Session session = getjSch().getSession(username, host, port);
                final Thread targetThread = Thread.currentThread();
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                if (null != password) {
                    session.setPassword(password);
                }
                session.setDaemonThread(true);
                session.setServerAliveInterval(30 * 1000); // 毫秒
                session.setServerAliveCountMax(Integer.MAX_VALUE);
                session.connect();
                // 保存
                SESSION_THREAD_LOCAL.set(session);
                // 释放资源
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            targetThread.join();
                        } catch (Exception e) {
                            logger.error("加入线程阻塞失败, ", e);
                        } finally {
                            try {
                                session.disconnect();
                            } catch (Exception e) {
                                logger.error("断开会话失败, ", e);
                            }
                        }
                    }
                }.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return SESSION_THREAD_LOCAL.get();
    }

    private JSch getjSch() {
        if (null == J_SCH_THREAD_LOCAL.get()) {
            J_SCH_THREAD_LOCAL.set(new JSch());
        }
        return J_SCH_THREAD_LOCAL.get();
    }


}
