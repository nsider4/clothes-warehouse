package com.warehouse.clothes_warehouse.controller;

import com.warehouse.clothes_warehouse.model.Brand;
import com.warehouse.clothes_warehouse.model.DistributionCentre;
import com.warehouse.clothes_warehouse.model.Item;
import com.warehouse.clothes_warehouse.model.ItemRequest;
import com.warehouse.clothes_warehouse.repository.ItemRequestRepository;
import com.warehouse.clothes_warehouse.service.DistributionCentreService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
public class AdminController {

    private final DistributionCentreService distributionCentreService;
    private final ItemRequestRepository itemRequestRepository;

    public AdminController(DistributionCentreService distributionCentreService,
                           ItemRequestRepository itemRequestRepository) {
        this.distributionCentreService = distributionCentreService;
        this.itemRequestRepository = itemRequestRepository;
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_EMPLOYEE')")
    public String showAdminDashboard(Model model) {
        return "admin-dashboard";
    }

    @GetMapping("/admin/add-item")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_EMPLOYEE')")
    public String showAddItemForm(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("brands", Brand.values());

        List<DistributionCentre> centres = distributionCentreService.fetchAllCentres();
        model.addAttribute("centres", centres);

        return "add-item";
    }

    @PostMapping("/admin/add-item")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_EMPLOYEE')")
    public String addItemToStock(@ModelAttribute("item") @Valid Item item,
                                 @RequestParam Long centreId,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("brands", Brand.values());
            List<DistributionCentre> centres = distributionCentreService.fetchAllCentres();
            model.addAttribute("centres", centres);
            return "add-item";
        }

        distributionCentreService.addItem(
                centreId,
                item.getName(),
                item.getBrand(),
                item.getQuantity(),
                item.getYearOfCreation(),
                item.getPrice()
        );

        return "redirect:/add-item?success";
    }

    @GetMapping("/admin/distribution-centres")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_EMPLOYEE', 'USER')")
    public String viewDistributionCentres(Model model) {
        List<DistributionCentre> centres = distributionCentreService.fetchAllCentres();
        model.addAttribute("centres", centres);
        return "distribution-centres";
    }

    @GetMapping("/admin/request-form")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_EMPLOYEE')")
    public String showRequestForm(@RequestParam(required = false) Long sourceCentreId,
                                  @RequestParam(required = false) Long itemId,
                                  Model model) {
        List<DistributionCentre> centres = distributionCentreService.fetchAllCentres();
        model.addAttribute("centres", centres);
        model.addAttribute("sourceCentreId", sourceCentreId);
        model.addAttribute("itemId", itemId);

        centres.stream()
                .filter(c -> c.getId().equals(sourceCentreId))
                .findFirst()
                .ifPresent(selectedCentre -> model.addAttribute("availableItems", selectedCentre.getItems()));

        Item selectedItem = distributionCentreService.findItemByIdInCentre(sourceCentreId, itemId);
        model.addAttribute("selectedItem", selectedItem);

        return "request-form";
    }

    @PostMapping("/admin/submit-request")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_EMPLOYEE')")
    public String submitItemRequest(@RequestParam Long sourceCentreId,
                                    @RequestParam Long itemId,
                                    @RequestParam int quantity,
                                    @RequestParam Long destinationCentreId,
                                    Model model) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setSourceCentreId(sourceCentreId);
        itemRequest.setItemId(itemId);
        itemRequest.setQuantity(quantity);
        itemRequest.setDestinationCentreId(destinationCentreId);

        itemRequestRepository.save(itemRequest);
        model.addAttribute("message", "Request submitted successfully!");

        return "redirect:/admin/item-requests";
    }

    @GetMapping("/admin/item-requests")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_EMPLOYEE', 'USER')")
    public String viewItemRequests(@RequestParam(required = false) Integer minQuantity,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String sort, Model model) {
        List<ItemRequest> requests = itemRequestRepository.findAll();

        if (minQuantity != null) {
            requests = requests.stream()
                    .filter(request -> request.getQuantity() >= minQuantity)
                    .toList();
        }

        if (status != null && !status.isEmpty()) {
            requests = requests.stream()
                    .filter(request -> request.getStatus().equalsIgnoreCase(status))
                    .toList();
        }

        if (sort != null) {
            switch (sort) {
                case "quantityAsc" -> requests.sort(Comparator.comparingInt(ItemRequest::getQuantity));
                case "quantityDesc" -> requests.sort((a, b) -> Integer.compare(b.getQuantity(), a.getQuantity()));
                case "statusAsc" -> requests.sort(Comparator.comparing(ItemRequest::getStatus));
                case "statusDesc" -> requests.sort((a, b) -> b.getStatus().compareToIgnoreCase(a.getStatus()));
            }
        }

        model.addAttribute("requests", requests);
        return "item-requests";
    }



    @GetMapping("/admin/item-requests/approve/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_EMPLOYEE')")
    public String approveRequest(@PathVariable Long id) {
        ItemRequest request = itemRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request ID: " + id));

        DistributionCentre sourceCentre = distributionCentreService.fetchCentreById(request.getSourceCentreId());
        Item requestedItem = null;

        if (sourceCentre != null) {
            requestedItem = sourceCentre.getItems().stream()
                    .filter(item -> item.getId().equals(request.getItemId()))
                    .findFirst()
                    .orElse(null);
        }

        if (requestedItem == null) {
            return "failed";
        }

        boolean transferSuccess = distributionCentreService.transferItem(
                request.getSourceCentreId(),
                request.getDestinationCentreId(),
                requestedItem.getName(),
                requestedItem.getBrand(),
                request.getQuantity(),
                requestedItem.getYearOfCreation(),
                requestedItem.getPrice()
        );

        if (!transferSuccess) {
            return "failed";
        }

        request.setStatus("Approved");
        itemRequestRepository.save(request);

        return "redirect:/admin/item-requests";
    }

    @GetMapping("/admin/item-requests/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteItemRequest(@PathVariable Long id) {
        itemRequestRepository.deleteById(id);
        return "redirect:/admin/item-requests";
    }

    @PostMapping("/admin/distribution-centres/{centreId}/inventory/delete/{itemName}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteItemFromCentre(@PathVariable Long centreId, @PathVariable String itemName) {
        distributionCentreService.deleteItem(centreId, itemName);
        return "redirect:/admin/distribution-centres/" + centreId + "/inventory";
    }

    @GetMapping("/admin/distribution-centres/{id}/inventory")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_EMPLOYEE', 'USER')")
    public String viewInventory(@PathVariable Long id,
                                @RequestParam(required = false) String search,
                                @RequestParam(required = false) String sort,
                                Model model) {
        DistributionCentre centre = distributionCentreService.fetchCentreById(id);
        if (centre == null) {
            model.addAttribute("error", "Distribution centre not found");
            return "error";
        }

        List<Item> items = centre.getItems();

        if (search != null && !search.trim().isEmpty()) {
            String lowerSearch = search.toLowerCase();
            items = items.stream()
                    .filter(item ->
                            item.getName().toLowerCase().contains(lowerSearch) ||
                                    item.getBrand().name().toLowerCase().contains(lowerSearch))
                    .toList();
        }

        if (sort != null) {
            switch (sort) {
                case "nameAsc" -> items.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                case "nameDesc" -> items.sort((a, b) -> b.getName().compareToIgnoreCase(a.getName()));
                case "brandAsc" -> items.sort((a, b) -> a.getBrand().name().compareToIgnoreCase(b.getBrand().name()));
                case "brandDesc" -> items.sort((a, b) -> b.getBrand().name().compareToIgnoreCase(a.getBrand().name()));
                case "quantityAsc" -> items.sort((a, b) -> Integer.compare(a.getQuantity(), b.getQuantity()));
                case "quantityDesc" -> items.sort((a, b) -> Integer.compare(b.getQuantity(), a.getQuantity()));
                case "yearAsc" -> items.sort((a, b) -> Integer.compare(a.getYearOfCreation(), b.getYearOfCreation()));
                case "yearDesc" -> items.sort((a, b) -> Integer.compare(b.getYearOfCreation(), a.getYearOfCreation()));
                case "priceAsc" -> items.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
                case "priceDesc" -> items.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
            }
        }

        centre.setItems(items);
        model.addAttribute("centre", centre);
        return "inventory";
    }
}