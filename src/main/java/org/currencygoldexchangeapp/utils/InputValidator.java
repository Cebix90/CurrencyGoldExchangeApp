package org.currencygoldexchangeapp.utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InputValidator {
    public static boolean isCurrencyAvailable(String currency, String date) {
        List<String> codeCurrencyAvailableFor2011AndLater = Arrays.asList("USD", "AUD", "CAD", "EUR", "HUF", "CHF", "GBP", "JPY", "CZK", "DKK", "NOK", "SEK", "XDR");
        List<String> codeCurrencyAvailableFor2010AndBefore = Arrays.asList("USD", "AUD", "CAD", "EUR", "HUF", "CHF", "GBP", "JPY", "CZK", "DKK", "EEK", "NOK", "SEK", "XDR");

        int year = Integer.parseInt(date.substring(0, 4));

        List<String> availableCurrencies;

        if (year >= 2011) {
            availableCurrencies = codeCurrencyAvailableFor2011AndLater;
        } else if (year >= 2003) {
            availableCurrencies = codeCurrencyAvailableFor2010AndBefore;
        } else {
            availableCurrencies = new ArrayList<>();
        }

        return availableCurrencies.stream()
                .map(String::toLowerCase)
                .toList()
                .contains(currency.toLowerCase());
    }

    public static LocalDate getDate(Scanner scanner) {
        LocalDate date = null;
        LocalDate today = LocalDate.now();

        while (date == null) {
            System.out.print("Please enter a date within the range from 2003-01-01 to " + today + " (optional, press ENTER for today's date): ");
            String dateString = scanner.nextLine();

            try {
                if (dateString.isEmpty()) {
                    date = LocalDate.now();
                } else {
                    date = LocalDate.parse(dateString);

                    LocalDate minDate = LocalDate.of(2003, 1, 1);

                    if (date.isAfter(today)) {
                        System.out.println("Entered date is greater than today's date. Please enter a date within the range from 2003-01-01 to " + today + ".");
                        date = null;
                    } else if (date.isBefore(minDate)) {
                        System.out.println("Entered date is earlier than the supported date range. Please enter a date within the range from 2003-01-01 to " + today + ".");
                        date = null;
                    }
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter a valid date in the format yyyy-MM-dd");
            }
        }
        return date;
    }

    public static double getCustomAmount(Scanner scanner) {
        double amount = 1;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Enter custom amount (or press ENTER for default 1): ");
            String userInput = scanner.nextLine().trim();

            if (!userInput.isEmpty()) {
                try {
                    amount = Double.parseDouble(userInput);
                    validInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            } else {
                validInput = true;
            }
        }

        System.out.println("Selected amount: " + amount);
        return amount;
    }
}
