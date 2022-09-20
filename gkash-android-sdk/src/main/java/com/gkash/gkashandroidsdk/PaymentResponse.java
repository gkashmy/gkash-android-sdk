package com.gkash.gkashandroidsdk;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;

public class PaymentResponse {
    public String description;
    public String CID;
    public String POID;
    public String status;
    public String currency;
    public BigDecimal amount;
    public String cartid;
    public String PaymentType;

    public PaymentResponse(String description, String CID, String POID, String status, String currency, BigDecimal amount, String cartid, String paymentType) {
        this.description = description;
        this.CID = CID;
        this.POID = POID;
        this.status = status;
        this.currency = currency;
        this.amount = amount;
        this.cartid = cartid;
        this.PaymentType = paymentType;
    }

    public String generateSignature(String signatureKey) {
        String signStr = (signatureKey + ";" + CID + ";" + POID + ";" + cartid + ";" + amount.toString().replace(".", "") + ";" + currency + ";" + status).toUpperCase();
        return getSHA512(signStr);
    }

    private String getSHA512(String input){
        String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(input.getBytes("utf8"));
            result = String.format("%0128x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
