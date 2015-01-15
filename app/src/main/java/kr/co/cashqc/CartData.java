
package kr.co.cashqc;

import java.io.Serializable;

/**
 * @author Jung-Hum Cho Created by anp on 14. 12. 30..
 */

public class CartData implements Serializable {

    private int ea = 1;

    private int price;

    private String orderCode;

    private String menuName;

    private String menuCode;

    public void plusEa() {
        ea++;
    }

    public void minusEa() {
        if (ea > 1)
            ea--;
    }

    public int getEa() {
        return ea;
    }

    public void setEa(int ea) {
        this.ea = ea;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }
}
