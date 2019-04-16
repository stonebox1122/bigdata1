package com.stone.ct.web.dao;

import com.stone.ct.web.bean.Calllog;

import java.util.List;
import java.util.Map;

/**
 * 通话日志数据访问对象
 */
public interface CalllogDao {
    List<Calllog> queryMonth(Map<String, Object> paramMap);
}
