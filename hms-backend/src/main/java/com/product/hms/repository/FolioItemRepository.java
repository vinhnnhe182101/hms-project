package com.product.hms.repository;

import com.product.hms.entity.FolioItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FolioItemRepository extends JpaRepository<FolioItemEntity, Long> {
}

