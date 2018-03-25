package chris.microservices.saml;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.EncryptedAssertion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.saml2.encryption.Encrypter;
import org.opensaml.saml2.encryption.Encrypter.KeyPlacement;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.encryption.EncryptionConstants;
import org.opensaml.xml.encryption.EncryptionException;
import org.opensaml.xml.encryption.EncryptionParameters;
import org.opensaml.xml.encryption.KeyEncryptionParameters;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.security.SecurityConfiguration;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.SignatureException;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

import chris.microservices.saml.config.EncryptAndSignConfig;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class EncryptAndSignAssertion {

	protected static Logger logger = Logger.getLogger(EncryptAndSignAssertion.class.getName());
	
	@Autowired
	EncryptAndSignConfig config;
	
	public EncryptAndSignAssertion(EncryptAndSignConfig config) {
		this.config = config;
	}
    
	
    private void intializeCredentials() {
        KeyStore ks = null;
        char[] password = config.getPassword().toCharArray();
        
        // Get Default Instance of KeyStore
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            //ks = KeyStore.getInstance("JCEKS");
        } catch (KeyStoreException e) {
        	logger.log(Level.SEVERE, "Error while Intializing Keystore", e);
        }

        // /Read and Load KeyStore
        try {
        	InputStream is = new ClassPathResource(config.getStrSigningKeyStore()).getInputStream();
;        	ks.load(is, password);
        } catch (Exception e) {
        	e.printStackTrace();
        	logger.log(Level.SEVERE, "Failed to Load the KeyStore:: ", e);
        }

        // Get Private Key Entry From Certificate
        KeyStore.PrivateKeyEntry pkEntry = null;
        try {
            pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(config.getCertificateAliasName(), new KeyStore.PasswordProtection(
                    config.getPassword().toCharArray()));
        } catch (Exception e) {
        	e.printStackTrace();
        	logger.log(Level.SEVERE, "Failed to Get Private Entry From the keystore", e);
        }

        PrivateKey pk = pkEntry.getPrivateKey();

        X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();
        BasicX509Credential credential = new BasicX509Credential();
        credential.setEntityCertificate(certificate);
        credential.setPrivateKey(pk);
        config.setSigningCredential(credential);

        logger.info("Private Key loaded");
        
        X509Certificate certificate2 = null;
        try{
        	InputStream is = new ClassPathResource(config.getStrEncryptKeyCER()).getInputStream();
        	CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        	certificate2 = (X509Certificate) certFactory.generateCertificate(is);
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Failed to load encryption key", e);
        }
        BasicX509Credential credential2 = new BasicX509Credential();
        credential2.setEntityCertificate(certificate2);
        config.setEncryptCredential(credential2);
        
        logger.info("Encrypt Key loaded");
    }
    
    public String getEncryptAndSign(String sessionId) {
    	
        intializeCredentials();
        
        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException e) {
        	logger.log(Level.SEVERE, "Configuration exception");
        }
        
        //Handle Signature
        Signature signature = (Signature) Configuration
                .getBuilderFactory()
                .getBuilder(Signature.DEFAULT_ELEMENT_NAME)
                .buildObject(Signature.DEFAULT_ELEMENT_NAME);

        signature.setSigningCredential(config.getSigningCredential());
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
         signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);

        SecurityConfiguration secConfig = Configuration.getGlobalSecurityConfiguration();
        
        //String keyInfoGeneratorProfile = "XMLSignature";

        try {
            SecurityHelper.prepareSignatureParams(signature, config.getSigningCredential(), secConfig, null);
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Couldn't prepare signature");
        }
        
        Assertion assertion = SAMLWriter.getSamlAssertion(sessionId);
        assertion.setSignature(signature);
        
        try {
            Configuration.getMarshallerFactory()
                    .getMarshaller(assertion)
                    .marshall(assertion);
        } catch (MarshallingException e) {
        	logger.log(Level.SEVERE, "Couldn't marshall");
        }

        try {
            Signer.signObject(signature);
        } catch (SignatureException e) {
        	logger.log(Level.SEVERE, "Couldn't sign object");
        }
        
        //Handle Encryption
        EncryptionParameters encParams = new EncryptionParameters();
        encParams.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        
        KeyEncryptionParameters kekParams = new KeyEncryptionParameters();
        kekParams.setEncryptionCredential(config.getEncryptCredential());
        kekParams.setAlgorithm(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        KeyInfoGeneratorFactory kigf = org.opensaml.xml.Configuration.getGlobalSecurityConfiguration()
        		.getKeyInfoGeneratorManager()
        		.getDefaultManager().getFactory(config.getEncryptCredential());
        kekParams.setKeyInfoGenerator(kigf.newInstance());
        Encrypter samlEncrypter = new Encrypter(encParams, kekParams);
        samlEncrypter.setKeyPlacement(KeyPlacement.PEER);
        EncryptedAssertion encryptedAssertion = null;
        
        try {
        	encryptedAssertion = samlEncrypter.encrypt(assertion);
        } catch (EncryptionException e) {
        	logger.log(Level.SEVERE, "Couldn't encrypt assertion", e);
        }
        
        
        //Construct SAMLResponse
        Response resp = (Response) Configuration
                .getBuilderFactory()
                .getBuilder(Response.DEFAULT_ELEMENT_NAME)
                .buildObject(Response.DEFAULT_ELEMENT_NAME);

        resp.setID(config.getStrRespId());
        resp.setIssueInstant(new DateTime());
        resp.setDestination(config.getStrRespDestination());
        
        SAMLObjectBuilder issuerBuilder = null;
        try {
			issuerBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
		} catch (ConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        Issuer issuer = (Issuer) issuerBuilder.buildObject();
        issuer.setValue(config.getStrRespIssuer());
		resp.setIssuer(issuer);
        
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
        
        //resp.getAssertions().add(assertion);
        resp.getEncryptedAssertions().add(encryptedAssertion);
        
        ResponseMarshaller marshaller = new ResponseMarshaller();
        Element plain;
        String samlResponse = null;
		try {
			plain = marshaller.marshall(resp);
			samlResponse = XMLHelper.nodeToString(plain);
		} catch (MarshallingException e) {
			e.printStackTrace();
		}
		
		System.out.println("SAMLResponse:" + samlResponse);
        return Base64.getEncoder().encodeToString(samlResponse.getBytes(StandardCharsets.UTF_8));
    }
	
}
