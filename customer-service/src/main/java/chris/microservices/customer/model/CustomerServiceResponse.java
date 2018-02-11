package chris.microservices.customer.model;

public class CustomerServiceResponse {

	private CustomerCheckStatus customerCheckStatus;
	private String reason;
	public CustomerCheckStatus getCustomerCheckStatus() {
		return customerCheckStatus;
	}
	public void setCustomerCheckStatus(CustomerCheckStatus customerCheckStatus) {
		this.customerCheckStatus = customerCheckStatus;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
