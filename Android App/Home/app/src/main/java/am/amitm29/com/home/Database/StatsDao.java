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
public interface StatsDao {

    @Query("SELECT * FROM `stats`")
    LiveData<List<Stats>> loadAllStats();

    @Query("SELECT * FROM `stats` WHERE id = :id")
    LiveData<Stats> loadStatById(int id);

    @Insert
    void insertStats(Stats stats);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateStats(Stats stats);

    @Delete
    void deleteStats(Stats stats);

}
