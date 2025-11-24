package com.vinyl.VinylExchange.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinyl.VinylExchange.domain.entity.Vinyl;

@Repository
public interface VinylRepository extends JpaRepository<Vinyl, String> {

}
