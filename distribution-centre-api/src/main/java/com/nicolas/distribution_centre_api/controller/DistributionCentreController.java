package com.nicolas.distribution_centre_api.controller;

import com.nicolas.distribution_centre_api.dto.DistributionCentre;
import com.nicolas.distribution_centre_api.dto.Item;
import com.nicolas.distribution_centre_api.service.DistributionCentreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distribution-centres")
public class DistributionCentreController {

    private final DistributionCentreService service;

    public DistributionCentreController(DistributionCentreService service) {
        this.service = service;
    }

    @GetMapping
    public List<DistributionCentre> getAllCentres() {
        return service.getAllCentres();
    }

    @PostMapping("/{centreId}/items")
    public ResponseEntity<Void> addItem(@PathVariable Long centreId, @RequestBody Item item) {
        service.addItem(centreId, item);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{centreId}/items/{itemName}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long centreId, @PathVariable String itemName) {
        service.deleteItem(centreId, itemName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items/search")
    public ResponseEntity<Item> requestItem(@RequestParam String brand, @RequestParam String name) {
        return service.requestItem(brand, name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{centreId}/items/{itemName}")
    public ResponseEntity<Void> updateItem(@PathVariable Long centreId, @PathVariable String itemName, @RequestBody Item updatedItem) {
        boolean isUpdated = service.updateItem(centreId, itemName, updatedItem);
        if (isUpdated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
