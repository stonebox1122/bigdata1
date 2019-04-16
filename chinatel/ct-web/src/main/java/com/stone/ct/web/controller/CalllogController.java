package com.stone.ct.web.controller;

import com.stone.ct.web.bean.Calllog;
import com.stone.ct.web.service.CalllogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 通话日志控制器对象
 */
@Controller
public class CalllogController {

    @Autowired
    private CalllogService calllogService;

    @RequestMapping("/query")
    public String query(){
        return "query";
    }

    /**
     * Object ==> json ==> String
     * @return
     */
    //@ResponseBody
    @RequestMapping("/view")
    public String view(String tel, String callTime, Model model){
        // 查询统计结果
        List<Calllog> calllogs = calllogService.queryMonth(tel, callTime);
        model.addAttribute("calllogs",calllogs);
        return "view";
    }

}
