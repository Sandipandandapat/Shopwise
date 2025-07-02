package com.ecommerce.repository;

import com.ecommerce.model.Products;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Products,String> {
    List<Products> findByCategory(String category);
}
