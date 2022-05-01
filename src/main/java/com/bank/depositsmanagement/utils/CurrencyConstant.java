package com.bank.depositsmanagement.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyConstant {
    public static final NumberFormat VND_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    public static final NumberFormat USD_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);
}
