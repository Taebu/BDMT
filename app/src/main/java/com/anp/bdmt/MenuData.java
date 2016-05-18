
package com.anp.bdmt;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 14..
 */
public class MenuData {

    private String thumb;

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    private String label;

    private String id;

    private String code;

    private int price;

    private int discountPrice;

    private int quantity;

    private int discountRate;

    private boolean isDeal;

    public boolean isDeal() {
        return isDeal;
    }

    public void setIsDeal(boolean isDeal) {
        this.isDeal = isDeal;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
    }

    private ArrayList<MenuData> child;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String parentId, String id) {

        this.code = parentId + "_" + id;
    }

    public ArrayList<MenuData> getChild() {
        return child;
    }

    public void setChild(ArrayList<MenuData> child) {
        this.child = child;
    }
}
