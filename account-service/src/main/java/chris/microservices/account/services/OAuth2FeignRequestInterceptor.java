package chris.microservices.account.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.util.Assert;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class OAuth2FeignRequestInterceptor implements RequestInterceptor {
	 
    private static final String AUTHORIZATION_HEADER = "Authorization";
 
    private static final String BEARER_TOKEN_TYPE = "Bearer";
 
    protected Logger logger = Logger.getLogger(OAuth2FeignRequestInterceptor.class.getName());
	
 
    private final OAuth2ClientContext oauth2ClientContext;
 
    public OAuth2FeignRequestInterceptor(OAuth2ClientContext oauth2ClientContext) {
        Assert.notNull(oauth2ClientContext, "Context can not be null");
        this.oauth2ClientContext = oauth2ClientContext;
    }
 
    @Override
    public void apply(RequestTemplate template) {
 
        if (template.headers().containsKey(AUTHORIZATION_HEADER)) {
        	logger.log(Level.WARNING, "The Authorization token has been already set");
        } else if (oauth2ClientContext.getAccessTokenRequest().getExistingToken() == null) {
        	logger.log(Level.WARNING, "Can not obtain existing token for request, if it is a non secured request, ignore.");
        } else {
        	logger.log(Level.ALL, "Constructing Header "+AUTHORIZATION_HEADER+" for Token " + BEARER_TOKEN_TYPE);
            template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE,
                    oauth2ClientContext.getAccessTokenRequest().getExistingToken().toString()));
        }
    }
}
