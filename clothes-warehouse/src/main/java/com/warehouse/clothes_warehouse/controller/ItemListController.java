package com.warehouse.clothes_warehouse.controller;

import com.warehouse.clothes_warehouse.model.Brand;
import com.warehouse.clothes_warehouse.service.ItemService;
import com.warehouse.clothes_warehouse.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ItemListController {

    private final ItemService itemService;

    public ItemListController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/delete-item/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return "redirect:/items";
    }


    @GetMapping("/items")
    public String listItems(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy
    ) {
        Page<Item> items = itemService.getAllItems(PageRequest.of(page, size, Sort.by(sortBy)));
        model.addAttribute("items", items);
        model.addAttribute("brands", Brand.values());
        return "list-items";
    }

    @GetMapping("/filter")
    public String filterByBrandAndYear(
            @RequestParam("brand") Brand brand,
            @RequestParam(defaultValue = "2022") int year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            Model model
    ) {
        Page<Item> items = itemService.getItemsByBrandAndYear(brand, year, PageRequest.of(page, size, Sort.by(sortBy)));
        model.addAttribute("items", items);
        model.addAttribute("brands", Brand.values());
        model.addAttribute("selectedBrand", brand);
        return "list-items";
    }
}