package com.example.libshop.Model;

public class CategoryModel {
    private String CategoryUID = "NO";
    private String CategoryName = "NO";
    private String CategoryPhotoUrl= "NO";
    private String CategoryBio= "NO";
    private String CategoryCreator= "NO";
    private long CategoryiViews= 0;
    private long CategoryiPriority= 0;

    public CategoryModel() {
    }

    public CategoryModel(String categoryUID, String categoryName, String categoryPhotoUrl, String categoryBio, String categoryCreator, long categoryiViews, long categoryiPriority) {
        CategoryUID = categoryUID;
        CategoryName = categoryName;
        CategoryPhotoUrl = categoryPhotoUrl;
        CategoryBio = categoryBio;
        CategoryCreator = categoryCreator;
        CategoryiViews = categoryiViews;
        CategoryiPriority = categoryiPriority;
    }

    public String getCategoryUID() {
        return CategoryUID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public String getCategoryPhotoUrl() {
        return CategoryPhotoUrl;
    }

    public String getCategoryBio() {
        return CategoryBio;
    }

    public String getCategoryCreator() {
        return CategoryCreator;
    }

    public long getCategoryiViews() {
        return CategoryiViews;
    }

    public long getCategoryiPriority() {
        return CategoryiPriority;
    }
}
