# CurrencyEditText

<img src="https://raw.githubusercontent.com/yigitserin/CurrencyEditText/master/Preview.gif" width="240" height="480" />

CurrencyEditText is an EditText library for inputting currency values. It supports grouping and decimal seperators.

  ##### Step 1. Add the dependency
  
  ```gradle
repositories {
  	mavenCentral()
  	google()
}

dependencies {
  	implementation 'com.yigitserin.currencyedittext:CurrencyEditText:0.1'
}
```
  
  ##### Step 2. Configure
  ```java
    CurrencyEditText etCurrency = findViewById(R.id.etCurrency);
    etCurrency.setLocale(new Locale("tr","TR"));
    etCurrency.setDecimalDigits(2);
   ```
  
  ##### Step 3. Enjoy
  
  
  ### Licence
  
  MIT License

Copyright (c) 2018 Yigit Serin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
