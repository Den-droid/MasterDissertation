package org.apiapplication.controllers;

import org.apiapplication.dto.url.MethodTypeDto;
import org.apiapplication.dto.url.UrlDto;
import org.apiapplication.services.interfaces.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/urls")
@CrossOrigin
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping
    public ResponseEntity<List<UrlDto>> getUrlByName(@RequestParam(required = false) String url,
                                                     @RequestParam(required = false) Integer method) {
        List<UrlDto> urlDto = urlService.getAllOrByUrl(url, method);
        return ResponseEntity.ok(urlDto);
    }

    @GetMapping("/methods")
    public ResponseEntity<List<MethodTypeDto>> getMethods() {
        List<MethodTypeDto> methodTypeDto = urlService.getMethods();
        return ResponseEntity.ok(methodTypeDto);
    }
}
