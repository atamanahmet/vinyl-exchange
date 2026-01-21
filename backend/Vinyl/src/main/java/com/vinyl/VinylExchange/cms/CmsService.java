package com.vinyl.VinylExchange.cms;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.cms.dto.PageDTO;
import com.vinyl.VinylExchange.shared.FileStorageService;
import com.vinyl.VinylExchange.shared.exception.PageNotFoundException;

@Service
public class CmsService {
    private final PageRepository pageRepository;
    private final FileStorageService fileStorageService;

    public CmsService(PageRepository pageRepository, FileStorageService fileStorageService) {
        this.pageRepository = pageRepository;
        this.fileStorageService = fileStorageService;
    }

    public Page getPageByPageType(PageType pageType) {
        return pageRepository.findByPageType(pageType).orElseThrow(() -> new PageNotFoundException());
    }

    public Page savePage(Page page) {
        return pageRepository.save(page);
    }

    public Boolean existsByPageType(PageType pageType) {
        return pageRepository.existsByPageType(pageType);
    }

    public void deleteAll() {
        pageRepository.deleteAll();
    }

    public PageDTO getPageDTOByType(PageType pageType) {
        Page page = getPageByPageType(pageType);

        String textContent = fileStorageService.readTextContentFile(page.getTextContentPath());

        return PageDTO.builder()
                .header(page.getHeader())
                .textContent(textContent)
                .backgroundImagePath(page.getBackgroundImagePath())
                .build();
    }
}
