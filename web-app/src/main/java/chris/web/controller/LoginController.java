package chris.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
	@RequestMapping(value = "/", method = RequestMethod.GET)
    public String landing() {
        return "landing";
    }
	
	@Value("${message:Hello default}")
    private String message;
	
	@RequestMapping("/message")
    String getMessage() {
        return this.message;
    }

}
