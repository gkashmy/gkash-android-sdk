package com.gkash.gkashandroidsdk;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;

public class PaymentRequest {
    private String version;
    private String cid;
    private String signatureKey;
    private String currency;
    private BigDecimal amount;
    private String cartId;
    private String callbackUrl;
    private String returnUrl;           // Android package name
    private String email;               // Optional
    private String mobileNo;            // Optional
    private String firstName;           // Optional
    private String lastName;            // Optional
    private String productDescription;  // Optional
    private String billingStreet;       // Optional
    private String billingPostCode;     // Optional
    private String billingCity;         // Optional
    private String billingState;        // Optional
    private String billingCountry;      // Optional

    private String strAmount;

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

    public PaymentRequest(String version, String cid, String signatureKey, String currency, BigDecimal amount, String cartId) {
        this.version = version;
        this.cid = cid;
        this.signatureKey = signatureKey;
        this.currency = currency;
        this.amount = amount;
        this.cartId = cartId;

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        this.strAmount = decimalFormat.format(amount);
    }

    public String generateSignature() {
        String signStr = (signatureKey + ";" + cid + ";" + cartId + ";" + strAmount.replace(".", "") + ";" + currency).toUpperCase();
        return getSHA512(signStr);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getSignatureKey() {
        return signatureKey;
    }

    public void setSignatureKey(String signatureKey) {
        this.signatureKey = signatureKey;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStrAmount() {
        return strAmount;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getBillingStreet() {
        return billingStreet;
    }

    public void setBillingStreet(String billingStreet) {
        this.billingStreet = billingStreet;
    }

    public String getBillingPostCode() {
        return billingPostCode;
    }

    public void setBillingPostCode(String billingPostCode) {
        this.billingPostCode = billingPostCode;
    }

    public String getBillingCity() {
        return billingCity;
    }

    public void setBillingCity(String billingCity) {
        this.billingCity = billingCity;
    }

    public String getBillingState() {
        return billingState;
    }

    public void setBillingState(String billingState) {
        this.billingState = billingState;
    }

    public String getBillingCountry() {
        return billingCountry;
    }

    public void setBillingCountry(String billingCountry) {
        this.billingCountry = billingCountry;
    }
}
