package com.adrian.ecommerceproject.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.adrian.ecommerceproject.models.Category;
import com.adrian.ecommerceproject.models.ECategories;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(ECategories categories);
}
