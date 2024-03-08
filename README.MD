# Loan Request Service Documentation

## Overview

The Loan Request Service is a microservice designed to manage loan requests for customers. It facilitates the creation of loan requests and calculates the total loan amount applied for by each customer. The service is built with Spring Boot and utilizes PostgreSQL for data persistence and Redis for caching total loan amounts to optimize read operations.

## Key Features

- **Create Loan Request**: Enables the addition of a new loan request for a customer with validation on the loan amount.
- **Get Total Loan Amount**: Allows retrieval of the total amount of money a customer has applied for, leveraging Redis caching for fast read access.

## Technology Stack

- **Spring Boot**: Framework for creating stand-alone, production-grade Spring-based applications.
- **PostgreSQL**: Open-source relational database used for persisting loan request data.
- **Redis**: In-memory data store used as a cache to store the total loan amounts for quick retrieval.

## Setup and Installation

To get the service up and running, follow these steps:

1. Clone the repository:
   ```
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```
   cd loan-request-service
   ```

3. Install the necessary dependencies and package the application:
   ```
   mvn clean package
   ```

4. Start the PostgreSQL and Redis services using Docker:
   ```
   docker-compose up -d postgres redis
   ```

5. Run the application:
   ```
   java -jar target/loan-request-service-0.0.1-SNAPSHOT.jar
   ```

This setup process ensures that only the PostgreSQL and Redis services are containerized, and the application runs directly on the host machine.

## API Endpoints

### Create Loan Request

- **URL**: `/loan-requests`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **Body**:
  ```json
  {
    "amount": 1000.5,
    "customerFullName": "John Doe",
    "customerId": 1
  }
  ```
- **CURL Example**:
  ```bash
  curl -X POST http://localhost:8080/loan-requests \
    -H 'Content-Type: application/json' \
    -d '{
          "amount": 1000.5,
          "customerFullName": "John Doe",
          "customerId": 1
        }'
  ```

### Get Total Loan Amount

- **URL**: `/loan-requests/total-amount/{customerId}`
- **Method**: `GET`
- **CURL Example**:
  ```bash
  curl -X GET http://localhost:8080/loan-requests/total-amount/1
  ```

## Design Decisions

- **Indexing on Customer ID**: An index has been added to the database to speed up search operations by customer ID, enhancing the performance of the service.
- **Usage of Redis**: To enhance the performance of reading operations, especially for frequently accessed data like total loan amounts, Redis is used as a caching layer.
- **BigDecimal for Monetary Values**: `BigDecimal` is used to represent monetary values to avoid the precision issues associated with `double` or `float` types.
- **Containerization**: Docker is employed to containerize PostgreSQL and Redis services, ensuring consistent environments and facilitating easy deployment.

## Future Improvements

- **Authentication and Authorization**: Implement security measures to ensure that API endpoints are accessed by authenticated and authorized users only.
- **API Versioning**: Introduce versioning to the API endpoints to support backward compatibility and smoother transitions for clients as the API evolves.
- **Comprehensive Logging and Monitoring**: Enhance the logging and monitoring capabilities to provide better insights into the application's performance and usage patterns.