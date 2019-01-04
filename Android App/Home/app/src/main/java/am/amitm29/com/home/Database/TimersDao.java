package am.amitm29.com.home.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TimersDao {

    @Query("SELECT * FROM timers")
    LiveData<List<Timer>> loadAllTimers();

    @Query("SELECT * FROM timers where R_ID=:r_id")
    LiveData<Timer> loadTimerById(char r_id);

    @Insert
    void addTimer(Timer timer);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTimer(Timer timer);

    @Delete
    void deleteTimer(Timer timer);
}
