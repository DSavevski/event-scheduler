package com.sorsix.eventscheduler.service;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.base.rest.PayPalResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayPalService {

    private APIContext apiContext;
    private OAuthTokenCredential credential;

    @PostConstruct
    public void init() {
        InputStream is = PayPalService.class.getResourceAsStream("/sdk_config.properties");
        try {
            credential = PayPalResource.initConfig(is);
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
    }

    public void config() {
        try {
            String accessToken = credential.getAccessToken();
            apiContext = new APIContext(accessToken);

        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
    }

    public Payment createPayment(){
        config();

        // Set payer details
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        // Set redirect URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setReturnUrl("http://localhost:4200/process-payment");
        redirectUrls.setCancelUrl("http://localhost:4200/process-payment");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setRedirectUrls(redirectUrls);
        payment.setTransactions(getTransactionsList());

        try {
            payment = payment.create(apiContext);
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return payment;
    }

    public Payment executePayment(String paymentId, String payerId)
    {
        // ### Api Context
        // Pass in a `APIContext` object to authenticate
        // the call and to send a unique request id
        // (that ensures idempotency). The SDK generates
        // a request id if you do not pass one explicitly.

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        Payment payment = new Payment();
        payment.setId(paymentId);

        // Execute the payment.
        Payment executedPayment = null;
        try {
            executedPayment = payment.execute(this.apiContext, paymentExecution);
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }

        return executedPayment;
    }

    private List<Transaction> getTransactionsList()
    {
        // A transaction defines the contract of a payment
        // what is the payment for and who is fulfilling it.
        ArrayList<Transaction> transactionList = new ArrayList<>();

        // The Payment creation API requires a list of Transaction;
        // add the created Transaction to a List
        Transaction transaction = new Transaction();

        // Set payment details
        Details details = new Details();
        details.setShipping("1");
        details.setSubtotal("5");
        details.setTax("1");

        // Payment amount
        Amount amount = new Amount();
        amount.setCurrency("USD");
        // Total must be equal to sum of shipping, tax and subtotal.
        amount.setTotal("7");
        amount.setDetails(details);

        transaction.setAmount(amount);
        transaction.setDescription("This is the payment transaction description.");

        // Add transaction to a list
        transactionList.add(transaction);
        return transactionList;
    }
}
