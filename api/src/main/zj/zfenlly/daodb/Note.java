package zj.zfenlly.daodb;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * Entity mapped to table "NOTE".
 */
@Entity(indexes = {
    @Index(value = "stockid, date DESC", unique = true)
})
public class Note {

    @Id
    private Long id;

    @NotNull
    private String stockid;
    private String content;
    private String date;
    private String date1;

    @Generated(hash = 768542178)
    public Note(Long id, @NotNull String stockid, String content, String date,
            String date1) {
        this.id = id;
        this.stockid = stockid;
        this.content = content;
        this.date = date;
        this.date1 = date1;
    }
    @Generated(hash = 1272611929)
    public Note() {
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStockid() {
        return this.stockid;
    }
    public void setStockid(String stockid) {
        this.stockid = stockid;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDate1() {
        return this.date1;
    }
    public void setDate1(String date1) {
        this.date1 = date1;
    }


}
