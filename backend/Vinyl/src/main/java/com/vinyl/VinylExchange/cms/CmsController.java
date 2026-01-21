package com.vinyl.VinylExchange.cms;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinyl.VinylExchange.cms.dto.PageDTO;

@RestController
@RequestMapping("/api/cms")
public class CmsController {

    private final CmsService cmsService;

    public CmsController(CmsService cmsService) {
        this.cmsService = cmsService;
    }

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
