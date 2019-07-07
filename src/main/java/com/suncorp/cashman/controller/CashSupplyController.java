package com.suncorp.cashman.controller;

import com.suncorp.cashman.denominations.AustralianCurrency;
import com.suncorp.cashman.exception.CashSupplyException;
import com.suncorp.cashman.interfaces.Currency;
import com.suncorp.cashman.utils.WithDrawCash;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
public class CashSupplyController {

	private Map<Currency, Integer> availableDenominations;
	private final String DENOMINATION_CONFIG_FILE = "denomination.config";
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PostConstruct
	public void intialiseDenominations() {
		logger.trace("Entered intialiser for denominations of Controller");
		HashMap<Currency, Integer> denominationsCount= new HashMap<Currency, Integer>();
		try (InputStream input = CashSupplyController.class.getClassLoader().getResourceAsStream(DENOMINATION_CONFIG_FILE)) {

			Properties denominations = new Properties();
			if (input == null) {
				logger.debug("CacheSupplyException thrown as the denomination.config file is not available");
				throw new CashSupplyException("The denomination.config file is not available to load initial configured counts for denomination");
			}
			denominations.load(input);
			input.close();
			denominations.entrySet().forEach(d -> denominationsCount.put(AustralianCurrency.valueOf(d.getKey().toString()), Integer.parseInt(d.getValue().toString())));
		} catch (IOException exception) {
			logger.error(exception.getStackTrace().toString());
			throw new CashSupplyException(exception.getMessage());
		}
		availableDenominations = Collections.synchronizedMap(denominationsCount);
		logger.trace("Added Values for each denomination from the properties file to a synchronised map");
	}


	@RequestMapping(value = "/addValuesToDenominations", method = {RequestMethod.POST}, produces = "application/json")
	public ResponseEntity<Map> addValuesToDenominations(@RequestBody Map<String, Integer> denominations) {
		logger.trace("Entered into function addValuesToDenominations with values " + denominations.toString());
		for (Map.Entry<String, Integer> entry : denominations.entrySet()) {
			try {
				Currency currency = AustralianCurrency.valueOf(entry.getKey());
				synchronized (availableDenominations) {
					if (availableDenominations.get(currency) != null) {
						availableDenominations.put(currency, availableDenominations.get(currency) + entry.getValue());
						logger.debug("Added value to {} {} {}" , currency.getValue() , currency.getCurrencyCode() ,entry.getValue() );
					}
				}
			}catch (IllegalArgumentException exception) {
				logger.debug(exception.getMessage());
				continue;
			}
		}
		return new ResponseEntity<Map>(availableDenominations, HttpStatus.OK);
	}

	@RequestMapping(value = "/getValuesOfDenominations", method = {RequestMethod.GET}, produces = "application/json")
	public ResponseEntity<Map> getValuesOfDenominations() {
		return new ResponseEntity<Map>(availableDenominations, HttpStatus.OK);
	}

	@RequestMapping(value = "/withdrawCash", method = {RequestMethod.POST}, produces = "application/json")
	public ResponseEntity withDrawCash(@RequestParam Double amount) {
		if(amount <= 0) {
			throw new CashSupplyException("Enter a valid amount to withdraw");
		}
		amount = Math.round(amount * 2) / 2.0;

		WithDrawCash withDrawCash = new WithDrawCash();
		Map<Currency, Integer> withDrawnCash = withDrawCash.withDrawCash(amount, availableDenominations);
		if(withDrawnCash.isEmpty()) {
			throw new CashSupplyException("Requested Amount could not be withdrawn!! Try different amount!");

		}
		return new ResponseEntity<Map>(withDrawnCash, HttpStatus.OK);
	}

}