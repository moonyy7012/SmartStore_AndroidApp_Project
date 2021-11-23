package com.ssafy.smartstore.model.dto;

public class Favorite {
    private int id;
    private String userId;
    private int productId;

    public Favorite(int id, String userId, int productId) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
    }

    public Favorite(String userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }

    Favorite() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

}
