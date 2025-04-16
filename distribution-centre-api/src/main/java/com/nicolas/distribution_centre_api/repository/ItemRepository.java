package com.nicolas.distribution_centre_api.repository;

import com.nicolas.distribution_centre_api.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}

