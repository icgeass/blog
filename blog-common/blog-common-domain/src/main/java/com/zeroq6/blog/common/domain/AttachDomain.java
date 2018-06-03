package com.zeroq6.blog.common.domain;

import com.zeroq6.blog.common.base.BaseDomain;

import java.util.Date;

/**
 * @author icgeass@hotmail.com
 * @date 2017-11-10
 */
public class AttachDomain extends BaseDomain<AttachDomain> {

    private static final long serialVersionUID = 1L;

    /**
    * 附件 => attach
    */
    public AttachDomain(){
        // 默认无参构造
    }

    /**
     * 文件名
     */
    private String name;
    /**
     * 文件md5
     */
    private String md5;
    /**
     * 文件大小
     */
    private Long size;
    /**
     * 本地创建时间
     */
    private Date localCtime;
    /**
     * 本地修改时间
     */
    private Date localMtime;

    /**
     * 获取文件名 name
     *
     * @return 文件名
     */
    public String getName() {
        return name;
    }
    /**
     * 设置文件名 name
     *
     * @param name 文件名
     */
    public AttachDomain setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 获取文件md5 md5
     *
     * @return 文件md5
     */
    public String getMd5() {
        return md5;
    }
    /**
     * 设置文件md5 md5
     *
     * @param md5 文件md5
     */
    public AttachDomain setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    /**
     * 获取文件大小 size
     *
     * @return 文件大小
     */
    public Long getSize() {
        return size;
    }
    /**
     * 设置文件大小 size
     *
     * @param size 文件大小
     */
    public AttachDomain setSize(Long size) {
        this.size = size;
        return this;
    }

    /**
     * 获取本地创建时间 localCtime
     *
     * @return 本地创建时间
     */
    public Date getLocalCtime() {
        return localCtime;
    }
    /**
     * 设置本地创建时间 localCtime
     *
     * @param localCtime 本地创建时间
     */
    public AttachDomain setLocalCtime(Date localCtime) {
        this.localCtime = localCtime;
        return this;
    }

    /**
     * 获取本地修改时间 localMtime
     *
     * @return 本地修改时间
     */
    public Date getLocalMtime() {
        return localMtime;
    }
    /**
     * 设置本地修改时间 localMtime
     *
     * @param localMtime 本地修改时间
     */
    public AttachDomain setLocalMtime(Date localMtime) {
        this.localMtime = localMtime;
        return this;
    }


    /**系统生成结束,请勿修改,重新生成会覆盖*/

    /**自定义开始 */

    /**自定义结束 */
}
