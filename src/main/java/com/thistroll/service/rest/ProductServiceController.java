package com.thistroll.service.rest;

import com.thistroll.service.client.ProductService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Rest layer for {@link ProductService}
 * Created by MVW on 7/2/2018.
 */
@Controller
@RequestMapping("/product")
public class ProductServiceController implements ProductService {

    private ProductService productService;

    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    @Override
    public @ResponseBody String getProductName(@PathVariable String productId) {
        return productService.getProductName(productId);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @Override
    public @ResponseBody List<String> getAllProductIds() {
        return productService.getAllProductIds();
    }

    @Required
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
