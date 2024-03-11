package org.currencygoldexchangeapp;

import org.currencygoldexchangeapp.services.UserMenuService;

public class Main {
    public static void main(String[] args) {
        UserMenuService userMenuService = new UserMenuService();
        userMenuService.runApplication();
    }
}