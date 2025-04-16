package com.warehouse.clothes_warehouse.service;

import com.warehouse.clothes_warehouse.model.Brand;
import com.warehouse.clothes_warehouse.model.Item;
import com.warehouse.clothes_warehouse.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Page<Item> getAllItems(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    public Page<Item> getItemsByBrandAndYear(Brand brand, int year, Pageable pageable) {
        return itemRepository.findItemsByBrandAndYear(brand, year, pageable);
    }

    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}