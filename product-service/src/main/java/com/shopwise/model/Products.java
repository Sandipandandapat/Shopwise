package com.shopwise.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document("product_vw")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Products {
    @Id
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String brand;
    private List<String> imageUrls;
    private Boolean active;
}
