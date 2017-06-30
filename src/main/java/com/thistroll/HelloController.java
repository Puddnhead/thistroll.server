package com.thistroll;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class HelloController {

	@RequestMapping(value = "{name}", method = RequestMethod.GET)
	public @ResponseBody Pojo simpleResponse(@PathVariable String name) {
		Pojo pojo = new Pojo();
		pojo.setName(name);
		pojo.setDisposition("Satisfied");
		return pojo;
	}
}