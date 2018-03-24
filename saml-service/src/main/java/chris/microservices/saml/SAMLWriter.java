package chris.microservices.saml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Condition;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.OneTimeUse;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml2.core.impl.AssertionMarshaller;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.security.SecurityConfiguration;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoGenerator;
import org.opensaml.xml.security.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xml.security.keyinfo.KeyInfoGeneratorManager;
import org.opensaml.xml.security.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.signature.X509Certificate;
import org.opensaml.xml.signature.X509Data;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

import lombok.Data;
 
/**
 * This is a demo class which creates a valid SAML 2.0 Assertion.
 */
public class SAMLWriter
{
	
	protected static Logger logger = Logger.getLogger(SAMLWriter.class.getName());
	
	public static Assertion getSamlAssertion(String sessionId) {
		
		SAMLInputContainer input = new SAMLInputContainer();
		input.setStrIssuer("http://mac.chris.com:10080/openam");
		input.setStrNameID("idpuser");
		input.setStrNameQualifier("http://mac.chris.com:10080/openam");
		input.setSessionId(sessionId);
		input.setStrSPNameQualifier("http://ubuntu.chris.com:8080/openam");
		input.setStrRecipient("http://ubuntu.chris.com:8080/openam/Consumer/metaAlias/sp");
		input.setStrAudienceURI("http://ubuntu.chris.com:8080/openam");
		input.setStrAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
		//input.setStrID(DigestUtils.sha1Hex(new DateTime().toString()));
		input.setStrID(DigestUtils.sha1Hex(new DateTime().toString()));
		Map customAttributes = new HashMap();
		customAttributes.put("uid", "idpuser");
		input.setAttributes(customAttributes);
		
		return SAMLWriter.buildDefaultAssertion(input);
	}
 
	private static XMLObjectBuilderFactory builderFactory;
 
	public static XMLObjectBuilderFactory getSAMLBuilder() throws ConfigurationException{
 
		if(builderFactory == null){
			// OpenSAML 2.3
			 DefaultBootstrap.bootstrap();
	         builderFactory = Configuration.getBuilderFactory();
		}
 
		return builderFactory;
	}
 
	/**
	 * Builds a SAML Attribute of type String
	 * @param name
	 * @param value
	 * @param builderFactory
	 * @return
	 * @throws ConfigurationException
	 */
	public static Attribute buildStringAttribute(String name, String value, XMLObjectBuilderFactory builderFactory) throws ConfigurationException
	{
		SAMLObjectBuilder attrBuilder = (SAMLObjectBuilder) getSAMLBuilder().getBuilder(Attribute.DEFAULT_ELEMENT_NAME);
		 Attribute attrFirstName = (Attribute) attrBuilder.buildObject();
		 attrFirstName.setName(name);
 
		 // Set custom Attributes
		 XMLObjectBuilder stringBuilder = getSAMLBuilder().getBuilder(XSString.TYPE_NAME);
		 XSString attrValueFirstName = (XSString) stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
		 attrValueFirstName.setValue(value);
 
		 attrFirstName.getAttributeValues().add(attrValueFirstName);
		return attrFirstName;
	}
 
	/**
	 * Helper method which includes some basic SAML fields which are part of almost every SAML Assertion.
	 *
	 * @param input
	 * @return
	 */
	public static Assertion buildDefaultAssertion(SAMLInputContainer input)
	{
		try
		{
	         // Create the NameIdentifier
	         SAMLObjectBuilder nameIdBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(NameID.DEFAULT_ELEMENT_NAME);
	         NameID nameId = (NameID) nameIdBuilder.buildObject();
	         nameId.setValue(input.getStrNameID());
	         nameId.setNameQualifier(input.getStrNameQualifier());
	         nameId.setSPNameQualifier(input.getStrSPNameQualifier());
	         nameId.setFormat(NameID.TRANSIENT);
 
	         // Create the SubjectConfirmation
	         SAMLObjectBuilder confirmationMethodBuilder = (SAMLObjectBuilder)  SAMLWriter.getSAMLBuilder().getBuilder(SubjectConfirmationData.DEFAULT_ELEMENT_NAME);
	         SubjectConfirmationData confirmationMethod = (SubjectConfirmationData) confirmationMethodBuilder.buildObject();
	         DateTime now = new DateTime();
	         //confirmationMethod.setNotBefore(now);
	         confirmationMethod.setNotOnOrAfter(now.plusMinutes(2));
	         confirmationMethod.setRecipient(input.getStrRecipient());
	         
 
	         SAMLObjectBuilder subjectConfirmationBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(SubjectConfirmation.DEFAULT_ELEMENT_NAME);
	         SubjectConfirmation subjectConfirmation = (SubjectConfirmation) subjectConfirmationBuilder.buildObject();
	         subjectConfirmation.setSubjectConfirmationData(confirmationMethod);
	         subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);
 
	         // Create the Subject
	         SAMLObjectBuilder subjectBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(Subject.DEFAULT_ELEMENT_NAME);
	         Subject subject = (Subject) subjectBuilder.buildObject();
 
	         subject.setNameID(nameId);
	         subject.getSubjectConfirmations().add(subjectConfirmation);
 
	         // Create Authentication Statement
	         SAMLObjectBuilder authStatementBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(AuthnStatement.DEFAULT_ELEMENT_NAME);
	         AuthnStatement authnStatement = (AuthnStatement) authStatementBuilder.buildObject();
	         //authnStatement.setSubject(subject);
	         //authnStatement.setAuthenticationMethod(strAuthMethod);
	         DateTime now2 = new DateTime();
	         authnStatement.setAuthnInstant(now2);
	         authnStatement.setSessionIndex(input.getSessionId());
	         //authnStatement.setSessionNotOnOrAfter(now2.plus(input.getMaxSessionTimeoutInMinutes()));
 
	         SAMLObjectBuilder authContextBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(AuthnContext.DEFAULT_ELEMENT_NAME);
	         AuthnContext authnContext = (AuthnContext) authContextBuilder.buildObject();
 
	         SAMLObjectBuilder authContextClassRefBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(AuthnContextClassRef.DEFAULT_ELEMENT_NAME);
	         AuthnContextClassRef authnContextClassRef = (AuthnContextClassRef) authContextClassRefBuilder.buildObject();
	         authnContextClassRef.setAuthnContextClassRef(input.getStrAuthnContextClassRef());
 
			authnContext.setAuthnContextClassRef(authnContextClassRef);
	        authnStatement.setAuthnContext(authnContext);
 
	        // Builder Attributes
	         SAMLObjectBuilder attrStatementBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(AttributeStatement.DEFAULT_ELEMENT_NAME);
	         AttributeStatement attrStatement = (AttributeStatement) attrStatementBuilder.buildObject();
 
	      // Create the attribute statement
	         Map attributes = input.getAttributes();
	         if(attributes != null){
	        	 Set<String>keySet = attributes.keySet();
	        	 for (String key : keySet)
				{
	        		 Attribute attrFirstName = buildStringAttribute(key, (String)attributes.get(key), getSAMLBuilder());
	        		 attrStatement.getAttributes().add(attrFirstName);
				}
	         }
 
	         // Create the do-not-cache condition
	         // SAMLObjectBuilder doNotCacheConditionBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(OneTimeUse.DEFAULT_ELEMENT_NAME);
	         //Condition condition = (Condition) doNotCacheConditionBuilder.buildObject();
	         SAMLObjectBuilder conditionsBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(Conditions.DEFAULT_ELEMENT_NAME);
	         Conditions conditions = (Conditions) conditionsBuilder.buildObject();
	         conditions.setNotBefore(now2);
	         conditions.setNotOnOrAfter(now2.plus(input.getMaxSessionTimeoutInMinutes() * 1000 * 60));
	         
	         SAMLObjectBuilder audienceRestrictionBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(AudienceRestriction.DEFAULT_ELEMENT_NAME);
	         AudienceRestriction audienceRestriction = (AudienceRestriction) audienceRestrictionBuilder.buildObject();
	         
	         SAMLObjectBuilder audienceBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(Audience.DEFAULT_ELEMENT_NAME);
	         Audience audience = (Audience) audienceBuilder.buildObject();
	         
	         audience.setAudienceURI(input.getStrAudienceURI());
	         audienceRestriction.getAudiences().add(audience);
	         conditions.getAudienceRestrictions().add(audienceRestriction);
	         
	         //conditions.getConditions().add(condition);
 
	         // Create Issuer
	         SAMLObjectBuilder issuerBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
	         Issuer issuer = (Issuer) issuerBuilder.buildObject();
	         issuer.setValue(input.getStrIssuer());
 
	         // Create the assertion
	         SAMLObjectBuilder assertionBuilder = (SAMLObjectBuilder) SAMLWriter.getSAMLBuilder().getBuilder(Assertion.DEFAULT_ELEMENT_NAME);
	         Assertion assertion = (Assertion) assertionBuilder.buildObject();
	         assertion.setID(input.getStrID());
	         assertion.setIssuer(issuer);
	         assertion.setIssueInstant(now);
	         assertion.setVersion(SAMLVersion.VERSION_20);
	         
	         assertion.getAuthnStatements().add(authnStatement);
	         assertion.getAttributeStatements().add(attrStatement);
	         assertion.setConditions(conditions);
	         
	         assertion.setSubject(subject);
	         
	         AssertionMarshaller marshaller = new AssertionMarshaller();
	         Element plain = null;
	         try {
	        	 plain = marshaller.marshall(assertion);
	         }catch (MarshallingException e){
	        	 e.printStackTrace();
	         }
	         System.out.println("SAMLAssertion:" + XMLHelper.nodeToString(plain));
			return assertion;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
 
}