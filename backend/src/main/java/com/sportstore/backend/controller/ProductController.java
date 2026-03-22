package com.sportstore.backend.controller;

import com.sportstore.backend.dto.ProductDto;
import com.sportstore.backend.service.ProductService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("/products")
  public List<ProductDto> getProducts() {
    return productService.getProducts();
  }

  @PostMapping("/products")
  @ResponseStatus(HttpStatus.CREATED)
  public ProductDto createProduct(@RequestBody ProductDto product) {
    return productService.create(product);
  }

  @PutMapping("/products/{id}")
  public ProductDto updateProduct(@PathVariable Long id, @RequestBody ProductDto product) {
    return productService.update(id, product);
  }

  @DeleteMapping("/products/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProduct(@PathVariable Long id) {
    productService.delete(id);
  }
}
