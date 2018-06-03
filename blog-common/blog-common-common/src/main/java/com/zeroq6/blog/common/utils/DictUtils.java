package com.zeroq6.blog.common.utils;

import com.zeroq6.blog.common.domain.DictDomain;

import java.util.List;

/**
 * Created by yuuki asuna on 2017/8/6.
 */
public class DictUtils {


    public boolean contains(List<DictDomain> list, DictDomain item) {
        if (null == list || list.isEmpty() || null == item) {
            return false;
        }
        for (DictDomain dictDomain : list) {
            if (dictDomain.getDictType().equals(item.getDictType()) && dictDomain.getDictKey().equals(item.getDictKey())) {
                return true;
            }
        }
        return false;
    }
}
