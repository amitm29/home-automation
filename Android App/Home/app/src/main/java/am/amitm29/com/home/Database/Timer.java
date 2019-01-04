package am.amitm29.com.home.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "timers")
public class Timer {
    @Ignore
    public static final int AC_ON = 1;
    @Ignore
    public static final int AC_OFF = 0;





    @PrimaryKey
    private char r_ID;
    @ColumnInfo(name = "title")
    private String titl;
    private int action;
    private long durationInSec;
    private long finalTimeinMillis;

    public Timer(char r_ID, String titl, int action, long durationInSec, long finalTimeinMillis) {
        this.r_ID = r_ID;
        this.titl = titl;
        this.action = action;
        this.durationInSec = durationInSec;
        this.finalTimeinMillis = finalTimeinMillis;
    }

    public char getR_ID() {
        return r_ID;
    }

    public void setR_ID(char r_ID) {
        this.r_ID = r_ID;
    }

    public String getTitl() {
        return titl;
    }

    public void setTitl(String titl) {
        this.titl = titl;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public long getDurationInSec() {
        return durationInSec;
    }

    public void setDurationInSec(long durationInSec) {
        this.durationInSec = durationInSec;
    }

    public long getFinalTimeinMillis() {
        return finalTimeinMillis;
    }

    public void setFinalTimeinMillis(long finalTimeinMillis) {
        this.finalTimeinMillis = finalTimeinMillis;
    }
}
