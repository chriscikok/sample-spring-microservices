package contracts

org.springframework.cloud.contract.spec.Contract.make {
	request {
		method 'GET'
		url '/customers/customer/999'
		
    }
    response {
    	status 200
    	body([
    		customerCheckStatus: 'NOTFOUND',
    		reason : 'Customer Not found'
    	])
    	headers {
    		contentType('application/json')
    	}
    }
   
}