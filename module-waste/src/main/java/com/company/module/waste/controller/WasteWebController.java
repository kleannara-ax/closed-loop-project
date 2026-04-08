package com.company.module.waste.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 폐기물 순환 추적 웹 UI 컨트롤러
 * - /waste → SPA index.html 서빙
 */
@RestController
public class WasteWebController {

    @GetMapping(value = "/waste", produces = MediaType.TEXT_HTML_VALUE)
    public String wastePage() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/index.html");
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }
}
