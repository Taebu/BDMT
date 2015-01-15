
package kr.co.cashqc;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho Created by anp on 14. 12. 18..
 */
public class ShopMenuData {

    private String shopCode;

    private String shopName;

    private ArrayList<MenuData> menu;

    public ArrayList<MenuData> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<MenuData> menu) {
        this.menu = menu;
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
}
