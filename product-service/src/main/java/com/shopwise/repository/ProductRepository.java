package com.shopwise.repository;

import com.shopwise.model.Products;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Products,String> {
    List<Products> findByCategory(String category);
}
