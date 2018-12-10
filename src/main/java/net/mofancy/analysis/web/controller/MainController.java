package net.mofancy.analysis.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
	
	
	
	@RequestMapping("index")
	public String index() {
		return "index";
	}
	
	@RequestMapping("ybp")
	public String ybp() {
		return "ybp";
	}
	
	@RequestMapping("detail")
	public String detail(ModelMap modelMap) {
		return "detail";
	}
	@RequestMapping("themeDetail")
	public String themeDetail() {
		return "theme_detail";
	}
	@RequestMapping("familyDetail")
	public String familyDetail() {
		return "family_detail";
	}
	@RequestMapping("dataPool")
	public String dataPool() {
		return "data_pool";
	}
	
	@RequestMapping("login")
	public String login() {
		return "login";
	}
	
	@RequestMapping("ybpDetail")
	public String ybpDetail() {
		return "ybp_detail";
	}
	
	@RequestMapping("segmentDetail")
	public String marketDetail() {
		return "segment_detail";
	}
	
	
}


