package com.sportstore.backend.repository;

import com.sportstore.backend.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
