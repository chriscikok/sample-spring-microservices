package chris.microservices.account.model;

import io.swagger.annotations.ApiModelProperty;

public class Account {
	
	@ApiModelProperty(notes = "Account ID")
	private Integer id;
	@ApiModelProperty(notes = "Customer ID")
	private Integer customerId;
	@ApiModelProperty(notes = "Account Number")
	private String number;

	public Account() {

	}

	public Account(Integer id, Integer customerId, String number) {
		this.id = id;
		this.customerId = customerId;
		this.number = number;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
