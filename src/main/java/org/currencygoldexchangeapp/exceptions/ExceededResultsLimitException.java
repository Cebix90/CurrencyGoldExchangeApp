package org.currencygoldexchangeapp.exceptions;

public class ExceededResultsLimitException extends RuntimeException {
    public ExceededResultsLimitException() {
        super("Data series has been exceeded");
    }
}
