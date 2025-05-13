## Table of Contents

- [Overview](#overview)
- [Service Components](#service-components)
  - [Product Service](#product-service)
  - [Price Trend Service](#price-trend-service)
  - [Discount Service](#discount-service)
  - [Discount Analysis Service](#discount-analysis-service)
  - [Basket Optimization Service](#basket-optimization-service)
  - [Price Alert Service](#price-alert-service)
- [Data Models](#data-models)

## Overview

This price comparison system allows users to:

- Load and manage product price data from CSV files
- Track price trends for products across different stores
- Monitor and analyze discounts
- Optimize shopping lists to find the best deals
- Set up price alerts for desired products

## Service Components

### Product Service

Implements `IProductService` interface for loading price entries from files.

**Methods:**

- `loadPriceEntries(String filePath)`: Loads price data from a CSV file and converts entries to DTOs.
  - **Logic**: Uses an `ItemRepository` to read price entries from the file and maps each entry to a `PriceEntryDTO`.

### Price Trend Service

Provides functionality for analyzing price trends over time.

**Methods:**

- `getPriceTrendsForProduct(String productName)`: Retrieves price history for a specific product.
  - **Logic**: Fetches all price entries for the product from the repository, sorts them by date, and converts to DTOs.
- `getPriceTrendsForProductAndStore(String productName, String storeName)`: Retrieves price history for a specific product at a specific store.
  - **Logic**: Filters price entries by both product name and store, sorts by date, and returns as DTOs.
- `getPriceTrendsByCategory(String category)`: Retrieves price trends for products in a specific category.
  - **Logic**: Fetches all entries for products in the specified category, sorts by date, and returns as DTOs.
- `getPriceTrendsByBrand(String brand)`: Retrieves price trends for products of a specific brand.
  - **Logic**: Fetches all entries for products of the specified brand, sorts by date, and returns as DTOs.

### Discount Service

Implements `IDiscountService` interface for handling discount data operations.

**Methods:**

- `loadDiscounts(String filePath)`: Loads discount data from files.
  - **Logic**: Uses a discount repository to read entries from the file and maps them to `DiscountDTO` objects.
- `getMaxDiscountPerProduct(int limit)`: Retrieves the highest discount for each product.
  - **Logic**: Filters for active discounts (based on current date), groups by product ID, keeps only the highest percentage discount for each product, sorts by discount percentage in descending order, and limits to the specified number of results.

### Discount Analysis Service

Provides advanced analysis of discount data.

**Methods:**

- `getHighestDiscounts(int limit)`: Retrieves the top active discounts by percentage.
  - **Logic**: Gets all discounts from the repository, filters for currently valid ones (based on start and end dates), sorts by discount percentage in descending order, and limits to the specified number of results.
- `getNewlyAddedDiscounts()`: Retrieves discounts that were newly added today.
  - **Logic**: Gets all discounts, filters for those with a starting date matching the current date, sorts by starting date in descending order, and converts to DTOs.

### Basket Optimization Service

Finds the best savings across different stores.

**Methods:**

- `optimizeBasket(List<BasketItem> basket)`: Finds the lowest price for each item across all stores.
  - **Logic**:
    1. Calculates the original cost of the basket
    2. Retrieves active discounts (based on current date)
    3. For each item in the basket:
       - Finds all price entries for the product
       - Identifies discounts
       - Calculates effective price after discounts for each store
       - Selects the store with the lowest effective price
    4. Groups items by store to create optimized shopping lists
    5. Returns an `OptimizedBasketDTO` containing shopping lists for each store with items at the best prices
- `optimizeBasketWithUnitPrice(List<BasketItem> basket)`: Similar to `optimizeBasket` but considers unit prices.
  - **Logic**: Same as above, but also calculates unit prices (price divided by package quantity) to make comparisons between different package sizes.
- `calculateOriginalCost(List<BasketItem> basket)`: Helper method to calculate the cost of the basket without optimization.
  - **Logic**: Determines the cost of buying all items from a single store that has all the requested products, selecting the store with the lowest total cost.

### Price Alert Service

Allows users to set up alerts for when product prices fall below specified targets.

**Methods:**

- `createAlert(String userId, String productId, double targetPrice)`: Creates a new price alert.
  - **Logic**: Creates a new `PriceAlert` entity with the specified parameters and saves it to the repository.
- `getUserAlerts(String userId)`: Retrieves all alerts for a specific user.
  - **Logic**: Queries the repository for alerts associated with the user ID and converts them to DTOs.
- `deleteAlert(Long alertId)`: Deletes an alert by its ID.
  - **Logic**: Removes the specified alert from the repository.
- `updateAlert(Long alertId, double newTargetPrice)`: Updates an alert's target price.
  - **Logic**: Finds the alert by ID, updates the target price, reactivates it, clears any previous trigger, and saves the changes.
- `checkPriceAlerts()`: Scheduled method that checks all active alerts against current prices.
  - **Logic**:
    1. Retrieves all active alerts and groups them by product ID
    2. For each product, finds the latest price entry
    3. Compares the current price against the target price for each alert
    4. Marks alerts as triggered when the current price is less than or equal to the target price
- `getTriggeredAlerts(String userId)`: Retrieves all triggered alerts for a user.
  - **Logic**: Finds all alerts for the user that are no longer active and have a trigger timestamp.

## Data Models

The system uses several data models:

- **PriceEntry**: Represents a price record for a product at a specific store and time
- **Discount**: Represents a discount offer for a product
- **PriceAlert**: Represents a user's price alert for a product
- **BasketItem**: Represents an item in a user's shopping basket

Each model has a corresponding DTO (Data Transfer Object) used for transferring data between layers.
