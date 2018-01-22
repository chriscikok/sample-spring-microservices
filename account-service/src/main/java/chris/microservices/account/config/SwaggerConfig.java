package chris.microservices.account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Contact;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import java.util.Collections;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {  
	
	@Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.basePackage("chris.microservices.account.services"))              
          .paths(PathSelectors.any())                          
          .build()
          .apiInfo(metaData())
          ;                                           
    }

    /*@Bean
    public SecurityConfiguration security() {
        return new springfox.documentation.swagger.web.SecurityConfiguration(
            null, null, null,
            "myapp-api",
            "access_token",
            ApiKeyVehicle.QUERY_PARAM,
            "XXXXXX",
            null);
    }*/
    

    private ApiInfo metaData() {
    	ApiInfo apiInfo = new ApiInfo(
                "Spring Boot REST API",
                "Spring Boot REST API for Account Services",
                "1.0",
                "Terms of service",
                new Contact("Chris Kok", "https://github.com/chriscikok", "chris.kok@gmail.com"),
               "Apache License Version 2.0",
                "https://www.apache.org/licenses/LICENSE-2.0",
                Collections.emptyList());
        return apiInfo;
    }
}
