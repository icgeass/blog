package com.zeroq6.blog.operate.manager.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * Created by yuuki asuna on 2017/5/26.
 */


@Service
public class VelocityManager {


    private VelocityEngine velocityEngine = null;

    private String resourceRootPath = null;

    private VelocityEngine getVelocityEngine() {
        try {
            if (null != velocityEngine) {
                return velocityEngine;
            }
            String path = new File(this.getClass().getResource("/").getPath()).getParentFile().getParentFile().getCanonicalPath();
            velocityEngine = new VelocityEngine();
            Properties properties = new Properties();
            properties.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, path);
            properties.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
            properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            velocityEngine.init(properties);   //初始化
            return velocityEngine;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void merge(Context context, String templatePath, String des) {
        try {
            Template template = getVelocityEngine().getTemplate(templatePath, "UTF-8");
            File desFile = new File(des);
            PrintWriter pw = new PrintWriter(desFile, "UTF-8");
            template.merge(context, pw);
            pw.close(); // 刷新
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getResourceRootPath(){
        try{
            if(null != resourceRootPath){
                return resourceRootPath;
            }
            Object obj = getVelocityEngine().getProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH);
            resourceRootPath = new File((String) obj).getCanonicalPath();
            return resourceRootPath;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
