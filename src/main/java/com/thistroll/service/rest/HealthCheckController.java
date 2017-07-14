package com.thistroll.service.rest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.thistroll.Pojo;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class HealthCheckController {

	@RequestMapping(value = "statuscheck", method = RequestMethod.GET)
	public @ResponseBody String statuscheck() {
		return "SUCCESS";
	}

	@RequestMapping(value = "test/{name}", method = RequestMethod.GET)
	public @ResponseBody Pojo jsonTest(@PathVariable String name) {
		Pojo pojo = new Pojo();
		pojo.setName(name);
		pojo.setDisposition("Satisfied");
		return pojo;
	}

}