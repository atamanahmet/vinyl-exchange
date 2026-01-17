package com.vinyl.VinylExchange.cms;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, UUID> {
    Optional<Page> findByPageType(PageType pageType);
}
