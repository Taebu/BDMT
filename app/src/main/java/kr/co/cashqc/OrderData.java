
package kr.co.cashqc;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 15..
 */
public class OrderData implements Serializable {

    private String payType;

    private String shopName;

    private String shopCode;

    private String shopPhone;

    private String shopVPhone;

    private String zipCode;

    private String address1;

    private String address2;

    private String userPhone;

    private String comment;

    private int total;

    private ArrayList<CartData> menu;

    public String getShopVPhone() {
        return shopVPhone;
    }

    public void setShopVPhone(String shopVPhone) {
        this.shopVPhone = shopVPhone;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public int getTotal() {
        total = 0;
        for (CartData data : menu) {
            total += data.getEa() * data.getPrice();
        }
        return total;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ArrayList<CartData> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<CartData> menu) {
        this.menu = menu;
    }
}
