package com.ecommerce.project.model;

public class Category {
    private Long categoryId;
    private String categoryName;

    public Category(Long id, String categoryName) {
        this.categoryId = id;
        this.categoryName = categoryName;
    }

    public Long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
