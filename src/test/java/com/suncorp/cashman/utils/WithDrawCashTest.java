package com.suncorp.cashman.utils;

import com.suncorp.cashman.denominations.AustralianCurrency;
import com.suncorp.cashman.interfaces.Currency;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.testng.annotations.BeforeTest;
import org.testng.collections.Maps;

import java.util.Map;

public class WithDrawCashTest {
    @Mock
    private Map<Currency, Integer> mockAvailableDenominations =  Maps.newHashMap();
    WithDrawCash withDrawCash = new WithDrawCash();


    @Before
    public void setUp() {
        mockAvailableDenominations.put(AustralianCurrency.HUNDREDDOLLAR,10);
        mockAvailableDenominations.put(AustralianCurrency.ONEDOLLAR,100);
        mockAvailableDenominations.put(AustralianCurrency.TWODOLLAR,100);
        mockAvailableDenominations.put(AustralianCurrency.TENCENTS,10);

    }

    @Test
    public void testWithdrawValidAmount(){
        Map<Currency, Integer> withDrawnCash = withDrawCash.withDrawCash(1000.00, mockAvailableDenominations);
        Assert.assertEquals(false,withDrawnCash.isEmpty());
        Assert.assertEquals(1,withDrawnCash.size());
        Assert.assertEquals(10,withDrawnCash.get(AustralianCurrency.HUNDREDDOLLAR).intValue());
        Assert.assertEquals(0,mockAvailableDenominations.get(AustralianCurrency.HUNDREDDOLLAR).intValue());
    }

    @Test
    public void testWithdrawInValidAmount(){
        Map<Currency, Integer> withDrawnCash = withDrawCash.withDrawCash(10000.00, mockAvailableDenominations);
        Assert.assertEquals(true,withDrawnCash.isEmpty());
    }


}
