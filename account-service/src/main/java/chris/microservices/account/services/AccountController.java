package chris.microservices.account.services;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import chris.microservices.account.AccountApplicationStatus;
import chris.microservices.account.model.Account;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController 

public class AccountController {

	//private List<Account> accounts;
	@Autowired
	private AccountService accountService;
	
	
	
	protected Logger logger = Logger.getLogger(AccountController.class.getName());
	
	private int runCnt=0;
	
	public AccountController() {
		//accounts = new ArrayList<>();
		//accounts.add(new Account(1, 1, "Acccount 1"));
		//accounts.add(new Account(2, 2, "Acccount 2"));
		//accounts.add(new Account(3, 3, "Acccount 3"));
		//accounts.add(new Account(4, 4, "Acccount 4"));
		//accounts.add(new Account(5, 1, "Acccount 5"));
		//accounts.add(new Account(6, 2, "Acccount 6"));
		//accounts.add(new Account(7, 2, "Acccount 7"));
	}
	
	@ApiOperation(value = "View account by number", 
			authorizations = {@Authorization (value = "SWAGUI")})
	@RequestMapping(value = "/accounts/{number}", method=RequestMethod.GET, produces = "application/json")
	public Account findByNumber(@PathVariable("number") String number) {
		logger.info(String.format("Account.findByNumber(%s)", number));
		return accountService.getAccounts().stream().filter(it -> it.getNumber().equals(number)).findFirst().get();
	}
	
	@ApiOperation(value = "View account by customer number", 
			authorizations = {@Authorization (value = "SWAGUI")})
	@RequestMapping(value = "/accounts/customer/{customer}", method=RequestMethod.GET, produces = "application/json")
	public List<Account> findByCustomer(@PathVariable("customer") Integer customerId) {
		logger.info(String.format("Account.findByCustomer(%s)", customerId));
		return accountService.getAccounts().stream().filter(it -> it.getCustomerId().intValue()==customerId.intValue()).collect(Collectors.toList());
	}
	
	@ApiOperation(value = "View all acounts", notes = "", 
			authorizations = {@Authorization (value = "SWAGUI")})
	@CrossOrigin(origins = "http://localhost:8080")
	@RequestMapping(value = "/accounts", method=RequestMethod.GET, produces = "application/json")
	public List<Account> findAll() {
		logger.info("Account.findAll()");
		return accountService.getAccounts();
	}
	
	@ApiOperation(value = "Add new account", notes = "", 
			authorizations = {@Authorization (value = "SWAGUI")})
	@RequestMapping(value = "/accounts/account", method=RequestMethod.POST, produces = "text/plain")
	public String addAccount(@RequestBody Account account) {
		if (accountService.addAccount(account)== AccountApplicationStatus.ADD) {
			return "Account add";
		} else {
			return "System error";
		}
		
	}
	
	@RequestMapping(value = "/slowdown", method=RequestMethod.GET)
	public List<Account> Slowdown() throws InterruptedException {
		logger.info("Account.Slowdown()");
		Thread.sleep(runCnt*100);
		runCnt++;
		return accountService.getAccounts();
	}
	
	
}
