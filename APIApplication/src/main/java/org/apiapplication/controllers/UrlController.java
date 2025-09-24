package org.apiapplication.controllers;

import org.apiapplication.dto.url.UrlDto;
import org.apiapplication.services.interfaces.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@CrossOrigin
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping
    public ResponseEntity<UrlDto> getUrlByName(@RequestParam String url) {
        UrlDto urlDto = urlService.getByName(url);
        return ResponseEntity.ok(urlDto);
    }
}
