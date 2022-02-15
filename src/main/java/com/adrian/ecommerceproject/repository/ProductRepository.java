package com.adrian.ecommerceproject.repository;

import java.util.Optional;


import com.adrian.ecommerceproject.models.ECategories;
import com.adrian.ecommerceproject.models.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(ECategories name);

    @Query(value="SELECT products.id, products.name, products.description, products.image_url, products.price FROM products join product_category on product_category.product_id=products.id and product_category.category_id=?1"
    , nativeQuery=true)
    Optional<Product> getAllProductsByCategoryId(@Param("id") Long id);
}
