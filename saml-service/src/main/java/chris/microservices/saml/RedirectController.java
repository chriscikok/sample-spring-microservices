package chris.microservices.saml;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import chris.microservices.saml.config.EncryptAndSignConfig;

@Controller
public class RedirectController {
	
	@Autowired
	EncryptAndSignConfig myConfig;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String redirect(HttpSession session) {
		return "redirect";
	}
	
	@ModelAttribute("RelayState")
	public String getRelayState() {
		return "http://ubuntu.chris.com:8080/openam/XUI/#profile/details";
	}
	@ModelAttribute("SAMLResponse") 
	public String getSAMLResponse(HttpSession session) {
		EncryptAndSignAssertion esAssertion = new EncryptAndSignAssertion(myConfig);
		String SAMLResponse = esAssertion.getEncryptAndSign(session.getId());
		System.out.println("encoded SAMLResponse" + SAMLResponse);
		return SAMLResponse;
	}
	@ModelAttribute("Action")
	public String getAction() {
		return "http://ubuntu.chris.com:8080/openam/Consumer/metaAlias/sp";
	}
}
