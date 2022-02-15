package com.adrian.ecommerceproject.repository;

import com.adrian.ecommerceproject.models.ImageModel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<ImageModel, Long>  {
    Optional<ImageModel> findByName(String name);
}
