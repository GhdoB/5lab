package com.example.a5lab;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class XmlParserUnitTest {

    private XmlParser parser;

    @Before
    public void setUp() {
        parser = new XmlParser();
    }

    @Test
    public void testParseValidXml_returnsCurrencyList() {
        String xmlData =
                "<channel>" +
                        "<item>" +
                        "<targetCurrency>EUR</targetCurrency>" +
                        "<targetName>Euro</targetName>" +
                        "<exchangeRate>0.92</exchangeRate>" +
                        "</item>" +
                        "<item>" +
                        "<targetCurrency>JPY</targetCurrency>" +
                        "<targetName>Japanese Yen</targetName>" +
                        "<exchangeRate>150.12</exchangeRate>" +
                        "</item>" +
                        "</channel>";

        InputStream inputStream = new ByteArrayInputStream(xmlData.getBytes());
        List<Currency> result = parser.parseXmlData(inputStream);

        assertNotNull(result);
        assertEquals(2, result.size());

        Currency eur = result.get(0);
        assertEquals("EUR", eur.getCode());
        assertEquals("Euro", eur.getName());
        assertEquals(0.92, eur.getExchangeRate(), 0.0001);

        Currency jpy = result.get(1);
        assertEquals("JPY", jpy.getCode());
        assertEquals("Japanese Yen", jpy.getName());
        assertEquals(150.12, jpy.getExchangeRate(), 0.0001);
    }

    @Test
    public void testParseInvalidXml_returnsEmptyList() {
        String invalidXml = "<channel><item><targetCurrency>USD"; // broken XML
        InputStream inputStream = new ByteArrayInputStream(invalidXml.getBytes());

        List<Currency> result = parser.parseXmlData(inputStream);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testParseEmptyXml_returnsEmptyList() {
        String emptyXml = "";
        InputStream inputStream = new ByteArrayInputStream(emptyXml.getBytes());

        List<Currency> result = parser.parseXmlData(inputStream);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

