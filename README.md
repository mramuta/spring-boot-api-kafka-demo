# Spring Boot Api Kafka Consumer Demo
This is a small project that contains a small REST API and a very basic Kafka consumer application.

###Requirements

- Java 8
- Docker

###Setup
1. Start the docker containers 

    `$ docker-compose up`
2. Start the spring boot api
    
    `$ ./gradlew api:bootRun` 
3. Start the Kafka consumer

    `$ ./gradlew consumer:run`

###API Operation
1. Get users by country

    GET /users?country=${country}
    
2. Create User
    
    POST /users
    
    Body: 
    ```
    {
    	"first_name": "someFirstName",
    	"last_name": "someLastName",
    	"email": "someEmail",
    	"password": "password"
    }
   ```


    

