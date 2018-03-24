package chris.microservices.saml;

import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class SAMLInputContainer {

	private String strAudienceURI;
	private String strRecipient;
	private String strIssuer;
	private String strNameID;
	private String strNameQualifier;
	private String sessionId;
	private int maxSessionTimeoutInMinutes = 15; // default is 15 minutes
	private String strSPNameQualifier;
	private String strAuthnContextClassRef;
	private String strID;
	private Map attributes;
}
