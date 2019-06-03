## Java project: money transfer demo REST API without (any) frameworks

## Run:
```
gradlew runApp
```
## build:
```
gradlew clean build
```

### Note about InMemoryRepository.java
``````(src\main\java\com\izam\data\account\InMemoryRepository.java)``````

This is a simple implementation of "in-memory-Java-repo" uses Collections.
Of course, better to use some transactional DB with tables in memory or some another
implementation.
For example, HSQLDB or other good "Java-storage-in-memory" 3rd party solution.
I'll try to implement it in future updates.

### application.properties
There are 2 `application.properties` file - for `run` and for `test`:
````
src\main\resources\application.properties
````
````
src\test\resources\application.properties
````
`server.port` stored in `application.properties`, by default: `8080`

## Endpoints
### GET
#### Check server health, current application version
```bash
curl localhost:8080/api/version
```
#### Check account state/data
```bash
curl localhost:8080/api/account?login=test
```
Important! Before checking account have to create account first by the following request `api/account/deposit`:
##POST
#### Register account state and set initial deposit
```bash
curl -X POST localhost:8080/api/account/deposit -d '{"login": "test" , "amount": "50.00"}'
```
##POST
#### main feature: transfer money `from` one account `to` another  
```bash
curl -X POST localhost:8080/api/account/transfer -d '{"from": "bank" , "to": "me", amount": "50.00"}'
```
Important! All accounts which are takes part in transfer request have to be created by `api/account/deposit` request first

## So, right sequence of requests are:
1. Create account/set initial deposit
2. Transfer money
3. Check accounts that money was transferred
#### also see tests: ````src\test\java\com\izam\app\DemoHttpServerTests.java````

##### Special thanks to Marcin Piczkowski and his [tutorial](https://dev.to/piczmar_0/framework-less-rest-api-in-java-1jbl)
 