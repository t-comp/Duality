package com.coms309.duality.controllers;

import com.coms309.duality.model.Payment;
import com.coms309.duality.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PersonRepository personRepository;

    /**
     * Validates length of card details.
     *
     * @param cardNumber - the number of the card
     * @param expirationDate - the cards expiration date in MM/YY format
     * @param cvv - the cards cvv value
     * @return true or false depending on the validity of the card
     */
    public boolean isCardValid(String cardNumber, String expirationDate, String cvv) {
        if (cardNumber.matches("\\d{16}") && expirationDate.matches("(0[1-9]|1[0-2])/?[0-9]{2}") && cvv.matches("\\d{3}")) {
            return true;
        }
        return false;
    }

    /**
     * Allows user to buy something with their form of payment.
     * @param payment - the payment information
     * @return a boolean value indicating the result of the operation
     */
    public boolean buy(Payment payment) {
        if (!isCardValid(payment.getCardNumber(), payment.getExpirationDate(), payment.getCvv())) {
            return false;
        }
        payment.setBought(true);
        return true;
    }

}
