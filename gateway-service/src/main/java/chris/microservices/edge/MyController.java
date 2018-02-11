package chris.microservices.edge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class MyController {
	
	@Value("${message:Hello default}")
    private String message;
	
	@RequestMapping("/message")
    String getMessage() {
        return this.message;
    }

}
