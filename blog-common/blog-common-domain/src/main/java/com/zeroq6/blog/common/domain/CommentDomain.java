package com.zeroq6.blog.common.domain;

import com.zeroq6.blog.common.base.BaseDomain;

/**
 * @author icgeass@hotmail.com
 * @date 2017-11-10
 */
public class CommentDomain extends BaseDomain<CommentDomain> {

    private static final long serialVersionUID = 1L;

    /**
    * 评论 => comment
    */
    public CommentDomain(){
        // 默认无参构造
    }

    /**
     * 用户名
     */
    private String username;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 链接
     */
    private String url;
    /**
     * 内容
     */
    private String content;
    /**
     * 文章id
     */
    private Long postId;
    /**
     * 父id
     */
    private Long parentId;
    /**
     * 父类型，1，文章，2，评论
     */
    private Integer parentType;
    /**
     * ip地址
     */
    private String ip;
    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 获取用户名 username
     *
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }
    /**
     * 设置用户名 username
     *
     * @param username 用户名
     */
    public CommentDomain setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * 获取邮箱 email
     *
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }
    /**
     * 设置邮箱 email
     *
     * @param email 邮箱
     */
    public CommentDomain setEmail(String email) {
        this.email = email;
        return this;
    }

    /**
     * 获取链接 url
     *
     * @return 链接
     */
    public String getUrl() {
        return url;
    }
    /**
     * 设置链接 url
     *
     * @param url 链接
     */
    public CommentDomain setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * 获取内容 content
     *
     * @return 内容
     */
    public String getContent() {
        return content;
    }
    /**
     * 设置内容 content
     *
     * @param content 内容
     */
    public CommentDomain setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 获取文章id postId
     *
     * @return 文章id
     */
    public Long getPostId() {
        return postId;
    }
    /**
     * 设置文章id postId
     *
     * @param postId 文章id
     */
    public CommentDomain setPostId(Long postId) {
        this.postId = postId;
        return this;
    }

    /**
     * 获取父id parentId
     *
     * @return 父id
     */
    public Long getParentId() {
        return parentId;
    }
    /**
     * 设置父id parentId
     *
     * @param parentId 父id
     */
    public CommentDomain setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    /**
     * 获取父类型，1，文章，2，评论 parentType
     *
     * @return 父类型，1，文章，2，评论
     */
    public Integer getParentType() {
        return parentType;
    }
    /**
     * 设置父类型，1，文章，2，评论 parentType
     *
     * @param parentType 父类型，1，文章，2，评论
     */
    public CommentDomain setParentType(Integer parentType) {
        this.parentType = parentType;
        return this;
    }

    /**
     * 获取ip地址 ip
     *
     * @return ip地址
     */
    public String getIp() {
        return ip;
    }
    /**
     * 设置ip地址 ip
     *
     * @param ip ip地址
     */
    public CommentDomain setIp(String ip) {
        this.ip = ip;
        return this;
    }

    /**
     * 获取用户代理 userAgent
     *
     * @return 用户代理
     */
    public String getUserAgent() {
        return userAgent;
    }
    /**
     * 设置用户代理 userAgent
     *
     * @param userAgent 用户代理
     */
    public CommentDomain setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }


    /**系统生成结束,请勿修改,重新生成会覆盖*/

    /**自定义开始 */

    /**自定义结束 */
}
