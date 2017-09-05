package com.zeroq6.blog.operate.manager;

import com.zeroq6.blog.common.base.BaseDao;
import com.zeroq6.blog.common.base.BaseManager;
import com.zeroq6.blog.common.dao.AttachDao;
import com.zeroq6.blog.common.domain.AttachDomain;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
/**
 * @author icgeass@hotmail.com
 * @date 2017-07-08
 */
@Service
public class AttachManager extends BaseManager<AttachDomain, Long> {


    @Autowired
    private AttachDao attachDao;


    @Autowired
    private AttachManager attachManager;

    @Value("${project.upload.path}")
    private String uploadPath;


    @Override
    public BaseDao<AttachDomain, Long> getDao() {
        return attachDao;
    }



    public String getUploadPath(){
        try{
            if(!StringUtils.isBlank(uploadPath) && !uploadPath.startsWith("$")){
                return uploadPath;
            }
            return new File(getClass().getResource("/").getPath() + File.separator + "upload").getCanonicalPath();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    @Transactional
    public void save(MultipartFile[] files){
        String writeToPath = null;
        try {
            MultipartFile clientFile = files[0];
            byte[] data = clientFile.getBytes();
            String md5 = DigestUtils.md5Hex(data);

            AttachDomain attachDomainDb = selectOne(new AttachDomain().setMd5(md5), true);
            if (null != attachDomainDb) {
                logger.warn("文件已存在, 跳过上传，" + clientFile.getOriginalFilename());
            } else {
                //
                writeToPath = attachManager.getUploadPath() + File.separator + clientFile.getOriginalFilename();
                FileUtils.writeByteArrayToFile(new File(writeToPath), data, false);

                // 入库
                AttachDomain attachDomain = new AttachDomain();
                attachDomain.setName(clientFile.getOriginalFilename());
                attachDomain.setMd5(md5);
                attachDomain.setSize(clientFile.getSize());

                insert(attachDomain);
                // 数据库编码不支持的文件名会显示方框，所以回查是否存在，不存在删除文件并回滚
                attachDomainDb = selectOne(new AttachDomain().setMd5(md5));
                if(!clientFile.getOriginalFilename().equals(attachDomainDb.getName())){
                    throw new RuntimeException("非法文件名, " + clientFile.getOriginalFilename());
                }
            }
        } catch (Exception e) {
            logger.error("上传文件异常, " + writeToPath, e);
            if (StringUtils.isNotBlank(writeToPath)) {
                FileUtils.deleteQuietly(new File(writeToPath));
            }
            throw new RuntimeException(e); // 一定要抛出回滚
        }
    }

    @Transactional
    public void deleteById(Long id){
        AttachDomain attachDomain = selectByKey(id);
        // 先disable数据库记录，然后删文件，异常可回滚
        disableByKey(id);
        //
        File file = null;
        if (null != attachDomain) {
            file = new File(attachManager.getUploadPath() + File.separator + attachDomain.getName());
        }
        if (null == attachDomain || !file.exists() || file.isDirectory()) {
            throw new RuntimeException("删除文件失败, 文件不存在");
        }
        if(!file.delete()){
            throw new RuntimeException("删除文件失败, 访问拒绝");
        }
    }

}
