package com.warehouse.clothes_warehouse.utils;

import com.warehouse.clothes_warehouse.model.Brand;
import com.warehouse.clothes_warehouse.model.Item;
import com.warehouse.clothes_warehouse.repository.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(ItemRepository itemRepository) {
        return args -> {
            if (itemRepository.count() == 0) {
                List<Item> items = List.of(
                        new Item(null, "Jacket", Brand.GUCCI, new BigDecimal("1500"), 2022),
                        new Item(null, "T-Shirt", Brand.BALENCIAGA, new BigDecimal("1200"), 2022),
                        new Item(null, "Jeans", Brand.PRADA, new BigDecimal("2000"), 2024)
                );
                itemRepository.saveAll(items);
            }
        };
    }
}

