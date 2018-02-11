package contracts

org.springframework.cloud.contract.spec.Contract.make {
	request {
		method 'GET'
		url '/customers/customer/1'
		
    }
    response {
    	status 200
    	body([
    		customerCheckStatus: 'FOUND',
    		reason : 'Record found'
    	])
    	headers {
    		contentType('application/json')
    	}
    }
   
}