package com.zfenlly.wb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "WB".
 */
public class WB {

    private Long id;
    /** Not-null value. */
    private String totalscore;
    private String nextscore;
    private String date;

    public WB() {
    }

    public WB(Long id) {
        this.id = id;
    }

    public WB(Long id, String totalscore, String nextscore, String date) {
        this.id = id;
        this.totalscore = totalscore;
        this.nextscore = nextscore;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getTotalscore() {
        return totalscore;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTotalscore(String totalscore) {
        this.totalscore = totalscore;
    }

    public String getNextscore() {
        return nextscore;
    }

    public void setNextscore(String nextscore) {
        this.nextscore = nextscore;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
