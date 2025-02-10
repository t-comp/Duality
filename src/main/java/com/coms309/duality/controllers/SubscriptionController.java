package com.coms309.duality.controllers;

import com.coms309.duality.model.Payment;
import com.coms309.duality.model.Person;
import com.coms309.duality.model.Subscription;
import com.coms309.duality.model.UserType;
import com.coms309.duality.repository.PersonRepository;
import com.coms309.duality.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controls subscriptions tasks such as users purchasing and canceling subscriptions and updates user information.
 * Additionally, uses some card validation for length.
 * @author Taylor Bauer
 */
@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private PaymentController paymentController;


    /**
     * Allows user to buy a subscription if they do not have an active one.
     *
     * @param userId - the id of the user buying subscription
     * @return a message with details of the transaction
     */
    @PostMapping("buy-subscription/{userId}")
    public String buySubscription(@PathVariable long userId, @RequestBody Payment paymentRequest) {
        Person p = personRepository.findById(userId);
        if (!p.isLoggedIn()) {
            return "You are not logged in!";
        }
        if (p.getSubscription() != null && p.getSubscription().isSubscriptionActive()) {
            return "Subscription is already active!";
        }
        // set sub and type
        paymentRequest.setAmount(2.49);
        paymentRequest.setPaymentType("SUBSCRIPTION");

        // payment
        Payment payment = new Payment();
        payment.setCardNumber(paymentRequest.getCardNumber());
        payment.setExpirationDate(paymentRequest.getExpirationDate());
        payment.setCvv(paymentRequest.getCvv());
        payment.setAmount(paymentRequest.getAmount());
        payment.setBuyer(p);

        if (paymentController.buy(payment)) {
            Subscription sub = new Subscription();
            subscriptionRepository.save(sub);
            p.setSubscription(sub);
            p.setType(UserType.PREMIUM);
            personRepository.save(p);
            return "Your subscription has been purchased. Welcome, new premium user!";
        }
        return "Sorry, payment failed. Please try again!";
    }

    /**
     * Allows a user to cancel their subscription if it is active.
     *
     * @param userId - the id of the user that is cancelling
     * @return a message indicating the outcome of the operation
     */
    @PutMapping("/cancel-subscription/{userId}")
    public String cancelSubscription(@PathVariable long userId) {
        Person p = personRepository.findById(userId);

        Subscription sub = p.getSubscription();

        if (sub == null || !sub.isSubscriptionActive()) {
            return "You have no subscription to cancel. You should buy one first!";
        }

        sub.setSubscriptionActive(false);
        subscriptionRepository.save(sub);
        p.setSubscription(null);
        p.setType(UserType.DEFAULT);
        personRepository.save(p);
        return "Your subscription has been cancelled. We are sorry to see you go!";
    }


}
