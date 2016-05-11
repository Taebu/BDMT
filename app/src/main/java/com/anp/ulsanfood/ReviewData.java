package com.anp.ulsanfood;

/**
 * @author Jung-Hum Cho
 * Created by anp on 15. 4. 2..
 */
public class ReviewData {

    private String nick;
    private String phone;
    private String content;
    private String insdate;
    private String update;
    private int rating;
    private String tokenId;

   private String seq;
   private String stSeq;

    public String getStSeq() {
        return stSeq;
    }

    public void setStSeq(String stSeq) {
        this.stSeq = stSeq;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getInsdate() {
        return insdate;
    }

    public void setInsdate(String insdate) {
        this.insdate = insdate;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
