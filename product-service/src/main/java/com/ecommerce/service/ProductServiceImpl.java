package com.ecommerce.service;

import com.ecommerce.controller.InventoryServiceClient;
import com.ecommerce.dto.InventoryRequestDto;
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
    private final InventoryServiceClient inventoryServiceClient;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper mapper, InventoryServiceClient inventoryServiceClient){
        this.productRepository=productRepository;
        this.mapper=mapper;
        this.inventoryServiceClient=inventoryServiceClient;
    }

    @Override
    public List<ProductResponseDto> addProduct(List<ProductRequestDto> requestDto) {
        List<Products> products = requestDto.stream()
                .map(mapper :: toProductEntity)
                .collect(Collectors.toList());
        productRepository.saveAll(products);
        List<InventoryRequestDto> inventoryRequest = products.stream()
                .map(product-> InventoryRequestDto.builder()
                        .productId(product.getId())
                        .quantity(0)
                        .build())
                        .toList();
        inventoryServiceClient.addStock(inventoryRequest);
        return products.stream()
                .map(mapper :: toProductResponseDto)
                .collect(Collectors.toList());

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
