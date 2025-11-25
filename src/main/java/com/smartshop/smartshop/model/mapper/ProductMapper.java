package com.smartshop.smartshop.model.mapper;

import com.smartshop.smartshop.model.dto.ProductDTO;
import com.smartshop.smartshop.model.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDto(Product product);
    Product toEntity(ProductDTO dto);
}
