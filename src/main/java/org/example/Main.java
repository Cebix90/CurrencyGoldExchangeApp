package org.example;

import org.example.models.CurrencyExchange;
import org.example.services.ExchangeRateService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter currency code: ");
            String currencyCode = scanner.next();

            System.out.print("Enter date (optional, press ENTER for today's date): ");
            scanner.nextLine();
            String dateString = scanner.nextLine();
            LocalDate date = dateString.isEmpty() ? LocalDate.now() : LocalDate.parse(dateString);

//            CurrencyExchange currencyExchange = new CurrencyExchange(date, currencyCode, "", 0.0);

            ExchangeRateService exchangeRateService = new ExchangeRateService();
            CurrencyExchange exchangeRateResult = exchangeRateService.getExchangeRate(currencyCode, date.toString());

            System.out.println("Exchange rate for " + currencyCode + " on " + date + ": " + exchangeRateResult);

        } catch (DateTimeParseException e) {
            System.out.println("Error during date parsing: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Another error: " + e.getMessage());
        }
    }
}
