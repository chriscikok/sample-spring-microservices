package chris.microservices.saml.config;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.opensaml.xml.security.credential.Credential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter @Setter
public class EncryptAndSignConfig {

	private Credential signingCredential = null;
    private Credential encryptCredential = null;
    
    private String strSigningKeyStore = "keystore/mac_openam_keystore.jks";
    private String strEncryptKeyCER = "keystore/sp.cer";
    
    @Value("${my.password}")
    private String password;
    
    private String certificateAliasName = "test";
    private String strRespId = DigestUtils.sha1Hex(new DateTime().toString());
    private String strRespDestination = "http://ubuntu.chris.com:8080/openam/Consumer/metaAlias/sp";
    private String strRespIssuer = "http://mac.chris.com:10080/openam";
}
