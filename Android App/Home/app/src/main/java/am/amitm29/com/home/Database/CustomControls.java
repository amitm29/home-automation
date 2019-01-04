package am.amitm29.com.home.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "custom controls")
public class CustomControls {
    @PrimaryKey(autoGenerate = true)
    private long _ID;

    public CustomControls(long _ID, String titl, String note, String output, String input, boolean state, long timeInMillis) {
        this._ID = _ID;
        this.titl = titl;
        this.note = note;
        this.output = output;
        this.input = input;
        this.state = state;
        this.timeInMillis = timeInMillis;
    }

    @Ignore
    public CustomControls(String titl, String note, String output, String input, boolean state, long timeInMillis) {

        this.titl = titl;
        this.note = note;
        this.output = output;
        this.input = input;
        this.state = state;
        this.timeInMillis = timeInMillis;
    }

    @ColumnInfo(name = "title")

    private String titl;

    private String note;

    private String output;

    private String input;

    private long timeInMillis;

    private boolean state;

    public void set_ID(int _ID) {
        this._ID = _ID;
    }

    public void setTitl(String titl) {
        this.titl = titl;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public long get_ID() {
        return _ID;
    }

    public String getTitl() {
        return titl;
    }

    public String getNote() {
        return note;
    }

    public String getOutput() {
        return output;
    }

    public String getInput() {
        return input;
    }

    public boolean getState() {
        return state;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }
}
