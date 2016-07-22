package com.zfenlly.db;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table "MSC".
 */
public class MSC {

    private Long id;
    /**
     * Not-null value.
     */
    private String last7minites;
    private String todayminites;
    private String date;

    public MSC() {
    }

    public MSC(Long id) {
        this.id = id;
    }

    public MSC(Long id, String last7minites, String todayminites, String date) {
        this.id = id;
        this.last7minites = last7minites;
        this.todayminites = todayminites;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Not-null value.
     */
    public String getLast7minites() {
        return last7minites;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setLast7minites(String last7minites) {
        this.last7minites = last7minites;
    }

    public String getTodayminites() {
        return todayminites;
    }

    public void setTodayminites(String todayminites) {
        this.todayminites = todayminites;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String toString() {
        String s = this.date + " " + this.last7minites + " ";
        return s;
    }

}