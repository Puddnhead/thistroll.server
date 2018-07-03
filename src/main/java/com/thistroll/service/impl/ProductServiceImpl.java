package com.thistroll.service.impl;

import com.thistroll.service.client.ProductService;

import java.util.*;

/**
 * Dummy product service implementation for Target interview
 *
 * Created by MVW on 7/2/2018.
 */
public class ProductServiceImpl implements ProductService {

    private static final Map<String, String> productsMap;

    static {
        productsMap = new HashMap<>();
        productsMap.put("10000000", "Time Machine");
        productsMap.put("10000001", "Anti-Gravity Boots");
        productsMap.put("10000002", "Sorcerer's Stone");
        productsMap.put("10000003", "Clone of Yourself");
        productsMap.put("10000004", "One Macaroni Noodle");
        productsMap.put("10000005", "United States Senator");
        productsMap.put("10000006", "Magic Carpet");
        productsMap.put("10000007", "Genetically Engineered Dinosaur Park");
        productsMap.put("10000008", "Cloak of Invisibility");
        productsMap.put("10000009", "Time Machine");
    }

    @Override
    public String getProductName(String productId) {
        return productsMap.get(productId);
    }

    @Override
    public List<String> getAllProductIds() {
        List<String> ids = new ArrayList<>(productsMap.keySet());
        Collections.sort(ids);
        return ids;
    }
}
