package chris.microservices.saml;

import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.SecurityConfiguration;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.util.XMLHelper;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class SignAssertion {

	protected static Logger logger = Logger.getLogger(SignAssertion.class.getName());

	    private static Credential signingCredential = null;
	    final static String password = "password";
	    final static String certificateAliasName = "tomcat";

	    @SuppressWarnings("static-access")
	    private void intializeCredentials() {
	        KeyStore ks = null;
	        char[] password = this.password.toCharArray();
	        

	        // Get Default Instance of KeyStore
	        try {
	            ks = KeyStore.getInstance(KeyStore.getDefaultType());
	        } catch (KeyStoreException e) {
	        	logger.log(Level.SEVERE, "Error while Intializing Keystore", e);
	        }

	        // /Read and Load KeyStore
	        try {
	        	FileInputStream is = new FileInputStream("/Users/chris/EclipseProjects/sample-spring-microservices/ssl/keystore");
	        	ks.load(is, password);
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	logger.log(Level.SEVERE, "Failed to Load the KeyStore:: ", e);
	        }

	        // Get Private Key Entry From Certificate
	        KeyStore.PrivateKeyEntry pkEntry = null;
	        try {
	            pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(this.certificateAliasName, new KeyStore.PasswordProtection(
	                    this.password.toCharArray()));
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	logger.log(Level.SEVERE, "Failed to Get Private Entry From the keystore", e);
	        }

	        PrivateKey pk = pkEntry.getPrivateKey();

	        X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();
	        BasicX509Credential credential = new BasicX509Credential();
	        credential.setEntityCertificate(certificate);
	        credential.setPrivateKey(pk);
	        signingCredential = credential;

	        logger.info("Private Key loaded");

	    }

	    public static void main(String args[]) throws Exception {
	        SignAssertion sign = new SignAssertion();
	        sign.intializeCredentials();
	        try {
	            DefaultBootstrap.bootstrap();
	        } catch (ConfigurationException e) {
	        	logger.log(Level.SEVERE, "Configuration exception");
	        }
	        Signature signature = (Signature) Configuration
	                .getBuilderFactory()
	                .getBuilder(Signature.DEFAULT_ELEMENT_NAME)
	                .buildObject(Signature.DEFAULT_ELEMENT_NAME);

	        signature.setSigningCredential(signingCredential);
	        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
	         signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);

	        // This is also the default if a null SecurityConfiguration is specified
	        SecurityConfiguration secConfig = Configuration.getGlobalSecurityConfiguration();
	        // If null this would result in the default KeyInfoGenerator being used

	        String keyInfoGeneratorProfile = "XMLSignature";

	        try {
	            SecurityHelper.prepareSignatureParams(signature, signingCredential, secConfig, null);
	        } catch (Exception e) {
	        	logger.log(Level.SEVERE, "Couldn't prepare signature");
	        }

	        Response resp = (Response) Configuration
	                .getBuilderFactory()
	                .getBuilder(Response.DEFAULT_ELEMENT_NAME)
	                .buildObject(Response.DEFAULT_ELEMENT_NAME);

	        resp.setID("HIHI");
	        resp.setIssueInstant(new DateTime());
	        
	        Status status = (Status) Configuration
	        		.getBuilderFactory()
	        		.getBuilder(Status.DEFAULT_ELEMENT_NAME)
	        		.buildObject(Status.DEFAULT_ELEMENT_NAME);
	        
	        StatusCode statusCode = (StatusCode) Configuration
	        		.getBuilderFactory()
	        		.getBuilder(StatusCode.DEFAULT_ELEMENT_NAME)
	        		.buildObject(StatusCode.DEFAULT_ELEMENT_NAME);
	        
	        statusCode.setValue(StatusCode.SUCCESS_URI);
	        status.setStatusCode(statusCode);
	        resp.setStatus(status);
	        
	        resp.getAssertions().add(SAMLWriter.getSamlAssertion());

	        resp.setSignature(signature);

	        try {
	            Configuration.getMarshallerFactory()
	                    .getMarshaller(resp)
	                    .marshall(resp);
	        } catch (MarshallingException e) {
	        	logger.log(Level.SEVERE, "Couldn't marshall");
	        }

	        try {
	            Signer.signObject(signature);
	        } catch (SignatureException e) {
	        	logger.log(Level.SEVERE, "Couldn't sign object");
	        }
	        ResponseMarshaller marshaller = new ResponseMarshaller();
	        Element plain = marshaller.marshall(resp);
	        // response.setSignature(sign);
	        String samlResponse = XMLHelper.nodeToString(plain);

	        logger.info("********************\n*\n***********::" + samlResponse);
	    }
	
}
