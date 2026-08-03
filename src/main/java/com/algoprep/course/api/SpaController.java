package com.algoprep.course.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({
            "/patterns",
            "/patterns/**",
            "/challenge",
            "/challenge/**",
            "/quiz",
            "/quiz/**",
            "/cheatsheet",
            "/skills",
            "/daily",
            "/mock",
            "/spring",
            "/settings",
            "/review",
            "/tracks",
            "/metrics",
            "/about"
    })
    public String spa() {
        return "forward:/index.html";
    }
}
