package org.currencygoldexchangeapp.exceptions;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException() {
        super("No data available for the given date");
    }
}
