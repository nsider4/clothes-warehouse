package com.warehouse.clothes_warehouse.repository;

import com.warehouse.clothes_warehouse.model.Item;
import com.warehouse.clothes_warehouse.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAll(Pageable pageable);

    @Query("SELECT i FROM Item i WHERE i.brand = :brand AND i.yearOfCreation = :year")
    Page<Item> findItemsByBrandAndYear(@Param("brand") Brand brand, @Param("year") int year, Pageable pageable);
}

