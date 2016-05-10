package com.wisepartners.dtalk;

import java.util.ArrayList;

/**
 * @author Jung-Hum Cho
 * Created by anp on 15. 4. 10..
 */
public class BoardData {
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String phone;
    private String name;
    private String datetime;
    private String subject;
    private String content;
    private ArrayList<BoardData> reply;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<BoardData> getReply() {
        return reply;
    }

    public void setReply(ArrayList<BoardData> reply) {
        this.reply = reply;
    }
}
