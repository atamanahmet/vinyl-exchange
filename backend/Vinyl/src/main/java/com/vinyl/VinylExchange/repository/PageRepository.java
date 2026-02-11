package com.vinyl.VinylExchange.repository;

import java.util.Optional;
import java.util.UUID;

import com.vinyl.VinylExchange.domain.entity.Page;
import com.vinyl.VinylExchange.domain.enums.PageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, UUID> {
    Optional<Page> findByPageType(PageType pageType);

    boolean existsByPageType(PageType pageType);
}
