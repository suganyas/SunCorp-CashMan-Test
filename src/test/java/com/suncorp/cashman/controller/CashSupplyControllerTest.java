package com.suncorp.cashman.controller;

import com.suncorp.cashman.denominations.AustralianCurrency;
import com.suncorp.cashman.exception.CashSupplyException;
import com.suncorp.cashman.interfaces.Currency;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.collections.Maps;

import java.util.Map;

public class CashSupplyControllerTest {

    @Mock
    private Map<Currency, Integer> mockAvailableDenominations =  Maps.newHashMap();
    private CashSupplyController mockCashSupplyController;
    private  Map<String, Integer> denominations = Maps.newHashMap();

    @Before
    public void setUp() {
        mockCashSupplyController = new CashSupplyController();
        denominations.put("HUNDREDDOLLAR",10);
        denominations.put("ABCD",10);
        denominations.put("ONEDOLLAR",100);
        denominations.put("TENCENTS",50);
        mockAvailableDenominations.put(AustralianCurrency.HUNDREDDOLLAR,10);
        mockAvailableDenominations.put(AustralianCurrency.ONEDOLLAR,100);
        mockAvailableDenominations.put(AustralianCurrency.TWODOLLAR,100);
    }

    @Test
    public void testAddValuesToDenominations() {
        Whitebox.setInternalState(mockCashSupplyController, "availableDenominations", mockAvailableDenominations);
        mockCashSupplyController.addValuesToDenominations(denominations);
        Assert.assertEquals(3,mockAvailableDenominations.size());
        Assert.assertEquals(false, mockAvailableDenominations.containsKey("ABCD"));
        Assert.assertEquals(20, mockAvailableDenominations.get(AustralianCurrency.HUNDREDDOLLAR).intValue());
        Assert.assertEquals(200, mockAvailableDenominations.get(AustralianCurrency.ONEDOLLAR).intValue());
        Assert.assertEquals(100, mockAvailableDenominations.get(AustralianCurrency.TWODOLLAR).intValue());
    }

    @Test
    public void testGetValuesToDenominations() {
        Whitebox.setInternalState(mockCashSupplyController, "availableDenominations", mockAvailableDenominations);
        ResponseEntity<Map> response = mockCashSupplyController.getValuesOfDenominations();
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
        Assert.assertEquals(3,mockAvailableDenominations.size());
    }

    @Test
    public void testWithDrawValidAmount() {
        Whitebox.setInternalState(mockCashSupplyController, "availableDenominations", mockAvailableDenominations);
        ResponseEntity<Map> response = mockCashSupplyController.withDrawCash(100.00);
        Assert.assertEquals(HttpStatus.OK,response.getStatusCode());
        Assert.assertEquals(3,mockAvailableDenominations.size());
        Assert.assertEquals(1,response.getBody().size());
        Assert.assertEquals(1,response.getBody().get(AustralianCurrency.HUNDREDDOLLAR));
    }

    @Test(expected = CashSupplyException.class)
    public void testWithDrawOverAvailableAmount() {
        Whitebox.setInternalState(mockCashSupplyController, "availableDenominations", mockAvailableDenominations);
        ResponseEntity<Map> response = mockCashSupplyController.withDrawCash(100000.00);
    }

    @Test(expected = CashSupplyException.class)
    public void testWithDrawInValidAmount() {
        Whitebox.setInternalState(mockCashSupplyController, "availableDenominations", mockAvailableDenominations);
        ResponseEntity<Map> response = mockCashSupplyController.withDrawCash(-100.00);
    }

}
