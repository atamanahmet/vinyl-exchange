package com.vinyl.VinylExchange.cms;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.shared.exception.PageNotFoundException;

@Service
public class CmsService {
    private final PageRepository pageRepository;

    public CmsService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public Page getPageByPageType(PageType pageType) {
        return pageRepository.findByPageType(pageType).orElseThrow(() -> new PageNotFoundException());
    }
}
