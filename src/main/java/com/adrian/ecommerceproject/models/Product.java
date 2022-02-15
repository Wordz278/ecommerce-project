package com.adrian.ecommerceproject.models;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "products")
public class Product implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Float price;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "product_category",
    joinColumns=@JoinColumn(name="product_id"),
    inverseJoinColumns = @JoinColumn(name="category_id"))
    private Set<Category> category = new HashSet<>();

    private String imageUrl;

    
    public Product() {
    }

    public Product(Long id) {
        this.id = id;
    }



    

    public Product(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public Product(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public Product(Set<Category> category) {
        this.category = category;
    }




    public Product(String name, String description, Float price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Product(String name, String description, Float price, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    
    @Override
    public String toString() {
        return "Product [description=" + description + ", name=" + name + ", price=" + price + "]";
    }

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }




    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public Float getPrice() {
        return price;
    }


    public void setPrice(Float price) {
        this.price = price;
    }


    public Set<Category> getCategory() {
        return category;
    }


    public void setCategory(Set<Category> category) {
        this.category = category;
    }


    public String getImageUrl() {
        return imageUrl;
    }


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    
    
}
