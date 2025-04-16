package com.warehouse.clothes_warehouse.controller;

import com.warehouse.clothes_warehouse.model.Brand;
import com.warehouse.clothes_warehouse.model.Item;
import com.warehouse.clothes_warehouse.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/add-item")
    public String showAddItemForm(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("brands", Brand.values());
        return "add-item";
    }

    @PostMapping("/add-item")
    public String addItem(@Valid @ModelAttribute("item") Item item, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("brands", Brand.values());
            return "add-item";
        }
        itemService.saveItem(item);
        return "redirect:/items";
    }
}
