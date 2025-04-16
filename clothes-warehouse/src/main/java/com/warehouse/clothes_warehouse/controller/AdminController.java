package com.warehouse.clothes_warehouse.controller;

import com.warehouse.clothes_warehouse.model.DistributionCentre;
import com.warehouse.clothes_warehouse.model.Item;
import com.warehouse.clothes_warehouse.model.ItemRequest;
import com.warehouse.clothes_warehouse.repository.ItemRequestRepository;
import com.warehouse.clothes_warehouse.service.DistributionCentreService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/admin/distribution-centres")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewDistributionCentres(Model model) {
        List<DistributionCentre> centres = distributionCentreService.fetchAllCentres();
        model.addAttribute("centres", centres);
        return "distribution-centres";
    }

    @GetMapping("/admin/request-form")
    @PreAuthorize("hasRole('ADMIN')")
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

        Item selectedItem = null;
        selectedItem = distributionCentreService.findItemByIdInCentre(sourceCentreId, itemId);
        model.addAttribute("selectedItem", selectedItem);

        return "request-form";
    }


    @PostMapping("/admin/submit-request")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public String viewItemRequests(Model model) {
        List<ItemRequest> requests = itemRequestRepository.findAll();
        model.addAttribute("requests", requests);
        return "item-requests";
    }

    @GetMapping("/admin/item-requests/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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


    @GetMapping("/admin/distribution-centres/{id}/inventory")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewInventory(@PathVariable Long id, Model model) {
        DistributionCentre centre = distributionCentreService.fetchCentreById(id);
        if (centre != null) {
            model.addAttribute("centre", centre);
            return "inventory";
        } else {
            model.addAttribute("error", "Distribution centre not found");
            return "error";
        }
    }
}
