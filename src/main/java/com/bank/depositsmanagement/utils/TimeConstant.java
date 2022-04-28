package com.bank.depositsmanagement.utils;


import java.time.format.DateTimeFormatter;

public class TimeConstant {
    public static final int MONTH_OF_YEAR = 12;
    public static final int DAY_OF_MONTH = 30;
    public static final int DAY_OF_YEAR = 360;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
}
