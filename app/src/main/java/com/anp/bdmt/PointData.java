package com.anp.bdmt;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Jung-Hum Cho
 *         Created by anp on 14. 11. 19..
 */
public class PointData {

    private String[] includeCodes;

    public String[] getIncludeCodes() {
        return includeCodes;
    }

    public void setIncludeCodes(String[] includeCodes) {
        this.includeCodes = Arrays.copyOf(includeCodes, includeCodes.length);
    }

    private int pointType;

    public int getPointType() {
        return pointType;
    }

    public void setPointType(int pointType) {
        this.pointType = pointType;
    }

    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String pointRuleContent;

    public String getPointRuleContent() {
        return pointRuleContent;
    }

    public void setPointRuleContent(String pointRuleContent) {
        this.pointRuleContent = pointRuleContent;
    }

    private ArrayList<PointRuleData> pointRuleList;

    public ArrayList<PointRuleData> getPointRuleList() {
        return pointRuleList;
    }

    public void setPointRuleList(ArrayList<PointRuleData> pointRuleList) {
        this.pointRuleList = pointRuleList;
    }

    private String borough;

    public String getBorough() {
        return borough;
    }

    public void setBorough(String borough) {
        this.borough = borough;
    }

    private boolean isFreePoint;

    public boolean isFreePoint() {
        return isFreePoint;
    }

    public void setIsFreePoint(boolean isFreePoint) {
        this.isFreePoint = isFreePoint;
    }

    private String storeSeq;

    public String getStoreSeq() {
        return storeSeq;
    }

    public void setStoreSeq(String storeSeq) {
        this.storeSeq = storeSeq;
    }

    private String grade;

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    private String seq;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    private String type;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private int position;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    private boolean checked;

    public int getCheckedCount() {
        return checkedCount;
    }

    public void setCheckedCount(int checkedCount) {
        this.checkedCount = checkedCount;
    }

    private int checkedCount;

    private String holder;

    private String point;

    private String biz_code;

    private String bank;

    private String accNum;

    private String date;

    private String name;

    private String accrue;

    private String status;

    private String deadline;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getEventcode() {
        return eventcode;
    }

    public void setEventcode(String eventcode) {
        this.eventcode = eventcode;
    }

    public String getPoint_seq() {
        return point_seq;
    }

    public void setPoint_seq(String point_seq) {
        this.point_seq = point_seq;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String userid, eventcode, point_seq, phone;

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getBiz_code() {
        return biz_code;
    }

    public void setBiz_code(String biz_code) {
        this.biz_code = biz_code;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAccNum() {
        return accNum;
    }

    public void setAccNum(String accNum) {
        this.accNum = accNum;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccrue() {
        return accrue;
    }

    public void setAccrue(String accrue) {
        this.accrue = accrue;
    }

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

    private String comment;

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
