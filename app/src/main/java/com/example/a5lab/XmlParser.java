package com.example.a5lab;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlParser {

    public List<Currency> parseXmlData(InputStream inputStream) {
        System.out.println("XmlParser: Starting XML parsing");

        List<Currency> currencies = new ArrayList<>();

        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            String currentCode = null;
            String currentName = null;
            String currentRate = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();

                    if ("targetCurrency".equals(tagName)) {
                        eventType = parser.next();
                        if (eventType == XmlPullParser.TEXT) {
                            currentCode = parser.getText().trim();
                            System.out.println("XmlParser: Code: " + currentCode);
                        }
                    }
                    else if ("targetName".equals(tagName)) {
                        eventType = parser.next();
                        if (eventType == XmlPullParser.TEXT) {
                            currentName = parser.getText().trim();
                            System.out.println("XmlParser: Name: " + currentName);
                        }
                    }
                    else if ("exchangeRate".equals(tagName)) {
                        eventType = parser.next();
                        if (eventType == XmlPullParser.TEXT) {
                            currentRate = parser.getText().trim();
                            System.out.println("XmlParser: Rate: " + currentRate);
                        }
                    }
                    else if ("item".equals(tagName)) {
                        currentCode = null;
                        currentName = null;
                        currentRate = null;
                    }
                }
                else if (eventType == XmlPullParser.END_TAG) {
                    String tagName = parser.getName();
                    if ("item".equals(tagName)) {
                        if (currentCode != null && currentRate != null) {
                            try {
                                double rateValue = Double.parseDouble(currentRate);
                                Currency currency = new Currency(currentCode, currentName != null ? currentName : "", rateValue);
                                currencies.add(currency);
                                System.out.println("XmlParser: ADDED: " + currentCode + " = " + rateValue);
                            } catch (NumberFormatException e) {
                                System.out.println("XmlParser: Invalid rate: " + currentRate);
                            }
                        }
                    }
                }

                eventType = parser.next();
            }

            System.out.println("XmlParser: Successfully parsed " + currencies.size() + " currencies");
            return currencies;

        } catch (Exception e) {
            System.out.println("XmlParser: Error - " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}