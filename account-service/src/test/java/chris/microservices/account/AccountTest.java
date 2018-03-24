package chris.microservices.account;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.context.annotation.Configuration;

import chris.microservices.account.AccountApplicationStatus;
import chris.microservices.account.model.Account;
import chris.microservices.account.services.AccountService;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

@RunWith(SpringRunner.class)
@AutoConfigureStubRunner(ids = {"chris:customer-service:+:stubs:6565"}, workOffline = true)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
public class AccountTest {
	
	@Autowired
	private AccountService accountService;
	
	@Test
	public void shouldBeRejectedDueToCustomerNotFound() {
		// given:
		Account account = new Account(999,999,"Account 999");
		//LinkAccountApplication application = new LinkAccountApplication(new Account(8, 5, "Acccount 8"));
		// when:
		AccountApplicationStatus aas = accountService.addAccount(account);
		//LinkAccountApplicationResult linkApplication = service.link(application);
		// then:
		assertThat(aas).isEqualTo(AccountApplicationStatus.CUST_NOT_FOUND);
		//assertThat(linkApplication.getRejectionReason()).isEqualTo("Customer not found");
	}
	
	@Test
	public void shouldBeNormalDueToCustomerFound() {
		// given:
		Account account = new Account(1,1,"Account 1");
		//LinkAccountApplication application = new LinkAccountApplication(new Account(8, 5, "Acccount 8"));
		// when:
		AccountApplicationStatus aas = accountService.addAccount(account);
		//LinkAccountApplicationResult linkApplication = service.link(application);
		// then:
		assertThat(aas).isEqualTo(AccountApplicationStatus.ADD);
		//assertThat(linkApplication.getRejectionReason()).isEqualTo("Customer not found");
	}

}
