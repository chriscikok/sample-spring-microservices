package chris.web.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class User {
	@RequestMapping("/user")
	  public Principal user(Principal principal) {
	    return principal;
	  }
	
	@Value("${message:Hello default}")
    private String message;
	
	@RefreshScope
	@RequestMapping("/greeting")
	String getMessage() {
		return this.message;
	}
}
