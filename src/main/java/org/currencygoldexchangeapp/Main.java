package org.currencygoldexchangeapp;

import org.currencygoldexchangeapp.datamodels.CurrencyExchange;
import org.currencygoldexchangeapp.handlers.ExchangeRateAPIHandler;
import org.currencygoldexchangeapp.services.CurrencyExchangeService;

import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter source currency code: ");
            String sourceCurrencyCode = scanner.next();

            System.out.print("Enter target currency code (press ENTER if not applicable): ");
            scanner.nextLine();
            String targetCurrencyCode = scanner.nextLine();

            System.out.print("Enter amount: ");
            double amount = scanner.nextDouble();

            // Konsumuj znak nowej linii, aby uniknąć problemu z pomiędzy nextDouble() a nextLine()
            scanner.nextLine();

            System.out.print("Enter date (optional, press ENTER for today's date): ");
            String dateString = scanner.nextLine();
            LocalDate date = dateString.isEmpty() ? LocalDate.now() : LocalDate.parse(dateString);

            ExchangeRateAPIHandler exchangeRateAPIHandler = new ExchangeRateAPIHandler(HttpClient.newHttpClient());
            CurrencyExchangeService currencyExchangeService = new CurrencyExchangeService(exchangeRateAPIHandler);

            try {
                CurrencyExchange result = currencyExchangeService.calculateExchangeAmount(sourceCurrencyCode, amount, targetCurrencyCode, date.toString());
                System.out.println("Result of currency exchange: " + result);
            } catch (Exception e) {
                System.out.println("Error during currency exchange: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Another error: " + e.getMessage());
        }
    }
}