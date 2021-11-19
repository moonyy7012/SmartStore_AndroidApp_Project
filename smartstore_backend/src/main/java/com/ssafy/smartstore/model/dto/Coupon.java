package com.ssafy.smartstore.model.dto;


public class Coupon {
    private Integer id;
    private String name;
    private String type;

    public Coupon(Integer id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Coupon(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Coupon() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Coupon [id = " + id + ", name = " + name + ", type = " + type + "]";
    }
}
