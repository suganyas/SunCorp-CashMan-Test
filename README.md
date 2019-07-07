# CashSupplyService

# Required :

- Maven
- Java 8

# Tools/Libraries Used:

- FrameWork : Spring-Boot
- REST API doc : Swagger
- Unit Tests: Junit, Mockito
- Logging : Simple Logging Facade for Java (SLF4J)

# Assumptions and considerations :

* Written a REST Webservice with Spring-boot to match skills required as in Job description.
* The initial values for the denominations supported and available count are configured in the file denomination.config.
They get loaded during the initialization of the RestController. Refer intialiseDenominations function with @PostConstruct annotation in controller
* Have rounded the amount passed to neared 0.5 for dispatching the amount .
* Since the available cash can be stored in memory have not used any Database and used synchronised map to be thread safe and handle concurrency

#Limitations

* Have provided an empty API to implement notifications by push message/ email as needed when cash runs less than the minimum threshold.
Have not implemented the notification part.

# Endpoint for Testing:

Have added /addValuesToDenominations to add denominations and /getValuesToDenominations to view the available denominations 

To use /addValuesToDenominations add proper Json with AustralianCurrency enum values

##sample:

``` 
{
"HUNDREDDOLLAR" : 100,
"FIVEDOLLAR" :200
}
```

# Start Application:

Go to the root directory . Run the command
```
mvn spring-boot:run
```
To Debug remotely
```
mvnDebug spring-boot:run
```
You can see the end points in Swagger with Docs: http://localhost:8080/swagger-ui.html

# Run UnitTests:

Go to the root directory . Run the command
```
mvn verify
```
The results can be found in the Target folder. If needed cna be run with code coverage metrics from IDEs.

# Requirements Completion Status:

##Mandatory Feature Set

*	The device will have a supply of cash (physical bank notes) available. 
*	It must know how many of each type of bank note it has. It should be able to report back how much of each note it has. 
*	It should be possible to tell it that it has so many of each type of note during initialisation. After initialisation, it is only possible to add or remove notes. 
*	It must support $20 and $50 Australian denominations. 
*	It should be able to dispense legal combinations of notes. For example, a request for $100 can be satisfied by either five $20 notes or 2 $50 notes. It is not required to present a list of options. 
*	If a request can not be satisfied due to failure to find a suitable combination of notes, it should report an error condition in some fashion. For example, in an ATM with only $20 and $50 notes, it is not possible to dispense $30. 
*	Dispensing money should reduce the amount of available cash in the machine. 
*	Failure to dispense money due to an error should not reduce the amount of available cash in the machine. 

##Optional Feature Set

*	Support all other legal Australian denominations and coinage. 
*	The controller should dispense combinations of cash that leave options open. For example, if it could serve up either 5 $20 notes or 2 $50 notes to satisfy a request for $100, but it only has 5 $20 notes left, it should serve the 2 $50 notes. 
*	The controller needs to be able to inform other interested objects of activity. Threshold notification in particular is desirable, so that the ATM can be re-supplied before it runs out of cash. 
*	Persistence of the controller is optional at this time. It can be kept in memory. However, it should go through a distinct initialisation period where it is told the current available amounts prior to being used.
