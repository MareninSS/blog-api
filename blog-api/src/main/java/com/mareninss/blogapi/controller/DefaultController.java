package com.mareninss.blogapi.controller;

import com.mareninss.blogapi.api.response.InitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {


  private final InitResponse initResponse;

  public DefaultController(InitResponse initResponse) {
    this.initResponse = initResponse;
  }

  @RequestMapping("/")
  public String index() {
    System.out.println(initResponse.getTitle());
    return "index";
  }
}
