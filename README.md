# Finmid test assignment

## Task description

[code-challenge-be](https://github.com/finmid/code-challenge-be)

Consider using following simplified data model:
```
Account:
- accountId - unique account identifier
- balance - account balance

Transaction:
- txId - unique transacion identifier
- amount - transferred amount
- from - source account id
- to - destination account id
```

Implement following banking APIs:
- create new account with predefined balance
- fetch account balance by `accountId`
- create transaction between two accounts

### Requirements

- Latest **Kotlin** or **Java**
- **Any framework**
- Stateless service with external data storage
- Ignore authentication and authorization

## Solution

It's implemented in a strict way - when we directly update accounts' balances. 
The best approach here is ES and CQRS.

### Used technologies

- Kotlin
- Spring Boot
- Mongo Db

### API

#### Response codes

200 - OK
400 - Bad request
404 - Requested entity not found
500 - Server error

#### Account API

##### Create new account with predefined balance

`POST http://localhost:8080/account`  
where body: `{balance:<balance_value>}`

example request:
```http request
POST http://localhost:8080/account
Content-Type: application/json

{
"balance": 1000
}

```

##### Request account info
Instead of providing account balance we provide the whole account model

`GET http://localhost:8080/account/<accountId>`

example request:
```http request
GET http://localhost:8080/account/61f639d6a471ea545a4a825d
```

example response:
```http request
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sun, 30 Jan 2022 07:11:53 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "accountId": "61f639d6a471ea545a4a825d",
  "balance": 1000
}

Response code: 200; Time: 51ms; Content length: 55 bytes

```

#### Transaction API

##### Create transaction between 2 accounts

`POST http://localhost:8080/transaction`  
where body: `{from:<accountId>, to:<accountId>, amount:<amount>}`

request example:
```http request
POST http://localhost:8080/transaction
Content-Type: application/json

{
  "from": "61f639d6a471ea545a4a825d",
  "to": "61f63aeda471ea545a4a825e",
  "amount": 500
}
```

response example:
```http request
POST http://localhost:8080/transaction

HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Sun, 30 Jan 2022 07:17:12 GMT
Keep-Alive: timeout=60
Connection: keep-alive

{
  "ixId": "61f63b78a471ea545a4a825f",
  "amount": 500,
  "from": "61f639d6a471ea545a4a825d",
  "to": "61f63aeda471ea545a4a825e"
}

Response code: 200; Time: 301ms; Content length: 114 bytes
```

### How to start
*make start*


