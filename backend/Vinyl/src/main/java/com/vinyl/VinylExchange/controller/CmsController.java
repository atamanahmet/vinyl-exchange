package com.vinyl.VinylExchange.controller;

import com.vinyl.VinylExchange.service.CmsService;
import com.vinyl.VinylExchange.domain.enums.PageType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinyl.VinylExchange.dto.PageDTO;

@RestController
@RequestMapping("/api/cms")
@RequiredArgsConstructor

//TODO: Add authentication
public class CmsController {

    private final CmsService cmsService;

    @GetMapping("/about")
    public ResponseEntity<?> getAboutPageContent() {

        PageDTO aboutPage = cmsService.getPageDTOByType(PageType.ABOUT);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aboutPage);
    }

    @GetMapping("/contact")
    public ResponseEntity<?> getContactPageContent() {

        PageDTO contactPage = cmsService.getPageDTOByType(PageType.CONTACT);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contactPage);
    }

}
