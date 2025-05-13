# Price-Comparator- Java Backend

## Summary

A Java-based Spring Boot application that offers users several shopping facilities, such as comparing product prices across different stores, tracking price trends over time and receiving alerts when products reach target prices. Moreover, the application analyzes price data from supermarkets to provide shopping recommendations.

## Key Features

- Shopping basket optimization to minimize total cost
- Price trend analysis by product, store, category, or brand
- Discount analysis to find top saving opportunities
- Price alerts to notify users when products reach target prices
- Automatic monitoring of new price data

## Project Structure

```
├── controller/     # REST API controllers
├── dto/            # Data Transfer Objects
├── model/          # Business domain models
├── repository/     # Data access layer
├── service/        # Business logic
├── utils/          # Helper utilities
└── Main.java       # Application entry point

└── resources/
    └── data/       # CSV data files

└── test/ # Test handling section

├── target/ # JavaDoc documentation page location

```

## Build and Compilation

### Prerequisites

- Java JDK 17 or higher
- Maven 3.6 or higher

### Build Instructions

1. Clone the repository:

   ```
   git clone https://github.com/engelInes/Price-Comparator-Backend.git
   cd price-comparator
   ```

2. Build with Maven:
   ```
   mvn clean install
   ```

### Running the Application

Run as a Spring Boot application:

```
mvn spring-boot:run
```

## App Usage

The application provides a command-line interface with the following options:

### Main Menu

```
Select an option:
1. Optimize shopping basket savings
2. Show price trend for a product
3. Show price trend for a product in a store
4. Show price trend by category
5. Show price trend by brand
6. Show top discounts of last 24h
7. Show max discounts
8. Optimize shopping basket with unit price
9. Create Price Alert
10. View Price Alerts
11. View Triggered Price Alerts
12. Delete Price Alert
13. Exit
```

### Automatic Features

- **CSV File Monitoring**: The application automatically monitors the data directory for new or updated CSV files.
- **Price Alert Notifications**: When new price data is detected, the system checks if any price alerts should be triggered.

## Data Files

The application reads price and discount data from CSV files in the `src/main/resources/data` directory. The naming convention for these files is:

- Regular price files: `store_name_yyyy-MM-dd.csv`
- Discount files: `store_name_discounts_yyyy-MM-dd.csv`
