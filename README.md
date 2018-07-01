# money-transfer-service

The main idea was to create a java service that provides a restful API using minimalistic approach. So no frameworks were used except for Jersey as JAX-RS implementation. The application and controller tests are run on embedded Jetty server.

Run app on port 8090
```
./gradlew build   
java -jar build/libs/money-transfers-service-1.0-SNAPSHOT.jar 8090
```

### Endpoints

| Method | Url | Decription |
| ------ | --- | ---------- |
| GET    |/api/accounts  | list all accounts |
| GET    |/api/accounts/{id}  | get account by id |
| POST    |/api/accounts  | create account |
| GET    |/api/transfers  | list all transfers(deposits and withdrawals are part of transfer) |
| POST    |/api/transfers  | create transfer |
| POST    |/api/deposits  | create deposit |
| POST    |/api/withdrawals  | create withdrawal |


### Examples

```javascript
POST api/accounts
{  
  "balance": 500
}

POST api/transfers
{    
  "fromAccountId": 1,  
  "toAccountId": 2,
  "amount": 100.1
}

POST api/deposits
POST api/withdrawals
{      
  "accountId": 3,    
  "amount": 200.12
}
```
