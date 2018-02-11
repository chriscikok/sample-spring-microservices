package chris.microservices.customer.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import chris.microservices.customer.model.Customer;
import chris.microservices.customer.model.CustomerType;

@Service
public class CustomerService {
	
	private List<Customer> customers;
	
	public CustomerService() {
		customers = new ArrayList<>();
		customers.add(new Customer(1, "1", "Customer 1", CustomerType.INDIVIDUAL));
		customers.add(new Customer(2, "2", "Customer 2", CustomerType.INDIVIDUAL));
		customers.add(new Customer(3, "2", "Customer 3", CustomerType.INDIVIDUAL));
		customers.add(new Customer(4, "3", "Customer 4", CustomerType.INDIVIDUAL));
		this.customers = customers;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}
}
