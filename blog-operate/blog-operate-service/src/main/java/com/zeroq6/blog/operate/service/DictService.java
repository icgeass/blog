package com.zeroq6.blog.operate.service;

import com.zeroq6.blog.common.base.BaseManager;
import com.zeroq6.blog.common.base.BaseService;
import com.zeroq6.blog.common.domain.DictDomain;
import com.zeroq6.blog.common.domain.enums.field.EmDictDictType;
import com.zeroq6.blog.operate.manager.DictManager;
import com.zeroq6.blog.common.base.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;



/**
 * @author icgeass@hotmail.com
 * @date 2017-05-17
 */
@Service
public class DictService extends BaseService<DictDomain, Long> {

    @Autowired
    private DictManager dictManager;



    @Override
    public BaseManager<DictDomain, Long> getManager() {
        return dictManager;
    }




    public BaseResponse<List<DictDomain>> getHistory() {
        try {
            List<DictDomain> list = dictManager.getDictByType(EmDictDictType.LISHI.value());
            Collections.sort(list, new Comparator<DictDomain>() {
                @Override
                public int compare(DictDomain o1, DictDomain o2) {
                    return Integer.valueOf(o2.getDictKey()) - Integer.valueOf(o1.getDictKey());
                }
            });
            return new BaseResponse<List<DictDomain>>(true, "成功", list);
        } catch (Exception e) {
            logger.error("获取历史异常, ", e);
            return new BaseResponse<List<DictDomain>>(false, e.getMessage(), null);
        }
    }


    public BaseResponse<Map<String, String>> getAboutInfo() {
        try {
            List<DictDomain> list = dictManager.getDictByTypeList(Arrays.asList(EmDictDictType.SHE_JIAO.value()));
            Map<String, String> result = dictManager.transferMap(list);
            return new BaseResponse<Map<String, String>>(true, "成功", result);
        } catch (Exception e) {
            logger.error("获取关于信息异常, ", e);
            return new BaseResponse<Map<String, String>>(false, e.getMessage(), null);
        }
    }


}
