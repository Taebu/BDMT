
package kr.co.cashqc;

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

    private String price;

    private String code;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
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
