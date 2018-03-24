package chris.microservices.customer;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import chris.microservices.customer.services.CustomerController;
import chris.microservices.customer.services.CustomerService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class CustomerBase {
	
	@Autowired
    private WebApplicationContext context;
	
	@Before
	public void setup() {
		RestAssuredMockMvc.webAppContextSetup(context);
	}
}
