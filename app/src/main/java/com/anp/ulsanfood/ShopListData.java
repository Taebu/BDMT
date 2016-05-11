
package com.anp.ulsanfood;

/**
 * @author Jung-Hum Cho
 */

public class ShopListData {

    private String reviewCount;

    private String reviewRating;

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(String reviewRating) {
        this.reviewRating = reviewRating;
    }

    public String getCallcnt() {
        return callcnt;
    }

    public void setCallcnt(String callcnt) {
        this.callcnt = callcnt;
    }

    public int getSeparatorType() {
        return separatorType;
    }

    public void setSeparatorType(int separatorType) {
        this.separatorType = separatorType;
    }

    private int separatorType;

    private String img1;

    private String img2;

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    private String callcnt;

    private String name; // 업체 이름

    private String type; // 업체 종류

    private String seq; // 업체 번호

    public String getStoreCode() {
        return bizCode.concat("_"+seq);
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    private String bizCode;

    private String pay; // 바로결제 여부

    private String pre_pay; // 캐시큐가맹점

    private String iscoupon; // 쿠폰북 지급 여부

    private String thm; // 섬네일 이미지 경로

    private String time1; // 오픈

    private String time2; // 마감

    private String minpay; // 최소 결제 금액

    private String address; // 주소

    private String delivery_comment; // 메모

    private String tel; // 전화번호

    private String distance; // 거리

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    private boolean isOpen;



    public ShopListData() {

    }

    public ShopListData(String name, String type, String seq) {
        this.setName(name);
        this.setType(type);
        this.setSeq(seq);
    }

    public ShopListData(String name, String type, String seq, String thm) {
        this.setName(name);
        this.setType(type);
        this.setSeq(seq);
        this.setThm(thm);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the seq
     */
    public String getSeq() {
        return seq;
    }

    /**
     * @param seq the seq to set
     */
    public void setSeq(String seq) {
        this.seq = seq;
    }

    /**
     * @return the pay
     */
    public String getPay() {
        return pay;
    }

    /**
     * @param pay the pay to set
     */
    public void setPay(String pay) {
        this.pay = pay;
    }

    /**
     * @return the pre_pay
     */
    public String getPre_pay() {
        return pre_pay;
    }

    /**
     * @param pre_pay the pre_pay to set
     */
    public void setPre_pay(String pre_pay) {
        this.pre_pay = pre_pay;
    }

    /**
     * @return the iscoupon
     */
    public String getIscoupon() {
        return iscoupon;
    }

    /**
     * @param iscoupon the iscoupon to set
     */
    public void setIscoupon(String iscoupon) {
        this.iscoupon = iscoupon;
    }

    /**
     * @return the thm
     */
    public String getThm() {
        return thm;
    }

    /**
     * @param thm the thm to set
     */
    public void setThm(String thm) {
        this.thm = thm;
    }

    /**
     * @return the time1
     */
    public String getTime1() {
        return time1;
    }

    /**
     * @param time1 the time1 to set
     */
    public void setTime1(String time1) {
        this.time1 = time1;
    }

    /**
     * @return the time2
     */
    public String getTime2() {
        return time2;
    }

    /**
     * @param time2 the time2 to set
     */
    public void setTime2(String time2) {
        this.time2 = time2;
    }

    /**
     * @return the minpay
     */
    public String getMinpay() {
        return minpay;
    }

    /**
     * @param minpay the minpay to set
     */
    public void setMinpay(String minpay) {
        this.minpay = minpay;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the delivery_comment
     */
    public String getDelivery_comment() {
        return delivery_comment;
    }

    /**
     * @param delivery_comment the delivery_comment to set
     */
    public void setDelivery_comment(String delivery_comment) {
        this.delivery_comment = delivery_comment;
    }

    /**
     * @return the tel
     */
    public String getTel() {
        return tel;
    }

    /**
     * @param tel the tel to set
     */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /**
     * @return the distance
     */
    public String getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String makeURL() {
        // return "http://nuevacasa.cafe24.com/m6/images/"+thm;
        return "http://cashq.co.kr/adm/upload/" + getThm();

    }

}
