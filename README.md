# Product Catalog - 3-Tier Architecture Application

A complete 3-tier web application built with Spring Boot.

## Architecture

- Presentation Tier: Thymeleaf + HTML/CSS
- Application Tier: Spring Boot 3
- Data Tier: H2 Database (Development)

## Features

- Full CRUD operations for products
- REST API endpoints
- Search and filtering
- Pagination support
- Data validation
- Docker containerization

## Technology Stack

- Java 17
- Spring Boot 3.1.5
- Maven
- H2 Database
- Thymeleaf
- Bootstrap 5

## Quick Start

### Local Development

mvn spring-boot:run

Access: http://localhost:8080/products

### Docker Deployment

docker build -t product-catalog .
docker run -p 8080:8080 product-catalog

## Project Structure

3-tier-app/
  src/
    main/
      java/
        com/example/productcatalog/
          ProductCatalogApplication.java
          controller/
          entity/
          repository/
          service/
      resources/
        application.yml
        templates/
        static/

## API Endpoints

- GET /products/api - Get all products
- POST /products/api - Create product
- PUT /products/api/{id} - Update product
- DELETE /products/api/{id} - Delete product

## Database

H2 Console: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (empty)

## Docker Commands

docker-compose up -d
docker-compose down
docker-compose logs

## License

MIT License
# 3-tier-app
# 3-tier-app
# Trigger workflow on Sat Sep  5 14:46:45 UTC 2026
# Trigger workflow
# Trigger workflow
# Updated README
# Trigger GitHub Actions

## 🚀 CI/CD Status

![CI/CD Pipeline](https://github.com/ranjeet12081988/3-tier-app/actions/workflows/ci.yml/badge.svg)

[![Build Status](https://github.com/ranjeet12081988/3-tier-app/actions/workflows/ci.yml/badge.svg)](https://github.com/ranjeet12081988/3-tier-app/actions/workflows/ci.yml)
