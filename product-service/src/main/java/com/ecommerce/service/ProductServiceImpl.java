package com.ecommerce.service;

import com.ecommerce.dto.ProductRequestDto;
import com.ecommerce.dto.ProductResponseDto;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.Products;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductMapper mapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper mapper){
        this.productRepository=productRepository;
        this.mapper=mapper;
    }

    @Override
    public List<ProductResponseDto> addProduct(List<ProductRequestDto> requestDto) {
        List<Products> request = requestDto.stream()
                .map(mapper :: toProductEntity)
                .collect(Collectors.toList());
        productRepository.saveAll(request);
        List<ProductResponseDto> response = request.stream()
                .map(mapper :: toProductResponseDto)
                .collect(Collectors.toList());
        return response;
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        List<Products> products = productRepository.findAll();
        List<ProductResponseDto> response = products.stream()
                .map(mapper::toProductResponseDto)
                .toList();
        return response;
    }

    @Override
    public ProductResponseDto getProductById(String productId) {
        return productRepository.findById(productId)
                .map(mapper::toProductResponseDto)
                .orElseThrow(()->new RuntimeException("Product not found"));
    }

    @Override
    public List<ProductResponseDto> getProductByCategory(String category) {
        return productRepository.findByCategory(category)
                .stream()
                .map(mapper::toProductResponseDto)
                .toList();

    }
}
