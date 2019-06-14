package com.zeroq6.blog.operate.service.atom;

import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.common.domain.PostDomain;
import com.zeroq6.blog.common.domain.enums.field.EmDictDictType;
import com.zeroq6.blog.common.domain.enums.field.EmPostPostType;
import com.zeroq6.blog.common.domain.enums.field.EmPostStatus;
import com.zeroq6.blog.common.utils.PostUtils;
import com.zeroq6.blog.operate.manager.DictManager;
import com.zeroq6.blog.operate.manager.velocity.VelocityManager;
import com.zeroq6.blog.operate.service.PostService;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yuuki asuna on 2017/5/25.
 */

@Service
public class AtomService implements InitializingBean {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PostService postService;

    @Autowired
    private DictManager dictManager;

    @Autowired
    private VelocityManager velocityManager;


    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");


    public void genAtomXmlWithoutException() {
        try {
            genAtomXml();
        } catch (Exception e) {
            logger.error("生成atom.xml异常", e);
        }
    }


    public void genAtomXml() {
        logger.info("开始生成atom.xml");
        try {
            List<DictDomain> list = dictManager.getDictByType(EmDictDictType.ZHAN_DIAN_XINXI);
            Map<String, String> dataMap = dictManager.transferMap(list);
            //
            VelocityContext vc = new VelocityContext(dataMap);
            vc.put("updated", sdf.format(new Date()));
            // 文章列表
            PostDomain query = new PostDomain();
            query.setPostType(EmPostPostType.WENZHANG.value()).setStatus(EmPostStatus.YI_FABU.value());
            query.setStartIndex(0).setEndIndex(10000);
            List<PostDomain> postDomainList = postService.selectList(query);
            for (PostDomain item : postDomainList) {
                Map<String, Object> extendMap = item.getExtendMap();
                extendMap.put("published", sdf.format(item.getCreatedTime()));
                extendMap.put("updated", sdf.format(item.getModifiedTime()));
                extendMap.put("tags", postService.getTagsByPostId(item.getId()));
                extendMap.put("category", postService.getCategoryByPostId(item.getId()));
                //
                String html = PostUtils.parseMarkdownText(item.getContent());
                extendMap.put("content", html);
                //
                extendMap.put("summary", PostUtils.substring(PostUtils.getHtmlText(html)));
            }
            vc.put("postDomainList", postDomainList);
            String desPath = velocityManager.getResourceRootPath() + "/" + "atom.xml";
            velocityManager.merge(vc, "atom.xml.vm", desPath);
            logger.info("生成atom.xml成功, 服务端文件路径: " + desPath);
        } catch (Exception e) {
            logger.error("生成atom.xml失败", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        genAtomXml();
    }
}
