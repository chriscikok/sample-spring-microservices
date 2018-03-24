package chris.microservices.account.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;

import chris.microservices.account.services.OAuth2FeignRequestInterceptor;

@Configuration
public class OAuth2FeignAutoConfiguration {
 
    @Bean
    @ConditionalOnBean(OAuth2ClientContext.class)
    public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2ClientContext oauth2ClientContext) {
        return new OAuth2FeignRequestInterceptor(oauth2ClientContext);
    }
}

