package com.example.libshop.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class OrderModel {

    private @ServerTimestamp Date diTime ;
    private String order_UID = "NO";
    private String uid_cart = "NO";
    private String uid_buyer = "NO";
    private String uid_shop_owner = "NO";
    private String dsNote = "NO";
    private String dsDeliveryHour = "NO";
    private String dsPaymentStatus = "NO";
    private String dsCompleteLevel = "NO";
    private long diTotal_Money = 0;

    public OrderModel() {
    }

    public OrderModel(Date diTime, String order_UID, String uid_cart, String uid_buyer, String uid_shop_owner, String dsNote, String dsDeliveryHour, String dsPaymentStatus, String dsCompleteLevel, long diTotal_Money) {
        this.diTime = diTime;
        this.order_UID = order_UID;
        this.uid_cart = uid_cart;
        this.uid_buyer = uid_buyer;
        this.uid_shop_owner = uid_shop_owner;
        this.dsNote = dsNote;
        this.dsDeliveryHour = dsDeliveryHour;
        this.dsPaymentStatus = dsPaymentStatus;
        this.dsCompleteLevel = dsCompleteLevel;
        this.diTotal_Money = diTotal_Money;
    }

    public Date getDiTime() {
        return diTime;
    }

    public String getOrder_UID() {
        return order_UID;
    }

    public String getUid_cart() {
        return uid_cart;
    }

    public String getUid_buyer() {
        return uid_buyer;
    }

    public String getUid_shop_owner() {
        return uid_shop_owner;
    }

    public String getDsNote() {
        return dsNote;
    }

    public String getDsDeliveryHour() {
        return dsDeliveryHour;
    }

    public String getDsPaymentStatus() {
        return dsPaymentStatus;
    }

    public String getDsCompleteLevel() {
        return dsCompleteLevel;
    }

    public long getDiTotal_Money() {
        return diTotal_Money;
    }
}
