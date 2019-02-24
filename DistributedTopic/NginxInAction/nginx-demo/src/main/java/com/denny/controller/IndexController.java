package com.denny.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class IndexController {

    @RequestMapping("/index")
    public String index(@RequestHeader(name = "Host", required = false) String host,
                        @RequestHeader(name = "X-Real-IP", required = false) String xRealIp,
                        @RequestHeader(name = "X-Forwarded-For", required = false) String xForwordFor, ModelMap modelMap){
        modelMap.put("host", host);
        modelMap.put("xRealIp", xRealIp);
        modelMap.put("xForwordFor", xForwordFor);
        return "index";
    }
}
