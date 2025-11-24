package com.vinyl.VinylExchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.vinyl.VinylExchange.domain.pojo.Label;

@Repository
public interface LabelRepository extends JpaRepository<Label, String> {

}
