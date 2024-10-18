# Currency Gold Exchange App

**Currency Gold Exchange App** is a robust application designed to provide users with the latest exchange rates for currencies and historical gold prices. The application enables users to calculate currency exchange amounts, track currency rates, and compare gold prices over time. It also allows users to load and save data, offering both online API integrations and offline file-based processing.

## Key Features

- **Currency Exchange Rates**: Fetches real-time currency exchange rates from NBP Rest API.
- **Gold Price Tracking**: Provides historical and current gold prices, allowing users to compare trends over time.
- **Currency Conversion**: Allows users to calculate conversions between different currencies based on real-time data.
- **File Processing**: Supports reading and writing exchange rate data from CSV files, enabling offline analysis.
- **Gold Price Comparison**: Compares today’s gold price to historical prices to assess potential gain or loss.

## Tech Stack

- **Java**: Core programming language used to implement the business logic and services.
- **Gradle**: Used for build automation and dependency management.
- **HttpClient**: Manages external API requests for fetching currency exchange rates and gold prices.
- **Jackson**: JSON processing library used to map API responses to Java objects.
- **JUnit**: Used for unit testing various application components.
- **Mockito**: Framework used for mocking objects in unit tests to isolate test logic.
- **Commons-IO**: Provides file handling capabilities for reading and writing CSV files.

## Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/Cebix90/CurrencyGoldExchangeApp.git
    ```

2. Navigate to the project directory:

    ```bash
    cd CurrencyGoldExchangeApp
    ```

3. Build the project with Gradle:

    ```bash
    ./gradlew build
    ```

4. Run the application:

    ```bash
    java -jar build/libs/CurrencyGoldExchangeApp-1.0.jar
    ```

5. The second option is to open the project in an IDE such as IntelliJ IDEA:

    - In IntelliJ IDEA, select **File** > **Open...** and choose the project directory.
    - IntelliJ will automatically detect the `build.gradle` file and configure the project for you.
   
## Usage

### Currency Exchange Rates:

The app fetches real-time exchange rates via external APIs. You can specify the currency code and date to get the current exchange rate.

### Gold Price Tracking:

The app allows you to track gold prices for specific dates or a range of dates. It retrieves data directly from the API and presents historical trends.

### File Handling:

You can load currency exchange data from CSV files and process conversions offline. The results can also be saved back to CSV for future use.

## Example Code Snippet

To fetch real-time exchange rates, the API is accessed as follows:

## API Integration

The app integrates with external APIs to retrieve the following data:

- **Currency Exchange Rates**: Data fetched from NBP Rest API.
- **Gold Prices**: Historical and current gold prices retrieved from the NBP Rest API.

## Unit Tests

The application is thoroughly tested using JUnit and Mockito for unit testing and mocking. You can run the tests by executing the following command:

```bash
./gradlew test
```

Alternatively, if your IDE supports it (e.g., IntelliJ IDEA), you can run the tests directly from the IDE.