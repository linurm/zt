package org.greenrobot.greendao.example;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by Administrator on 2017/5/5.
 */
@Entity(indexes = {
        @Index(value = "last7minites, date DESC", unique = true)
})
public class MSC {
    @Id
    private Long id;

    @NotNull
    private String last7minites;
    private String todayminites;
    private String date;
@Generated(hash = 769511464)
public MSC(Long id, @NotNull String last7minites, String todayminites,
        String date) {
    this.id = id;
    this.last7minites = last7minites;
    this.todayminites = todayminites;
    this.date = date;
}
@Generated(hash = 1852795046)
public MSC() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getLast7minites() {
    return this.last7minites;
}
public void setLast7minites(String last7minites) {
    this.last7minites = last7minites;
}
public String getTodayminites() {
    return this.todayminites;
}
public void setTodayminites(String todayminites) {
    this.todayminites = todayminites;
}
public String getDate() {
    return this.date;
}
public void setDate(String date) {
    this.date = date;
}
}
