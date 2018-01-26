package chris.microservices.customer.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import chris.microservices.customer.intercomm.AccountClient;
import chris.microservices.customer.model.Account;
import chris.microservices.customer.model.Customer;
import chris.microservices.customer.model.CustomerType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
public class CustomerController {
	
	@Autowired
	private AccountClient accountClient;
	
	protected Logger logger = Logger.getLogger(CustomerController.class.getName());
	
	private List<Customer> customers;
	
	public CustomerController() {
		customers = new ArrayList<>();
		customers.add(new Customer(1, "1", "Customer 1", CustomerType.INDIVIDUAL));
		customers.add(new Customer(2, "2", "Customer 2", CustomerType.INDIVIDUAL));
		customers.add(new Customer(3, "2", "Customer 3", CustomerType.INDIVIDUAL));
		customers.add(new Customer(4, "3", "Customer 4", CustomerType.INDIVIDUAL));
	}
	
	@ApiOperation(value = "View account by doc", 
			authorizations = {@Authorization (value = "SWAGUI")})
	@RequestMapping(value="/customers/pesel/{pesel}", method=RequestMethod.GET, produces = "application/json")
	public Customer findByPesel(@PathVariable("pesel") String pesel) {
		logger.info(String.format("Customer.findByPesel(%s)", pesel));
		return customers.stream().filter(it -> it.getPesel().equals(pesel)).findFirst().get();	
	}
	
	@ApiOperation(value = "View all customers", notes = "", 
			authorizations = {@Authorization (value = "SWAGUI")})
	@RequestMapping(value = "/customers", method=RequestMethod.GET, produces = "application/json")
	public List<Customer> findAll() {
		logger.info("Customer.findAll()");
		return customers;
	}
	
	@ApiOperation(value = "View customer by ID", 
			authorizations = {@Authorization (value = "SWAGUI")})
	@RequestMapping(value="/customers/{id}", method=RequestMethod.GET, produces = "application/json")
	public Customer findById(@PathVariable("id") Integer id) {
		logger.info(String.format("Customer.findById(%s)", id));
		Customer customer = customers.stream().filter(it -> it.getId().intValue()==id.intValue()).findFirst().get();
		List<Account> accounts =  accountClient.getAccounts(id);
		customer.setAccounts(accounts);
		return customer;
	}
	
}
