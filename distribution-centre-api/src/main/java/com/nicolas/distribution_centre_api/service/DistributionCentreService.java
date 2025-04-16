package com.nicolas.distribution_centre_api.service;

import com.nicolas.distribution_centre_api.dto.DistributionCentre;
import com.nicolas.distribution_centre_api.dto.Item;
import com.nicolas.distribution_centre_api.repository.DistributionCentreRepository;
import com.nicolas.distribution_centre_api.repository.ItemRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DistributionCentreService {

    private final DistributionCentreRepository distributionCentreRepository;
    private final ItemRepository itemRepository;

    public DistributionCentreService(DistributionCentreRepository distributionCentreRepository, ItemRepository itemRepository) {
        this.distributionCentreRepository = distributionCentreRepository;
        this.itemRepository = itemRepository;
    }

    @PostConstruct
    public void initData() {
        List<DistributionCentre> centres = new ArrayList<>();

        List<Item> items1 = new ArrayList<>();
        items1.add(new Item("T-Shirt", "Nike", 5));
        items1.add(new Item("Jeans", "Levi's", 4));
        DistributionCentre centre1 = new DistributionCentre("North Hub", 43.65107, -79.347015); // Toronto
        centre1.setItems(items1);

        List<Item> items2 = new ArrayList<>();
        items2.add(new Item("Jacket", "Columbia", 3));
        DistributionCentre centre2 = new DistributionCentre("West Hub", 49.282729, -123.120738); // Vancouver
        centre2.setItems(items2);

        DistributionCentre centre3 = new DistributionCentre("East Hub", 45.501689, -73.567256); // Montreal
        centre3.setItems(new ArrayList<>());

        List<Item> items4 = new ArrayList<>();
        items4.add(new Item("Sweater", "H&M", 40));
        items4.add(new Item("Cap", "Adidas", 25));
        items4.add(new Item("Scarf", "Zara", 15));
        DistributionCentre centre4 = new DistributionCentre("South Hub", 51.044733, -114.071883); // Calgary
        centre4.setItems(items4);

        centres.add(centre1);
        centres.add(centre2);
        centres.add(centre3);
        centres.add(centre4);

        distributionCentreRepository.saveAll(centres);
    }


    public List<DistributionCentre> getAllCentres() {
        return distributionCentreRepository.findAll();
    }

    public void addItem(Long centreId, Item itemDTO) {
        Optional<DistributionCentre> centreOpt = distributionCentreRepository.findById(centreId);
        centreOpt.ifPresent(centre -> {
            centre.getItems().add(itemDTO);
            distributionCentreRepository.save(centre);
        });
    }

    public void deleteItem(Long centreId, String itemName) {
        Optional<DistributionCentre> centreOpt = distributionCentreRepository.findById(centreId);
        centreOpt.ifPresent(centre -> {
            centre.getItems().removeIf(item -> item.getName().equalsIgnoreCase(itemName));
            distributionCentreRepository.save(centre);
        });
    }

    public boolean updateItem(Long centreId, String itemName, Item updatedItem) {
        Optional<DistributionCentre> centre = distributionCentreRepository.findById(centreId);
        if (centre.isPresent()) {
            Optional<Item> item = centre.get().getItems().stream()
                    .filter(i -> i.getName().equals(itemName))
                    .findFirst();
            if (item.isPresent()) {
                item.get().setQuantity(updatedItem.getQuantity());
                itemRepository.save(item.get());
                return true;
            }
        }
        return false;
    }


    public Optional<Item> requestItem(String brand, String name) {
        return distributionCentreRepository.findAll().stream()
                .flatMap(centre -> centre.getItems().stream())
                .filter(item -> item.getBrand().equalsIgnoreCase(brand) && item.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
