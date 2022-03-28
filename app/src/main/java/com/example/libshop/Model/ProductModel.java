package com.example.libshop.Model;

public class ProductModel {

    private String ProductUID = "NO";
    private String ProductName = "NO";
    private String ProductPhotoUrl = "NO";
    private String ProductBio = "NO";
    private String ProductCreator = "NO";

    private long ProductiViews = 0;
    private long ProductiPriority = 0;
    private long ProductiRating = 0;
    private long ProductiOrders = 0;
    private long ProductiPrice = 0;
    private long ProductiDiscount = 0;
    private String ProductExtra =  "NO";;
    private String ProductSize  = "NO";;
    private String ProductCategory  = "NO";;

    public ProductModel() {
    }

    public ProductModel(String productUID, String productName, String productPhotoUrl, String productBio, String productCreator, long productiViews, long productiPriority, long productiRating, long productiOrders, long productiPrice, long productiDiscount, String productExtra, String productSize, String productCategory) {
        ProductUID = productUID;
        ProductName = productName;
        ProductPhotoUrl = productPhotoUrl;
        ProductBio = productBio;
        ProductCreator = productCreator;
        ProductiViews = productiViews;
        ProductiPriority = productiPriority;
        ProductiRating = productiRating;
        ProductiOrders = productiOrders;
        ProductiPrice = productiPrice;
        ProductiDiscount = productiDiscount;
        ProductExtra = productExtra;
        ProductSize = productSize;
        ProductCategory = productCategory;
    }

    public String getProductUID() {
        return ProductUID;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getProductPhotoUrl() {
        return ProductPhotoUrl;
    }

    public String getProductBio() {
        return ProductBio;
    }

    public String getProductCreator() {
        return ProductCreator;
    }

    public long getProductiViews() {
        return ProductiViews;
    }

    public long getProductiPriority() {
        return ProductiPriority;
    }

    public long getProductiRating() {
        return ProductiRating;
    }

    public long getProductiOrders() {
        return ProductiOrders;
    }

    public long getProductiPrice() {
        return ProductiPrice;
    }

    public long getProductiDiscount() {
        return ProductiDiscount;
    }

    public String getProductExtra() {
        return ProductExtra;
    }

    public String getProductSize() {
        return ProductSize;
    }

    public String getProductCategory() {
        return ProductCategory;
    }

    public void setProductUID(String productUID) {
        ProductUID = productUID;
    }
}
