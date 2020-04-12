package com.zeroq6.blog.operate.service;

import com.alibaba.fastjson.JSON;
import com.zeroq6.blog.common.base.BaseManager;
import com.zeroq6.blog.common.base.BaseService;
import com.zeroq6.blog.common.domain.CommentDomain;
import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.common.domain.PostDomain;
import com.zeroq6.blog.common.domain.enums.field.EmCommentParentType;
import com.zeroq6.blog.common.domain.enums.field.EmDictDictType;
import com.zeroq6.blog.common.domain.enums.field.EmPostPostType;
import com.zeroq6.blog.common.domain.enums.field.EmPostStatus;
import com.zeroq6.blog.operate.manager.CommentManager;
import com.zeroq6.blog.common.base.BaseResponse;
import com.zeroq6.blog.operate.manager.DictManager;
import com.zeroq6.blog.operate.service.comment.CommentPostLog;
import com.zeroq6.common.utils.JsonUtils;
import com.zeroq6.common.utils.MyDateUtils;
import com.zeroq6.common.utils.MyStringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义开始 自定义结束
 */


/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
@Service
public class CommentService extends BaseService<CommentDomain, Long> {

    @Autowired
    private CommentManager commentManager;


    @Autowired
    private PostService postService;

    @Autowired
    private DictManager dictManager;

    private final static int MAX_PER_IP_DAY = 10;

    private final static long POST_INTERVAL_MILLS = 30 * 1000;

    private volatile Map<String, Map<String, CommentPostLog>> commentDatePostLogMap = new ConcurrentHashMap<String, Map<String, CommentPostLog>>();


    @Override
    public BaseManager<CommentDomain, Long> getManager() {
        return commentManager;
    }


    /**
     * 提交评论
     *
     * @param commentDomain
     * @return
     */
    public BaseResponse<String> post(CommentDomain commentDomain) {
        try {
            // 校验
            if (StringUtils.isBlank(commentDomain.getUsername())) {
                throw new RuntimeException("称呼不能为空");
            }
            if (!EmailValidator.getInstance().isValid(commentDomain.getEmail())) {
                throw new RuntimeException("邮箱格式错误");
            }
            if (null != commentDomain.getUrl() && !UrlValidator.getInstance().isValid(commentDomain.getUrl())) {
                throw new RuntimeException("url格式错误");
            }
            if (StringUtils.isBlank(commentDomain.getContent())) {
                throw new RuntimeException("评论内容不能为空");
            }
            if (null == commentDomain.getPostId() || null == commentDomain.getParentType() || null == commentDomain.getParentId()) {
                throw new RuntimeException("文章id，关联id，关联类型不能为空");
            }
            if(MyStringUtils.findSubStringTimes(commentDomain.getContent(), "http") > 3){
                throw new RuntimeException("包含过多链接");
            }
            if (!checkIpCount(commentDomain)) {
                throw new RuntimeException("非法评论请求");
            }
            if(!checkbannedIpComment(commentDomain.getIp())){
                throw new RuntimeException("ip被禁止评论");
            }

            // 文章id是否非法
            PostDomain query = new PostDomain();
            query.setId(commentDomain.getPostId());
            query.setStatus(EmPostStatus.YI_FABU.value());
            query.put("postTypeIn", Arrays.asList(new Integer[]{EmPostPostType.WENZHANG.value(), EmPostPostType.LIUYAN.value()}));
            PostDomain post = postService.selectOne(query);
            //
            if (commentDomain.getParentType() == EmCommentParentType.PINGLUN.value()) {
                this.selectOne(new CommentDomain().setPostId(commentDomain.getPostId()).setId(commentDomain.getParentId()), false);
            } else if (commentDomain.getParentType() == EmCommentParentType.WENZHANG.value()) {
                commentDomain.setParentId(commentDomain.getPostId());
            } else {
                throw new RuntimeException("评论父类型非法, " + commentDomain.getParentType());
            }
            return commentManager.post(commentDomain, post);
        } catch (Exception e) {
            logger.error("提交评论异常, " + JsonUtils.toJSONString(commentDomain), e);
            return new BaseResponse<String>(false, e.getMessage(), null);
        }
    }


    @Transactional
    public BaseResponse<String> deleteCommentById(Long id) {
        try {
            List<Long> commentIdList = getChildrenIdList(id);
            if (!commentIdList.isEmpty()) {
                disableByCondition(new CommentDomain().setParentType(EmCommentParentType.PINGLUN.value()).put("idIn", commentIdList), commentIdList.size());
            }
            disableByKey(id);
            return new BaseResponse<String>(true, null, null);
        } catch (Exception e) {
            logger.error("删除评论异常, " + id, e);
            return new BaseResponse<String>(false, e.getMessage(), null);
        }

    }

    private List<Long> getChildrenIdList(Long id) {
        return getChildrenIdList(id, null);
    }

    private List<Long> getChildrenIdList(Long id, List<Long> commentIdList) {
        if (commentIdList == null || commentIdList.isEmpty()) {
            commentIdList = new ArrayList<Long>();
        }
        List<CommentDomain> commentDomainList = selectList(new CommentDomain().setParentType(EmCommentParentType.PINGLUN.value()).setParentId(id));
        for (CommentDomain comment : commentDomainList) {
            commentIdList.add(comment.getId());
            getChildrenIdList(comment.getId(), commentIdList);
        }
        return commentIdList;
    }

    private boolean checkIpCount(CommentDomain commentDomain) {
        String date = MyDateUtils.format(new Date(), "yyyyMMdd");
        String ip = commentDomain.getIp();
        Map<String, CommentPostLog> commentIpPostLogMap = commentDatePostLogMap.get(date);
        if (MapUtils.isEmpty(commentIpPostLogMap)) {
            // 删除当天之前的评论记录
            commentDatePostLogMap.clear();
            commentIpPostLogMap = new ConcurrentHashMap<String, CommentPostLog>();
            commentDatePostLogMap.put(date, commentIpPostLogMap);

        }
        CommentPostLog commentPostLog = commentIpPostLogMap.get(ip);
        if (null == commentPostLog) {
            commentPostLog = new CommentPostLog(System.currentTimeMillis(), new AtomicInteger(0));
            commentIpPostLogMap.put(ip, commentPostLog);
        }
        if (commentPostLog.getCnt().get() > MAX_PER_IP_DAY) {
            logger.error("ip: {}, post too many times in a day", ip);
            return false;
        }
        if (commentPostLog.getCnt().get() != 0 && System.currentTimeMillis() - commentPostLog.getCurrentTimeMillis() < POST_INTERVAL_MILLS) {
            logger.error("ip: {}, post too fast", ip);
            return false;
        }
        commentPostLog.getCnt().incrementAndGet();
        commentPostLog.setCurrentTimeMillis(System.currentTimeMillis());
        return true;


    }


    private boolean checkbannedIpComment(String ip){
        if(StringUtils.isBlank(ip)){
            logger.error("ip为空");
            return false;
        }
        DictDomain dictDomain = dictManager.getDictByTypeAndKey(EmDictDictType.XI_TONG_PEIZHI, "banned_ip_comment", true);
        if(null == dictDomain){
            return true; // 没有配置，均放行
        }
        List<String> bannedIpList = JSON.parseObject(dictDomain.getDictValue(), List.class);
        return !bannedIpList.contains(ip);
    }

}
