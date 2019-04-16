package com.stone.ct.web.service.impl;

import com.stone.ct.web.bean.Calllog;
import com.stone.ct.web.dao.CalllogDao;
import com.stone.ct.web.service.CalllogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通话日志服务对象
 */
@Service
public class CalllogServiceImpl implements CalllogService {

    @Autowired
    private CalllogDao calllogDao;

    /**
     * 查询用户指定时间的通话统计信息
     * @param tel
     * @param callTime
     * @return
     */
    @Override
    public List<Calllog> queryMonth(String tel, String callTime) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tel",tel);
        if (callTime.length() > 4){
            callTime = callTime.substring(0,4);
        }
        paramMap.put("callTime",callTime);
        return calllogDao.queryMonth(paramMap);
    }
}
