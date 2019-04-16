package com.stone.ct.web.service;

import com.stone.ct.web.bean.Calllog;

import java.util.List;

public interface CalllogService {
    List<Calllog> queryMonth(String tel, String callTime);
}
