package com.example.demo.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.logging.Logger;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;
    private static final Logger logger = Logger.getLogger(CustomErrorController.class.getName());

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public String handleError(WebRequest webRequest, Model model) {
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        model.addAllAttributes(errorAttributes);

        logger.severe("Error attributes: " + errorAttributes);

        return "error";
    }
}
