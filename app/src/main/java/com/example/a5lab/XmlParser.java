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
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, "UTF-8");

            int eventType = parser.getEventType();
            String currentCode = "";
            String currentName = "";
            double currentRate = 0.0;
            boolean inItem = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("item".equals(tagName)) {
                            inItem = true;
                            currentCode = "";
                            currentName = "";
                            currentRate = 0.0;
                            System.out.println("XmlParser: Found new item");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (inItem) {
                            String text = parser.getText().trim();
                            if (!text.isEmpty()) {
                                switch (tagName) {
                                    case "targetCurrency":
                                        currentCode = text;
                                        System.out.println("XmlParser: Found currency code: " + text);
                                        break;
                                    case "targetName":
                                        currentName = text;
                                        System.out.println("XmlParser: Found currency name: " + text);
                                        break;
                                    case "exchangeRate":
                                        try {
                                            currentRate = Double.parseDouble(text);
                                            System.out.println("XmlParser: Found exchange rate: " + text);
                                        } catch (NumberFormatException e) {
                                            System.out.println("XmlParser: Error parsing rate: " + text);
                                        }
                                        break;
                                }
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if ("item".equals(tagName) && inItem) {
                            if (!currentCode.isEmpty() && currentRate > 0) {
                                Currency currency = new Currency(currentCode, currentName, currentRate);
                                currencies.add(currency);
                                System.out.println("XmlParser: Added currency to list: " + currentCode);
                            }
                            inItem = false;
                        }
                        break;
                }

                eventType = parser.next();
            }

            System.out.println("XmlParser: Parsing completed. Total currencies: " + currencies.size());
            return currencies;

        } catch (Exception e) {
            System.out.println("XmlParser: Error parsing XML - " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
