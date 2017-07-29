package com.thistroll.service.rest;

import org.springframework.stereotype.Controller;
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

}