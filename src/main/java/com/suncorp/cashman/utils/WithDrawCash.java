package com.suncorp.cashman.utils;

import com.google.common.collect.Maps;
import com.suncorp.cashman.exception.CashSupplyException;
import com.suncorp.cashman.interfaces.Currency;

import javax.websocket.Session;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class WithDrawCash {

    private final double MIN_THRESHOLD_AMOUNT = 1000;

    public Map<Currency, Integer> withDrawCash(Double withdrawAmount, Map<Currency, Integer> availableDenominations) throws CashSupplyException {
        Map<Currency, Integer> withDrawnCash = withdrawCashFromATM(withdrawAmount, availableDenominations);
        if(!withDrawnCash.isEmpty()) {
            updateWithdrawal(withDrawnCash, availableDenominations);
        }
        return withDrawnCash;
    }

    private Map<Currency, Integer> withdrawCashFromATM(Double amount, Map<Currency, Integer> availableDenominations) throws CashSupplyException{
        Map<Currency, Integer> toWithdraw = Maps.newHashMap();
        List<Currency> denominations = availableDenominations.entrySet().stream().filter(entry -> entry.getValue() > 0).map(Map.Entry::getKey).collect(Collectors.toList());

        denominations.sort(Comparator.comparing(Currency::getValue).reversed());

        for (int start = 0; start < denominations.size(); start++) {
            toWithdraw = determineDenominations(start, denominations, amount, availableDenominations);
            if (!toWithdraw.isEmpty()) {
                break;
            }
        }
        return toWithdraw;
    }

    private Map<Currency, Integer> determineDenominations(int start, List<Currency> denominations, Double amount,Map<Currency, Integer> availableDenominations ) {
        Map<Currency, Integer> toWithdraw = new HashMap<>();
        int totalCurrency = availableDenominations.values().stream().mapToInt(i -> i.intValue()).sum();
        for (int currencyCount = 0; currencyCount < totalCurrency; currencyCount++) {
            toWithdraw.clear();
            double interAmount = amount.doubleValue();
            int interCurrencyCount = currencyCount;
            for (int i = start; i < denominations.size(); i++) {
                double value = denominations.get(i).getValue().doubleValue();
                if (value <= interAmount) {
                    int count = (int) Math.floor((interAmount / value) - interCurrencyCount);
                    toWithdraw.put(denominations.get(i), count);
                    //return empty withdrawal to start again with next denomination
                    if(availableDenominations.get(denominations.get(i)) < count) {
                        toWithdraw.clear();
                        return toWithdraw;
                    }
                    interAmount =interAmount - (count * value);
                    interCurrencyCount = 0;
                    double withdrawSum = toWithdraw.entrySet().stream().mapToDouble(w -> w.getKey().getValue()* w.getValue()).sum();
                    if (withdrawSum == amount) {
                        return toWithdraw;
                    }
                }
            }
        }
        return toWithdraw;
    }

    private void updateWithdrawal(Map<Currency, Integer> toWithdraw,Map<Currency, Integer> availableDenominations ) {
        synchronized (availableDenominations) {
            toWithdraw.entrySet().forEach(entry -> {
                Integer deduction = availableDenominations.get(entry.getKey()) - entry.getValue();
                availableDenominations.put(entry.getKey(), deduction);
            });
        }
        Double amountLeft = availableDenominations.entrySet().stream().mapToDouble( a-> a.getValue() * a.getKey().getValue()).sum();
        if(amountLeft < MIN_THRESHOLD_AMOUNT) {
            notifyToReloadCash();
        }
    }

    private void notifyToReloadCash(){
       //configure email alerts or push notifications when the cash left in atm is less than 1000 $
}

}
