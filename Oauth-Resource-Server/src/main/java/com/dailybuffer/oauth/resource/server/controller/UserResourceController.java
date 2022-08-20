package com.dailybuffer.oauth.resource.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserResourceController {

    @GetMapping("/api/users")
    public String[] getUser(){

        return new String[]{"Arun", "Tarun","Atharv","Radhika"};
    }
}
