package chris.microservices.customer.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.TokenEndpoint;
import springfox.documentation.service.TokenRequestEndpoint;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationCodeGrant;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableAutoConfiguration
public class SwaggerConfig {  
	
	@Bean
    public Docket api() { 
		
		List <SecurityScheme> ss = new ArrayList<SecurityScheme>();
		ss.add(oauth2());
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.basePackage("chris.microservices.customer.services"))              
          .paths(PathSelectors.any())                          
          .build()
          .apiInfo(metaData())
          .securitySchemes(ss)
          ;                                           
    }
    
	@Bean
	SecurityScheme oauth2() {
		return new OAuthBuilder().name("SWAGUI").grantTypes(grantTypes()).scopes(scopes()).build();
	}
	
	@Bean
	  SecurityConfiguration security() {
	    return SecurityConfigurationBuilder.builder()
	        .scopeSeparator(",")
	        .additionalQueryStringParams(null)
	        .useBasicAuthenticationWithAccessCodeGrant(false)
	        .build();
	  }
	
	List<AuthorizationScope> scopes() {
		List<AuthorizationScope> as = new ArrayList<AuthorizationScope>();
		as.add(new AuthorizationScope("profile", "Read access on API"));
		return as;
	}
	
	List<GrantType> grantTypes(){
		String accessTokenUri = "http://ubuntu.chris.com:8080/openam/oauth2/access_token";
		String userAuthorizationUri = "http://ubuntu.chris.com:8080/openam/oauth2/authorize";
		TokenRequestEndpoint tRE = new TokenRequestEndpoint(userAuthorizationUri, "client_id", "client_secret");
		TokenEndpoint tE = new TokenEndpoint(accessTokenUri, "access_token");
		AuthorizationCodeGrant code = new AuthorizationCodeGrant(tRE, tE);
		List <GrantType> ig = new ArrayList<GrantType>();
		ig.add(code);
		return ig;
	}

    private ApiInfo metaData() {
    	ApiInfo apiInfo = new ApiInfo(
                "Customer Servives",
                "Spring Boot REST API",
                "1.0",
                "Terms of service",
                new Contact("Chris Kok", "https://github.com/chriscikok", "chris.kok@gmail.com"),
               "Apache License Version 2.0",
                "https://www.apache.org/licenses/LICENSE-2.0",
                Collections.emptyList());
        return apiInfo;
    }
}
