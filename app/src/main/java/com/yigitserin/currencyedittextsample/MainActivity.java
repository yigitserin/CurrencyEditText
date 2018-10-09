package com.yigitserin.currencyedittextsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yigitserin.currencyedittext.CurrencyEditText;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CurrencyEditText cetPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cetPayment = findViewById(R.id.cetPayment);
        cetPayment.setLocale(new Locale("tr", "TR"));
        cetPayment.setDecimalDigits(2);
    }
}
