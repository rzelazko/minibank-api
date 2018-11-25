# Minibank API

A simple & lightweight RESTFul API for money transfers between internal accounts (transfer via DB only, no distributed transactions).

Minibank API is artificial project created to learn [Spark Framework](https://github.com/perwendel/spark/).
Technological stack:
- Java 8
- [Spark Framework 2.8.0](https://github.com/perwendel/spark/)
- [Lombok](https://projectlombok.org/)
- [Junit5](https://junit.org/junit5/)
- [Gson 2.8.5](https://github.com/google/gson/)
- [Mockito 2.x](https://site.mockito.org/)
- [REST-assured 3.2.0](http://rest-assured.io/)

## Installation

To install Minibank API locally clone git repository and
```batch
mvn install
mvn exec:java
REM -- OR --
java -jar target\minibank-api-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Tests

Minibank API has two types of tests 
- unit tests - fast, isolated with Mockito, designed with JUnit5 Parametrized Test feature - run with `mvn test`. Supported by `maven-surefire-plugin`
- integration tests - slower, use real application to test complex scenario with help of REST-assured - run with `mvn integration-test`. Supported by `maven-failsafe-plugin`

### IntelliJ Users

IntelliJ has support for scratch file. It allows easily call REST endpoints from IDE (no need to have Postman or cURL). Basic scenario written in `.http` file format can be found under `src\main\client\*.http`.

## API Documentation

Minibank API consists of two REST resources. Main focus of this exercise is `transfers`. Helper, internal resource acts as support for creating money transfer - `account`.

Available REST endpoints summary:
- `POST http://{{host}}/api/accounts` - [Create account](#open-account)
- `GET http://{{host}}/api/accounts/{{iban}}` - [Retrieve account details](#retrieve-account-details)
- `POST http://{{host}}/api/transfers` - [Initialize transfer](#step-1---initialize-transfer)
- `PUT http://{{host}}/api/transfers/{{uuid}}` - [Authorize transfer](#step-2---authorize-transfer)
- `GET http://{{host}}/api/transfers/{{uuid}}` - [Retrieve transfer details](#retrieve-transfer-details)
- `DELETE http://{{host}}/api/transfers/{{uuid}}` - [Cancel transfer](#cancel-transfer-request)

### Accounts management

Accounts API allows to open new account or check its balance

#### Open account

##### Create account request
Fields:
- iban - IBAN number - pattern `[a-zA-Z]{2}[a-zA-Z0-9]{14,30}`
- currency - three letter currency code
- balance - initial account balance. *Note: amount is integer divided by 100 to avoid precision issues*

Example:
```http request
POST http://localhost:8080/api/accounts
Content-Type: application/json

{
  "iban": "PL12345678901234567890123456",
  "currency": "PLN",
  "balance": 100000
}
```

##### Create account response

Valid response will have HTTP status: `201 Created`, header `Location` and created account details.

#### Retrieve account details

##### Get account request

URL to get account request use IBAN to find valid account.

Example:
```http request
GET http://localhost:8080/api/accounts/PL12345678901234567890123456
```

##### Get account response

Valid response will have HTTP status: `200 OK` and account details in body.

### Transfers management

Money transfer is two step process. First step is to place transfer request. Second is to authorize it with valid SMS code.

#### Step 1 - Initialize transfer

##### Initialize transfer request

Fields:
- source - IBAN number of source account. Need exists and have enough money.
- destination - IBAN number of destination account. Need exists
- amount - integer representing amount to be transfer. *Note: amount is integer divided by 100 to avoid precision issues*
- currency - source, destination & transfer currency needs to match as Minibank API does not support currency conversion.

Example: 
```http request
POST http://localhost:8080/api/transfers
Content-Type: application/json

{
  "source": "PL12345678901234567890123456",
  "destination": "PL98765432109876543210987654",
  "amount": 50000,
  "currency": "PLN"
}
```

##### Initialize transfer response

Valid response will have HTTP status: `201 Created`, header `Location` and created transfer details.
Important response field is `uuid`. Authorization, cancellation or details request will require `uuid` to be provided. 

#### Step 2 - Authorize transfer

##### Authorize transfer request

URL to authorize transfer use `uuid` to find valid transfer.

Fields:
- authCode - imaginary authorization code (in real world could be SMS code), for training purposes it is hardcoded. **Valid authCode is 123456**

Example:
```http request
PUT http://localhost:8080/api/transfers/5fb01e44-63b1-42e9-b7e6-ba6988e5652f
Content-Type: application/json

{
  "authCode": "123456"
}
```  

##### Authorize transfer response

Valid response will have HTTP status: `202 Accepted` without body. 
In case transfer has been already confirmed (and `authCode` is valid) HTTP status will be `204 No Content`

#### Retrieve transfer details

##### Get transfer request

URL to get transfer request use `uuid` to find valid transfer.

Example:
```http request
GET http://localhost:8080/api/transfer/5fb01e44-63b1-42e9-b7e6-ba6988e5652f
```

##### Get transfer response

Valid response will have HTTP status: `200 OK` and transfer details in body.

#### Cancel transfer request

##### Delete transfer request

URL to delete transfer request use `uuid` to find valid transfer.

Example:
```http request
DELETE http://localhost:8080/api/transfer/5fb01e44-63b1-42e9-b7e6-ba6988e5652f
```

##### Delete transfer response

Valid response will have HTTP status: `200 OK` and canceled transfer details in body.

### Application error codes description

Error codes returned in both resources in case of exceptional situations.

| Error code  | Error description                                                            |
|-------------|------------------------------------------------------------------------------|
| E000        | Generic error, return in case of serious issue with Minibank API application |
| E0N1 - E0N3 | Requested or dependant resource has not been found                           |
| E0P1 - E0P5 | Request is invalid (ex. JSON invalid, IBAN invalid, etc)                     |
| E0A1        | Can't create account with IBAN which already exists                          |
| E0T1        | Transfer issue - insufficient founds                                         |
| E0T2 - E0T6 | Other transfer issue, description message should tell more                   |  


