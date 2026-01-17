package com.vinyl.VinylExchange.cms;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vinyl.VinylExchange.security.principal.UserPrincipal;

@RestController("/api/cms")
public class CmsController {

    private final CmsService cmsService;

    public CmsController(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    @GetMapping
    public ResponseEntity<?> getAboutPageContent(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Page aboutPage = cmsService.getPageByPageType(PageType.ABOUT);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(aboutPage);
    }
}
