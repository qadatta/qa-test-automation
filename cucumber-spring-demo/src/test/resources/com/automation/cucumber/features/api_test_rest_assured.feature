@api_check
Feature: this feature does API testing using rest asssured 

	Scenario: execute custom-download POST api for given specification and compare new downloads with existing one

			Given Custom-download POST api is up and running
			When Custom-download POST api executed for specification given in file "download_specification.json"
			Then the status code is 200
			
			Given Custom-download GET api is up and running
			When Custom-download GET api is executed for request-id of custom-download post request
			Then the status code is 200
			And response includes the currentState as "completed"
			
#			Given s3URI is available from custom-download api
			When download custom-download zip file using curl command
  		Then compare source zip file with destination zip file
#			