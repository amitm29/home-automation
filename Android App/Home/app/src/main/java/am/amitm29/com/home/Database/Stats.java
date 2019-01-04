package am.amitm29.com.home.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Stats {

    @PrimaryKey
    private int id;

    private long millis;
    private long startTimeInMillis;
    private long s0;
    private long s1;
    private long s2;
    private long s3;
    private long s4;
    private long s5;
    private long s6;
    private long s7;
    private long s8;

    public Stats(int id, long millis, long startTimeInMillis, long s0, long s1,
                 long s2, long s3, long s4, long s5, long s6, long s7, long s8) {
        this.id = id;
        this.millis = millis;
        this.startTimeInMillis = startTimeInMillis;
        this.s0 = s0;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        this.s4 = s4;
        this.s5 = s5;
        this.s6 = s6;
        this.s7 = s7;
        this.s8 = s8;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public long getStartTimeInMillis() {
        return startTimeInMillis;
    }

    public void setStartTimeInMillis(long startTimeInMillis) {
        this.startTimeInMillis = startTimeInMillis;
    }

    public long getS0() {
        return s0;
    }

    public void setS0(long s0) {
        this.s0 = s0;
    }

    public long getS1() {
        return s1;
    }

    public void setS1(long s1) {
        this.s1 = s1;
    }

    public long getS2() {
        return s2;
    }

    public void setS2(long s2) {
        this.s2 = s2;
    }

    public long getS3() {
        return s3;
    }

    public void setS3(long s3) {
        this.s3 = s3;
    }

    public long getS4() {
        return s4;
    }

    public void setS4(long s4) {
        this.s4 = s4;
    }

    public long getS5() {
        return s5;
    }

    public void setS5(long s5) {
        this.s5 = s5;
    }

    public long getS6() {
        return s6;
    }

    public void setS6(long s6) {
        this.s6 = s6;
    }

    public long getS7() {
        return s7;
    }

    public void setS7(long s7) {
        this.s7 = s7;
    }

    public long getS8() {
        return s8;
    }

    public void setS8(long s8) {
        this.s8 = s8;
    }

}
