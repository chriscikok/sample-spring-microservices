package chris.microservices.account.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chris.microservices.account.AccountApplicationStatus;
import chris.microservices.account.intercomm.CustomerClient;
import chris.microservices.account.model.Account;
import chris.microservices.account.model.CustomerCheckStatus;
import chris.microservices.account.model.CustomerServiceResponse;

@Service
public class AccountService {

	private List<Account> accounts;
	
	@Autowired
	private CustomerClient customerClient;
	
	public AccountService (){
		
		List<Account> accounts = new ArrayList<>();
		accounts.add(new Account(1, 1, "Account 1"));
		accounts.add(new Account(2, 2, "Account 2"));
		accounts.add(new Account(3, 3, "Account 3"));
		accounts.add(new Account(4, 4, "Account 4"));
		accounts.add(new Account(5, 1, "Account 5"));
		accounts.add(new Account(6, 2, "Account 6"));
		accounts.add(new Account(7, 2, "Account 7"));
		
		this.setAccounts(accounts);
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	
	public AccountApplicationStatus addAccount(Account account){
		
		CustomerServiceResponse customerServiceResponse = customerClient.verify(account.getCustomerId());
		if (customerServiceResponse.getCustomerCheckStatus().equals(CustomerCheckStatus.FOUND)) {
			this.accounts.add(account);
			return AccountApplicationStatus.ADD;
		} else {
			return AccountApplicationStatus.CUST_NOT_FOUND;
		}
	}
	
	
}
