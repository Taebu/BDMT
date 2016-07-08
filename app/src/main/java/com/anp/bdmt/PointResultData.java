
package com.anp.bdmt;

/**
 * @author Jung-Hum Cho Created by anp on 15. 1. 2..
 */
public class PointResultData {

    /*- point_status
    1 사용가능
    11 사용가능(확인됨)
    2 신청중
    12 다른 주문 : hh:mm *** 주문
    3 지급완료

    16 미주문
    15 금액미달 : xx,000원 주문
    17 기타 : etc....
    99 전산오류
    10 삭제*/

    public static final int AVAILABLE = 1;

    public static final int CHECKING = 2;

    public static final int PAY_OK = 3;

    public static final int DELETE = 10;

    public static final int AVAILABLE_CHECKED = 11;

    public static final int ANOTHER_ORDER = 12;

    public static final int NOT_ENOUGH = 15;

    public static final int NOT_ORDER = 16;

    public static final int ETC_COMMENT = 17;

    public static final int SYSTEM_ERROR = 99;

    private String name;

    private String date;

    private String comment;

    private int pointStatus;

    private String minimumAmount;

    private String notEnough;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPointStatus() {
        return pointStatus;
    }

    public void setPointStatus(int pointStatus) {
        this.pointStatus = pointStatus;
    }

    public String getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(String minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getNotEnough() {
        return notEnough;
    }

    public void setNotEnough(String notEnough) {
        this.notEnough = notEnough;
    }
}
