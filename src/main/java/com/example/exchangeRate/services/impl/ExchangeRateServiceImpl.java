package com.example.exchangeRate.services.impl;
import com.example.exchangeRate.client.cbrClient;
import com.example.exchangeRate.exception.serviceException;
import com.example.exchangeRate.services.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
    // адреса выражений, которые нужно вытащить из XML
    private static final String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/Value";
    private static final String EUR_XPATH = "/ValCurs//Valute[@ID='R01239']/Value";
    @Autowired
    private cbrClient cbrClient;

    @Override
    public String getUSDCurrencyRate() throws serviceException {
        var xml = cbrClient.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml, USD_XPATH);
    }

    @Override
    public String getEURCurrencyRate() throws serviceException {
        var xml = cbrClient.getCurrencyRatesXML();
        return extractCurrencyValueFromXML(xml, EUR_XPATH);
    }

    private static String extractCurrencyValueFromXML(String xml, String xpathExpression) throws serviceException {
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = xpath.evaluate("/", source, XPathConstants.NODE);

            return xpath.evaluate(xpathExpression, document);
        } catch (XPathExpressionException e) {
            throw new serviceException("Не удалось распарсить XML", e);
        }
    }

    private static String extractCurrencyValueFromIMOEX(String xml, String xpathExpression)throws serviceException{
        var source = new InputSource(new StringReader(xml));
        try {
            var xpath = XPathFactory.newInstance().newXPath();
            var document = xpath.evaluate("/", source, XPathConstants.NODE);

            return xpath.evaluate(xpathExpression, document);
        } catch (XPathExpressionException e) {
            throw new serviceException("Не удалось распарсить XML", e);
        }
    }
}
