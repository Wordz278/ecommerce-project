package com.adrian.ecommerceproject.repository;

import java.util.Optional;

import com.adrian.ecommerceproject.models.ImageUpload;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageUploadRepository extends JpaRepository<ImageUpload, Long> {
    Optional<ImageUpload> findById(Long id);   
}
