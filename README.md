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
- Code will be pushed to **private Github repository** and dedicated finmid members will be added as collaborators

## Solution

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

### How to start
*make start*


