package com.example.libshop.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class CartModel {

    //String dsProductUID = ProductModel.getProductUID();

        private String ProductUID   = "NO";
        private String ProductName  = "NO";
        private String ProductPHOTO = "NO";

        private String ProductColor = "NO";
        private String ProductSize  = "NO";
        private long ProductiPrice  = 0;
        private long ProductQuantity = 0;
    
        private @ServerTimestamp Date ProductOrderTime;

    public CartModel() {
    }

    public CartModel(String ProductUID, String ProductName, String ProductPHOTO, String ProductColor, String ProductSize, long ProductiPrice,long ProductQuantity, Date ProductOrderTime) {
        this.ProductUID = ProductUID;
        this.ProductName = ProductName;
        this.ProductPHOTO = ProductPHOTO;
        this.ProductColor = ProductColor;
        this.ProductSize = ProductSize;
        this.ProductiPrice = ProductiPrice;
        this.ProductQuantity = ProductQuantity;
        this.ProductOrderTime = ProductOrderTime;
    }

    public long getProductQuantity() {
        return ProductQuantity;
    }

    public String getProductUID() {
        return ProductUID;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getProductPHOTO() {
        return ProductPHOTO;
    }

    public String getProductColor() {
        return ProductColor;
    }

    public String getProductSize() {
        return ProductSize;
    }

    public long getProductiPrice() {
        return ProductiPrice;
    }

    public Date getProductOrderTime() {
        return ProductOrderTime;
    }
}
