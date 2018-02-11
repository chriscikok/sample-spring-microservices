package chris.microservices.account.intercomm;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import chris.microservices.account.config.OAuth2FeignAutoConfiguration;
import chris.microservices.account.model.CustomerServiceResponse;

@FeignClient(name="customer-service", configuration = OAuth2FeignAutoConfiguration.class)
public interface CustomerClient {

    @RequestMapping(method = RequestMethod.GET, value = "/customers/customer/{id}")
    CustomerServiceResponse verify(@PathVariable("id") Integer id);
    
}