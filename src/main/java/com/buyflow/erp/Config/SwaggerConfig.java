package com.buyflow.erp.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	// Springdoc은 대부분 설정이 필요 없음 (자동 동작)
	
	//필요시 OpenAPI Bean으로 제목, 버전 등 커스터마이징.
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
					.title("BuyFlow ERP")
					.version("v1")
					.description("ERP 시스템 API 문서 "));
	}
}
