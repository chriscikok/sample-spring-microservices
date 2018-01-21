package chris.microservices.account.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chris.microservices.account.model.Account;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController 

public class AccountController {

	private List<Account> accounts;
	
	protected Logger logger = Logger.getLogger(AccountController.class.getName());
	
	public AccountController() {
		accounts = new ArrayList<>();
		accounts.add(new Account(1, 1, "111111"));
		accounts.add(new Account(2, 2, "222222"));
		accounts.add(new Account(3, 3, "333333"));
		accounts.add(new Account(4, 4, "444444"));
		accounts.add(new Account(5, 1, "555555"));
		accounts.add(new Account(6, 2, "666666"));
		accounts.add(new Account(7, 2, "777777"));
	}
	
	//@ApiOperation(value = "View user", response = Iterable.class)
	@RequestMapping(value = "/user", method=RequestMethod.GET, produces = "application/json")
	  public Principal user(Principal principal) {
	    return principal;
	  }
	
	//@ApiOperation(value = "View account by number", response = Iterable.class)
	@RequestMapping(value = "/accounts/{number}", method=RequestMethod.GET, produces = "application/json")
	public Account findByNumber(@PathVariable("number") String number) {
		logger.info(String.format("Account.findByNumber(%s)", number));
		return accounts.stream().filter(it -> it.getNumber().equals(number)).findFirst().get();
	}
	
	//@ApiOperation(value = "View account by customer number", response = Iterable.class)
	@RequestMapping(value = "/accounts/customer/{customer}", method=RequestMethod.GET, produces = "application/json")
	public List<Account> findByCustomer(@PathVariable("customer") Integer customerId) {
		logger.info(String.format("Account.findByCustomer(%s)", customerId));
		return accounts.stream().filter(it -> it.getCustomerId().intValue()==customerId.intValue()).collect(Collectors.toList());
	}
	
	@ApiOperation(value = "View all acounts", notes = "")
	@RequestMapping(value = "/accounts", method=RequestMethod.GET, produces = "application/json")
	public List<Account> findAll(@ModelAttribute String access_token) {
		logger.info("Account.findAll()");
		return accounts;
	}
	
}
