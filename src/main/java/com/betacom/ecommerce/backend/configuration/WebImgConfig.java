package com.betacom.ecommerce.backend.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebImgConfig implements WebMvcConfigurer {
	//cercherà imgs da uploads
	@Value("${app.upload.dir:uploads}")
	private String uploadDir;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
		String uploadLocation = "file:"+ uploadPath.toString() + "/";
		log.debug("uploadLocation");
		registry.addResourceHandler("/**")
				.addResourceLocations(uploadLocation);
	}
}
