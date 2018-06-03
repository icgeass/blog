package com.zeroq6.blog.operate.service;

import com.zeroq6.blog.common.base.BaseManager;
import com.zeroq6.blog.common.base.BaseService;
import com.zeroq6.blog.common.dao.PostDao;
import com.zeroq6.blog.common.domain.CommentDomain;
import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.common.domain.PostDomain;
import com.zeroq6.blog.common.domain.RelationDomain;
import com.zeroq6.blog.common.domain.enums.field.*;
import com.zeroq6.blog.common.utils.PostUtils;
import com.zeroq6.blog.operate.manager.DictManager;
import com.zeroq6.blog.operate.manager.PostManager;
import com.zeroq6.blog.common.base.BaseResponse;
import com.zeroq6.blog.common.base.Page;
import com.zeroq6.common.utils.GravatarUtils;
import com.zeroq6.common.utils.MyDateUtils;
import com.zeroq6.sso.web.client.context.LoginContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.*;



/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
@Service
public class PostService extends BaseService<PostDomain, Long> {

    @Autowired
    private PostManager postManager;



    private SimpleDateFormat commentFmt = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);


    @Autowired
    private CommentService commentService;

    @Autowired
    private PostDao postDao;


    @Autowired
    private RelationService relationService;

    @Autowired
    private DictManager dictManager;

    private SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");


    @Override
    public BaseManager<PostDomain, Long> getManager() {
        return postManager;
    }





    public BaseResponse<Map<String, Object>> getGuestBook() {
        try {
            PostDomain postDomain = selectOne(new PostDomain().setPostType(EmPostPostType.LIUYAN.value()));
            return show(postDomain.getId());
        } catch (Exception e) {
            logger.error("查询留言异常", e);
            return new BaseResponse<Map<String, Object>>(false, e.getMessage(), null);
        }

    }


    public BaseResponse<Map<String, Object>> getSidebarInfo() {
        try {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            //
            List<DictDomain> sidebarCategories = dictManager.getDictByType(EmDictDictType.FENLEI.value());
            List<DictDomain> sidebarTags = dictManager.getDictByType(EmDictDictType.BIAOQIAN.value());
            List<DictDomain> sidebarLinks = dictManager.getDictByType(EmDictDictType.LIANJIE.value());
            // 站点信息
            List<DictDomain> siteInfo = dictManager.getDictByType(EmDictDictType.ZHAN_DIAN_XINXI.value());
            dataMap.putAll(dictManager.transferMap(siteInfo));
            PostDomain query0 = new PostDomain();
            query0.setPostType(EmPostPostType.WENZHANG.value());
            query0.setStatus(EmPostStatus.YI_FABU.value());
            query0.setStartIndex(0).setEndIndex(10);
            query0.setOrderField("created_time").setOrderFieldType("DESC");
            List<PostDomain> sidebarRecentPosts = postManager.selectList(query0);
            //
            dataMap.put("sidebarCategories", sidebarCategories);
            dataMap.put("sidebarTags", sidebarTags);
            dataMap.put("sidebarLinks", sidebarLinks);
            dataMap.put("sidebarRecentPosts", sidebarRecentPosts);
            return new BaseResponse<Map<String, Object>>(true, "成功", dataMap);
        } catch (Exception e) {
            logger.error("查询sidebar异常", e);
            return new BaseResponse<Map<String, Object>>(false, e.getMessage(), null);
        }
    }

    /**
     * 首页文章列表
     * @param page
     * @return
     */
    public BaseResponse<Page<PostDomain>> index(String page) {
        try {
            int currentPage = 1;
            if (StringUtils.isNumeric(page) && page.length() <= 5) {
                currentPage = Integer.valueOf(page);
            }
            PostDomain contentDomain = new PostDomain();
            contentDomain.setPostType(EmPostPostType.WENZHANG.value());
            contentDomain.setStatus(EmPostStatus.YI_FABU.value());
            Page<PostDomain> p = new Page<PostDomain>(currentPage, 8);
            this.selectPage(contentDomain, p);
            for (PostDomain postDomain : p.getData()) {
                postDomain.getExtendMap().put("contentSummary", PostUtils.getHtmlTextSubstring(postDomain));
            }
            return new BaseResponse<Page<PostDomain>>(true, "成功", p);
        } catch (Exception e) {
            logger.error("查询文章列表异常, page: " + page, e);
            return new BaseResponse<Page<PostDomain>>(false, e.getMessage(), null);
        }
    }


    /**
     * 查看文章详情页
     * @param id
     * @return
     */
    public BaseResponse<Map<String, Object>> show(Long id) {
        try {
            if (null == id) {
                throw new RuntimeException("id不能为空, " + id);
            }
            // 文章
            PostDomain post = this.selectOne(new PostDomain().setId(id));
            post.getExtendMap().put("content", PostUtils.parseMarkdownText(post.getContent()));
            Map<String, Object> dataMap = new HashMap<String, Object>();
            // 评论
            List<CommentDomain> commentDomainList = commentService.selectList(new CommentDomain().setPostId(id).setOrderField("id").setOrderFieldType("ASC"));
            for (CommentDomain item : commentDomainList) {
                Map<String, Object> extendMap = item.getExtendMap();
                extendMap.put("avatar", GravatarUtils.getAvatar(item.getEmail()));
                extendMap.put("timeBefore", MyDateUtils.getDateBeforeNow(item.getCreatedTime()));
                extendMap.put("timeFmt", commentFmt.format(item.getCreatedTime()));
                if (item.getParentType() == EmCommentParentType.PINGLUN.value()) {
                    extendMap.put("commentParent", commentService.selectByKey(item.getParentId()));
                }
            }
            // 评论数量
            post.put("commentCount", commentDomainList.size());
            // 只有文章才查询标签，上一篇，下一篇文章
            if (post.getPostType() == EmPostPostType.WENZHANG.value()) {
                // 标签
                dataMap.put("tags", this.getTagsByPostId(id));

                // 分类
                dataMap.put("category", this.getCategoryByPostId(id));

                // 上一篇，下一篇
                PostDomain prev = postDao.selectPrevPost(post.getId());
                PostDomain next = postDao.selectNextPost(post.getId());
                if (null != prev) {
                    dataMap.put("prev", prev);
                }
                if (null != next) {
                    dataMap.put("next", next);
                }
                dataMap.put("categoryTitle", post.getTitle());
            }
            dataMap.put("post", post);
            dataMap.put("commentList", commentDomainList);
            return new BaseResponse<Map<String, Object>>(true, "成功", dataMap);
        } catch (Exception e) {
            logger.error("查看文章异常, id: " + id, e);
            return new BaseResponse<Map<String, Object>>(false, e.getMessage(), null);
        }
    }


    /**
     * 归档，可指定分类，标签
     * @param category
     * @param tag
     * @return
     */
    public BaseResponse<Map<String, List<PostDomain>>> getArchiveList(String category, String tag) {
        try {
            if (StringUtils.isNotBlank(category) && StringUtils.isNotBlank(tag)) {
                throw new RuntimeException("不能同时指定分类和标签");
            }
            Map<String, List<PostDomain>> data = new LinkedHashMap<String, List<PostDomain>>();
            PostDomain query0 = new PostDomain();
            query0.setOrderField("created_time").setOrderFieldType("DESC").setPostType(EmPostPostType.WENZHANG.value()).setStatus(EmPostStatus.YI_FABU.value());
            List<String> ids = new ArrayList<String>();
            // 标签对应文章id
            if (StringUtils.isNotBlank(tag)) {
                DictDomain dictDomain = dictManager.getDictByTypeAndKey(EmDictDictType.BIAOQIAN.value(), tag);
                RelationDomain query1 = new RelationDomain();
                query1.setType(EmRelationType.WEN_ZHANG_BIAOQIAN.value());
                query1.setChildId(dictDomain.getId() + "");
                List<RelationDomain> relationDomainList = relationService.selectList(query1);
                for (RelationDomain relationDomain : relationDomainList) {
                    ids.add(relationDomain.getParentId() + "");
                }
            }
            // 分类对应文章id
            if (StringUtils.isNotBlank(category)) {
                DictDomain dictDomain = dictManager.getDictByTypeAndKey(EmDictDictType.FENLEI.value(), category);
                RelationDomain query2 = new RelationDomain();
                query2.setType(EmRelationType.WEN_ZHANG_FENLEI.value());
                query2.setChildId(dictDomain.getId() + "");
                List<RelationDomain> relationDomainList = relationService.selectList(query2);
                for (RelationDomain relationDomain : relationDomainList) {
                    ids.add(relationDomain.getParentId() + "");
                }
            }
            if (null == ids || ids.isEmpty()) {
                ids.add("-1"); // 如果查询不到则放置一个不存在id
            }
            if(StringUtils.isNotBlank(tag) || StringUtils.isNotBlank(category)){
                query0.put("idIn", ids);
            }
            List<PostDomain> contentDomainList = postManager.selectList(query0);
            for (PostDomain post : contentDomainList) {
                String key = yyyy.format(post.getCreatedTime());
                if (null == data.get(key)) {
                    data.put(key, new ArrayList<PostDomain>());
                }
                data.get(key).add(post);
            }
            return new BaseResponse<Map<String, List<PostDomain>>>(true, "成功", data);
        } catch (Exception e) {
            logger.error("查询归档异常, categoryCode: " + category + ", tag: " + tag, e);
            return new BaseResponse<Map<String, List<PostDomain>>>(false, e.getMessage(), null);
        }
    }

    public BaseResponse<String> addPost(PostDomain postDomain, List<String> tags, String category) {
        try {
            postDomain.setUsername(LoginContext.getCurrentUsername());
            if (postDomain.getPostType() == EmPostPostType.WENZHANG.value()) {
                if (null == tags || tags.isEmpty() || StringUtils.isBlank(category)) {
                    throw new RuntimeException("新增文章分类和标签不能为空");
                }
                postDomain.setStatus(EmPostStatus.YI_FABU.value());
                // 分类
                RelationDomain relationDomain = new RelationDomain();
                relationDomain.setChildId(category);
                relationDomain.setType(EmRelationType.WEN_ZHANG_FENLEI.value());
                // 标签
                List<RelationDomain> tagsList = new ArrayList<RelationDomain>();
                for (String tag : tags) {
                    RelationDomain newTag = new RelationDomain();
                    newTag.setType(EmRelationType.WEN_ZHANG_BIAOQIAN.value());
                    newTag.setChildId(tag);
                    tagsList.add(newTag);
                }
                postManager.addPost(postDomain, relationDomain, tagsList);
            } else {
                if(null != selectOne(new PostDomain().setPostType(EmPostPostType.LIUYAN.value()), true)){
                    throw new RuntimeException("留言只能发布一篇");
                }
                postManager.addPost(postDomain, null, null);
            }
            return new BaseResponse<String>(true, "成功", null);
        } catch (Exception e) {
            logger.error("新增文章异常, ", e);
            return new BaseResponse<String>(false, e.getMessage(), null);
        }

    }

    public BaseResponse<String> editPost(PostDomain postDomain, List<String> tags, String category) {
        try {
            PostDomain postDomainDb = selectByKey(postDomain.getId());
            if(postDomainDb.getPostType() != postDomain.getPostType()){
                throw new RuntimeException("暂不支持文章类型修改");
            }
            if (postDomain.getPostType() == EmPostPostType.WENZHANG.value()) {
                // 标签最终写数据
                List<RelationDomain> addTags = new ArrayList<RelationDomain>();
                List<RelationDomain> deleteTags = new ArrayList<RelationDomain>();
                //
                List<RelationDomain> dbTags = relationService.selectList(new RelationDomain().setType(EmRelationType.WEN_ZHANG_BIAOQIAN.value()).setParentId(postDomain.getId() + ""));
                for (RelationDomain dbTag : dbTags) {
                    if (!tags.contains(dbTag.getChildId())) {
                        deleteTags.add(dbTag);
                    } else {
                        // 更新忽略，移除最后剩下需要增加的
                        tags.remove(dbTag.getChildId());
                    }
                }
                for (String tag : tags) {
                    RelationDomain tagRelationDomain = new RelationDomain();
                    tagRelationDomain.setChildId(tag);
                    tagRelationDomain.setParentId(postDomain.getId() + "");
                    tagRelationDomain.setType(EmRelationType.WEN_ZHANG_BIAOQIAN.value());

                    addTags.add(tagRelationDomain);
                }
                postManager.editPost(postDomain, category, addTags, deleteTags);
            } else {
                postManager.editPost(postDomain, null, null, null);
            }
            return new BaseResponse<String>(true, "成功", null);
        } catch (Exception e) {
            logger.error("编辑文章异常, ", e);
            return new BaseResponse<String>(false, e.getMessage(), null);
        }

    }


    public List<DictDomain> getTagsByPostId(Long id) {
        Assert.notNull(id, "id不能为空");
        List<RelationDomain> relationTagList = relationService.selectList(new RelationDomain().setType(EmRelationType.WEN_ZHANG_BIAOQIAN.value()).setParentId(id + ""));
        List<DictDomain> tags = new ArrayList<DictDomain>();
        for (RelationDomain relationTag : relationTagList) {
            tags.add(dictManager.selectByKey(Long.valueOf(relationTag.getChildId())));
        }
        return tags;
    }

    public DictDomain getCategoryByPostId(Long id) {
        Assert.notNull(id, "id不能为空");
        RelationDomain relationCategory = relationService.selectOne(new RelationDomain().setType(EmRelationType.WEN_ZHANG_FENLEI.value()).setParentId(id + ""));
        DictDomain category = dictManager.selectByKey(Long.valueOf(relationCategory.getChildId()));
        return category;
    }



}
