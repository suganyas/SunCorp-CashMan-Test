package com.suncorp.cashman.exception;

public class CashSupplyException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        private final String message;

        public CashSupplyException(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
}
