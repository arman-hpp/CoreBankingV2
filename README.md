This project is a banking software solution designed around the concept of `Interest-Free Loan Funds` to empower individuals and communities by providing loans without interest, in line with ethical financial principles. The platform offers a seamless, user-friendly experience that facilitates benevolent lending, ensuring financial assistance is available to those in need, while promoting fairness, social equity, and financial inclusion.


### Prerequisites
* Maven
* Git
* JDK 22.0.2

### Dependencies
* [Spring Boot Starter](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter)
* [Spring Boot Starter Data JPA](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)
* [Spring Boot Starter Test](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test)
* [Spring Boot Starter Web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web)
* [Spring Boot Starter Security](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security)
* [Spring Boot Maven Plugin](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-maven-plugin)
* [Lombok](https://mvnrepository.com/artifact/org.projectlombok/lombok)
* [Model Mapper](https://mvnrepository.com/artifact/org.modelmapper/modelmapper)
* [H2 Database Engine](https://mvnrepository.com/artifact/com.h2database/h2)
* [Apache POI Common](https://mvnrepository.com/artifact/org.apache.poi/poi)
* [Apache Commons CSV](https://mvnrepository.com/artifact/org.apache.commons/commons-csv)
* [IText Core](https://mvnrepository.com/artifact/com.itextpdf/itextpdf)
* [SpringDoc OpenAPI Starter WebMVC UI](https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui)

### Installation
* `git clone https://github.com/arman-hpp/CoreBanking`
* `cd CoreBanking`
* `$mvnw install`

### Testing (Compile + Testing)
* `$mvnw clean test`

### Start Spring boot Application
* `$mvnw spring-boot:run`

### Package (Compile + Testing + Create JAR file)
* `$mvnw clean package`

### Start Spring boot Application with JAR file
* `$mvnw clean package`
* `$java -jar target/<filename>.jar`

### Test Users
| Username  | Password  | UserType  |
|-----------|-----------|-----------|
| admin     | 12345     | Admin     |
| arman     | 12345     | User      |
| ali       | 12345     | Admin     |

### Database Users
| Username | Password  |
|----------|-----------|
| sa       | 12345     |