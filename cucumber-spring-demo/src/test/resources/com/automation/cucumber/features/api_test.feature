@api_check
Feature: this feature does API testing



    # 1  
    #Scenario: Customer login to application and fetch Auth tocken
    #Given User open applicaton "https://developer.theexchange.fanniemae.com/"
    #When user login using username "qa.datta@gmail.com" and password "P@ssw0rd"
    #Then I should see user logged in to application
    #
    # 2
    #Scenario: Customer create custom-download request for given spec
    #Given User has access to custom-download api
    #When I execute custom-download  POST request using spec
    #Then I should see status code as 200 
    #
    # 3
    #Scenario: Customer check if custom-download request is completed
    #Given User has access to custom-download status api
    #When I execute custom-download  GET request 
    #Then I should see status code as 200
    #And Verify response is having completed status
    #Given User has access to custom-download api
    #When I execute custom-download  POST request using spec
    #Then I should see status code as 200
    
    
   
    
     
    Scenario: Customer login to application and fetch Auth tocken and execute cutom download api and check status
    Given User open applicaton "https://developer.theexchange.fanniemae.com/"
    When user login using username "qa.datta@gmail.com" and password "P@ssw0rd"
    Then I should see user logged in to application
     
    Given User has access to custom-download api
    When I execute custom-download  POST request using spec "download_specification.json"
    Then I should see status code as 200 
    Given User has access to custom-download status api
    When I execute custom-download  GET request 
    Then I should see status code as 200
    And Verify response is having completed status
         
         
         
         
         
         