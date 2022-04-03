# Deposits Management Web Application

## How to run:
1. Uncomment in DepositsManagementApplication.java to create user with first running (Only first)
2. In file resources/application.properties:
    - config database info
    - change `spring.jpa.hibernate.ddl-auto=none` to `spring.jpa.hibernate.ddl-auto=update` in order to create database structure
3. Run command `mvn spring-boot:run` in terminal

