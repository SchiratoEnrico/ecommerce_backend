package com.betacom.ecommerce.backend.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


public class WebImgConfig implements WebMvcConfigurer {
	//cercherà imgs da uploads
	@Value("${app.uload.dir:uploads}")
	private String uploadDir;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
		String uploadLocation = "file:"+ uploadPath.toString() + "/";
		
		registry.addResourceHandler("/images/**")
				.addResourceLocations(uploadLocation);
	}
}
