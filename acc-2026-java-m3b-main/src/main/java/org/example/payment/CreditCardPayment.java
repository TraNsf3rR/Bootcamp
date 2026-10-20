package org.example.payment;

import org.example.model.PaymentResult;

public class CreditCardPayment extends PaymentMethod {
    private final String cardNumber;
    private final String cardHolderName;

    public CreditCardPayment(String cardNumber, String cardHolderName) {
        super("CreditCard");
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
    }

    @Override
    public PaymentResult processPayment(double amount) {
//          Added basic validation
        if (cardNumber == null || cardNumber.isBlank()) {
            return new PaymentResult(false, "Card number is required.");
        }
        if (cardHolderName == null || cardHolderName.isBlank()) {
            return new PaymentResult(false, "Card holder name is required.");
        }
        if (cardNumber.length() < 4) {
            return new PaymentResult(false, "Card number must have at least 4 characters.");
        }
        return new PaymentResult(true, "Paid " + amount + " using credit card ending with " + cardNumber.substring(cardNumber.length() - 4));
    }
}
