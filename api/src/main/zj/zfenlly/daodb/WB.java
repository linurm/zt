package zj.zfenlly.daodb;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Created by Administrator on 2017/5/5.
 */
@Entity(indexes = {
        @Index(value = "totalscore, date DESC", unique = true)
})
public class WB {
    @Id
    private Long id;

    @NotNull
    private String totalscore;
    private String nextscore;
    private String date;

    @Generated(hash = 1786608188)
    public WB(Long id, @NotNull String totalscore, String nextscore, String date) {
        this.id = id;
        this.totalscore = totalscore;
        this.nextscore = nextscore;
        this.date = date;
    }

    @Generated(hash = 50959290)
    public WB() {
    }

    @Keep
    public String toString() {
        return date + " " + nextscore + " " + totalscore;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTotalscore() {
        return this.totalscore;
    }

    public void setTotalscore(String totalscore) {
        this.totalscore = totalscore;
    }

    public String getNextscore() {
        return this.nextscore;
    }

    public void setNextscore(String nextscore) {
        this.nextscore = nextscore;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
