package com.sportstore.backend.repository;

import com.sportstore.backend.domain.CustomerOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
  List<CustomerOrder> findAllByOrderByCreatedAtDesc();
  List<CustomerOrder> findByUserIdOrderByCreatedAtDesc(Long userId);
}
