package com.thistroll.service.client;

import java.util.List;

/**
 * Product service for a Target interview
 *
 * Created by MVW on 7/2/2018.
 */
public interface ProductService {

    /**
     * Returns a product name for a given id
     *
     * @param productId product id
     * @return a product name
     */
    String getProductName(String productId);

    /**
     * Returns a list of all possible product ids. Primitive.
     *
     * @return a list of product ids
     */
    List<String> getAllProductIds();
}
