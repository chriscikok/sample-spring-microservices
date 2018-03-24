package chris.microservices.saml;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RedirectController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String redirect() {
		return "redirect";
	}
	
	@ModelAttribute("RelayState")
	public String getRelayState() {
		return "http://ubuntu.chris.com:8080/openam/XUI/#profile/details";
	}
	@ModelAttribute("SAMLResponse") 
	public String getSAMLResponse(HttpSession session) {
		EncryptAndSignAssertion esAssertion = new EncryptAndSignAssertion();
		String SAMLResponse = esAssertion.getEncryptAndSign(session.getId());
		System.out.println("encoded SAMLResponse" + SAMLResponse);
		return SAMLResponse;
	}
	@ModelAttribute("Action")
	public String getAction() {
		return "http://ubuntu.chris.com:8080/openam/Consumer/metaAlias/sp";
	}
}
