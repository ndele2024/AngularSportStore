package com.sportstore.backend.service;

import com.sportstore.backend.domain.Product;
import com.sportstore.backend.dto.ProductDto;
import com.sportstore.backend.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final StoreMapper storeMapper;

  public ProductService(ProductRepository productRepository, StoreMapper storeMapper) {
    this.productRepository = productRepository;
    this.storeMapper = storeMapper;
  }

  public List<ProductDto> getProducts() {
    return productRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
      .map(storeMapper::toProductDto)
      .toList();
  }

  public ProductDto create(ProductDto dto) {
    Product product = new Product();
    apply(product, dto);
    return storeMapper.toProductDto(productRepository.save(product));
  }

  public ProductDto update(Long id, ProductDto dto) {
    Product product = productRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));
    apply(product, dto);
    return storeMapper.toProductDto(productRepository.save(product));
  }

  public void delete(Long id) {
    productRepository.deleteById(id);
  }

  private void apply(Product product, ProductDto dto) {
    product.setName(dto.name());
    product.setCategory(dto.category());
    product.setDescription(dto.description());
    product.setPrice(dto.price());
    product.setImageUrl(dto.imageUrl());
  }
}
