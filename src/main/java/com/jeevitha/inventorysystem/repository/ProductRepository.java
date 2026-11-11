package com.jeevitha.inventorysystem.repository;

import com.jeevitha.inventorysystem.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(Long id);

    @Query("SELECT COALESCE(SUM(p.stock),0) FROM Product p")
    Integer getTotalAvailableStock();
}