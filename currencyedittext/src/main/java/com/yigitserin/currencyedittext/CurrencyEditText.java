package com.yigitserin.currencyedittext;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CurrencyEditText extends AppCompatEditText {

    public interface NumericValueWatcher {
        void onChanged(double newValue);
        void onCleared();
    }

    public Locale locale = new Locale("en","US");
    public int decimalDigits = 2;

    private char GROUPING_SEPARATOR;
    private char DECIMAL_SEPARATOR;
    private String LEADING_ZERO_FILTER_REGEX;

    private String mDefaultText = null;
    private String mPreviousText = "";
    private String mNumberFilterRegex;
    private List<NumericValueWatcher> mNumericListeners = new ArrayList<>();

    //region TextWatcher

    private final TextWatcher mTextWatcher = new TextWatcher() {
        private boolean validateLock = false;

        @Override
        public void afterTextChanged(Editable s) {

            String text = s.toString();

            if (validateLock) {
                return;
            }

            // If user presses GROUPING_SEPARATOR, convert it to DECIMAL_SEPARATOR
            if (text.endsWith(GROUPING_SEPARATOR+"")){
                text = text.substring(0,text.length()-1) + DECIMAL_SEPARATOR;
            }

            // Limit decimal digits
            if (decimalDigitLimitReached(text)){
                validateLock = true;
                setText(mPreviousText); // cancel change and revert to previous input
                setSelection(mPreviousText.length());
                validateLock = false;
                return;
            }

            // valid decimal number should not have thousand separators after a decimal separators
            if (hasGroupingSeperatorAfterDecimalSeperator(text)){
                validateLock = true;
                setText(mPreviousText); // cancel change and revert to previous input
                setSelection(mPreviousText.length());
                validateLock = false;
                return;
            }


            // valid decimal number should not have more than 2 decimal separators
            if (countMatches(text, String.valueOf(DECIMAL_SEPARATOR)) > 1) {
                validateLock = true;
                setText(mPreviousText); // cancel change and revert to previous input
                setSelection(mPreviousText.length());
                validateLock = false;
                return;
            }

            if (text.length() == 0) {
                handleNumericValueCleared();
                return;
            }

            setTextInternal(format(text));
            setSelection(getText().length());
            handleNumericValueChanged();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // do nothing
        }
    };

    //endregion

    public CurrencyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        setInputType(InputType.TYPE_CLASS_PHONE);
        setTextAlignment(TEXT_ALIGNMENT_TEXT_END);

        reload();

        addTextChangedListener(mTextWatcher);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // disable moving cursor
                setSelection(getText().length());
            }
        });
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        reload();
    }

    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
        reload();
    }

    private void reload(){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);

        GROUPING_SEPARATOR = symbols.getGroupingSeparator();
        DECIMAL_SEPARATOR = symbols.getDecimalSeparator();
        LEADING_ZERO_FILTER_REGEX = "^0+(?!$)";
        mNumberFilterRegex = "[^\\d\\" + this.DECIMAL_SEPARATOR + "]";
    }

    //region Utils

    private void handleNumericValueCleared() {
        mPreviousText = "";
        for (NumericValueWatcher listener : mNumericListeners) {
            listener.onCleared();
        }
    }

    private void handleNumericValueChanged() {
        mPreviousText = getText().toString();
        for (NumericValueWatcher listener : mNumericListeners) {
            listener.onChanged(getNumericValue());
        }
    }

    public void addNumericValueChangedListener(NumericValueWatcher watcher) {
        mNumericListeners.add(watcher);
    }

    public void removeAllNumericValueChangedListeners() {
        while (!mNumericListeners.isEmpty()) {
            mNumericListeners.remove(0);
        }
    }

    public void setDefaultNumericValue(double defaultNumericValue, final String defaultNumericFormat) {
        mDefaultText = String.format(defaultNumericFormat, defaultNumericValue);
        setTextInternal(mDefaultText);
    }

    public void clear() {
        setTextInternal(mDefaultText != null ? mDefaultText : "");
        if (mDefaultText != null) {
            handleNumericValueChanged();
        }
    }

    public double getNumericValue() {
        String original = getText().toString().replaceAll(mNumberFilterRegex, "");
        try {
            return NumberFormat.getInstance().parse(original).doubleValue();
        } catch (ParseException e) {
            return Double.NaN;
        }
    }

    private String format(final String original) {
        //Dot is special character in regex, so we have to treat it specially.
        if (DECIMAL_SEPARATOR == '.'){
            final String[] parts = original.split("\\.", -1);
            String number = parts[0].replaceAll(mNumberFilterRegex, "").replaceFirst(LEADING_ZERO_FILTER_REGEX, "");

            number = reverse(reverse(number).replaceAll("(.{3})", "$1" + GROUPING_SEPARATOR));
            number = removeStart(number, String.valueOf(GROUPING_SEPARATOR));

            if (parts.length > 1) {
                parts[1] = parts[1].replaceAll(mNumberFilterRegex,"");
                number += DECIMAL_SEPARATOR + parts[1];
            }

            return number;
        }else{
            final String[] parts = original.split(DECIMAL_SEPARATOR+"", -1);
            String number = parts[0].replaceAll(mNumberFilterRegex, "").replaceFirst(LEADING_ZERO_FILTER_REGEX, "");

            number = reverse(reverse(number).replaceAll("(.{3})", "$1" + GROUPING_SEPARATOR));
            number = removeStart(number, String.valueOf(GROUPING_SEPARATOR));

            if (parts.length > 1) {
                parts[1] = parts[1].replaceAll(mNumberFilterRegex,"");
                number += DECIMAL_SEPARATOR + parts[1];
            }

            return number;
        }


    }

    private void setTextInternal(String text) {
        removeTextChangedListener(mTextWatcher);
        setText(text);
        addTextChangedListener(mTextWatcher);
    }

    private String reverse(String original) {
        if (original == null || original.length() <= 1) {
            return original;
        }
        return TextUtils.getReverse(original, 0, original.length()).toString();
    }

    private String removeStart(String str, String remove) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        if (str.startsWith(remove)){
            return str.substring(remove.length());
        }
        return str;
    }

    private int countMatches(String str, String sub) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        int lastIndex = str.lastIndexOf(sub);
        if (lastIndex < 0) {
            return 0;
        } else {
            return 1 + countMatches(str.substring(0, lastIndex), sub);
        }
    }

    private boolean hasGroupingSeperatorAfterDecimalSeperator(String text){
        //Return true if thousand seperator (.) comes after a decimal seperator. (,)

        if (text.contains(GROUPING_SEPARATOR+"") && text.contains(DECIMAL_SEPARATOR+"")){
            int firstIndexOfDecimal = text.indexOf(DECIMAL_SEPARATOR);
            int lastIndexOfGrouping = text.lastIndexOf(GROUPING_SEPARATOR);

            if (firstIndexOfDecimal < lastIndexOfGrouping){
                return true;
            }
        }

        return false;
    }

    private boolean decimalDigitLimitReached(String text){
        //Return true if decimal digit limit is reached
        if (text.contains(DECIMAL_SEPARATOR+"")){

            if (DECIMAL_SEPARATOR == '.'){
                //Dot is special character in regex, so we have to treat it specially.
                String[] parts = text.split("\\.");
                if (parts.length>0){
                    String lastPart = parts[parts.length-1];

                    if (lastPart.length() == decimalDigits + 1){
                        return true;
                    }
                }
            }else{
                //If decimal seperator is not a dot, we can safely split.
                String[] parts = text.split(DECIMAL_SEPARATOR+"");
                if (parts.length>0){
                    String lastPart = parts[parts.length-1];

                    if (lastPart.length() == decimalDigits + 1){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    //endregion
}
