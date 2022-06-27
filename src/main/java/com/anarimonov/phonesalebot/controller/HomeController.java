package com.anarimonov.phonesalebot.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @RequestMapping("/api/test/hello")
    public HttpEntity<?> run() {
        return ResponseEntity.ok("/start");
    }
}
