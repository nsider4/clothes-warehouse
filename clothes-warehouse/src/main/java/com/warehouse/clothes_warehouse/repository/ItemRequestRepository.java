package com.warehouse.clothes_warehouse.repository;

import com.warehouse.clothes_warehouse.model.ItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
}
