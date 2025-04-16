package com.warehouse.clothes_warehouse.service;

import com.warehouse.clothes_warehouse.model.Brand;
import com.warehouse.clothes_warehouse.model.DistributionCentre;
import com.warehouse.clothes_warehouse.model.Item;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DistributionCentreService {

    private static final String API_URL = "http://localhost:8080/api/distribution-centres";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "password";

    private final RestTemplate restTemplate;

    public DistributionCentreService() {
        this.restTemplate = new RestTemplate();
    }

    public List<DistributionCentre> fetchAllCentres() {
        ResponseEntity<List<DistributionCentre>> response = restTemplate.exchange(
                API_URL,
                HttpMethod.GET,
                createAuthEntity(),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public DistributionCentre fetchCentreById(Long id) {
        return fetchAllCentres().stream()
                .filter(centre -> centre.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void addItem(Long centreId, String itemName, Brand itemBrand, int quantity, int year, double price) {
        String url = API_URL + "/" + centreId + "/items";
        Item item = new Item(itemName, itemBrand, quantity, year, price);
        HttpEntity<Item> requestEntity = createAuthEntity(item);
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
    }

    public void deleteItem(Long centreId, String itemName) {
        String url = API_URL + "/" + centreId + "/items/" + itemName;
        restTemplate.exchange(url, HttpMethod.DELETE, createAuthEntity(), Void.class);
    }

    public boolean transferItem(Long sourceCentreId, Long targetCentreId, String itemName, Brand itemBrand, int quantity, int year, double price) {
        DistributionCentre sourceCentre = fetchCentreById(sourceCentreId);
        DistributionCentre targetCentre = fetchCentreById(targetCentreId);

        if (sourceCentre == null || targetCentre == null) return false;

        Item sourceItem = sourceCentre.getItems().stream()
                .filter(item -> item.getName().equalsIgnoreCase(itemName)
                        && item.getBrand().name().equalsIgnoreCase(itemBrand.name()))
                .findFirst()
                .orElse(null);

        if (sourceItem == null || sourceItem.getQuantity() < quantity) return false;

        sourceItem.setQuantity(sourceItem.getQuantity() - quantity);
        updateItem(sourceCentreId, sourceItem);

        Item targetItem = new Item(itemName, itemBrand, quantity, year, price);
        HttpEntity<Item> requestEntity = createAuthEntity(targetItem);
        String url = API_URL + "/" + targetCentreId + "/items";
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);

        return true;
    }

    private void updateItem(Long centreId, Item item) {
        String url = API_URL + "/" + centreId + "/items/" + item.getName();
        HttpEntity<Item> request = createAuthEntity(item);
        restTemplate.exchange(url, HttpMethod.PUT, request, Void.class);
    }

    public Item findItemByIdInCentre(Long centreId, Long itemId) {
        DistributionCentre centre = fetchCentreById(centreId);
        if (centre == null) return null;

        return centre.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(USERNAME, PASSWORD);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private <T> HttpEntity<T> createAuthEntity(T body) {
        return new HttpEntity<>(body, createAuthHeaders());
    }

    private HttpEntity<Void> createAuthEntity() {
        return new HttpEntity<>(createAuthHeaders());
    }
}
