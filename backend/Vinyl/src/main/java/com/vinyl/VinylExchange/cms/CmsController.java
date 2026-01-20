package com.vinyl.VinylExchange.cms;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/cms")
public class CmsController {

    private final CmsService cmsService;

    public CmsController(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @GetMapping("/about")
    public ResponseEntity<?> getAboutPageContent() {

        Page aboutPage = cmsService.getPageByPageType(PageType.ABOUT);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aboutPage);
    }

    @GetMapping("/contact")
    public ResponseEntity<?> getContactPageContent() {

        Page contactPage = cmsService.getPageByPageType(PageType.CONTACT);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contactPage);
    }

}
