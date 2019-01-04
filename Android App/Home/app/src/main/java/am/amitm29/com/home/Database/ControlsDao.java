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
public interface ControlsDao {
    @Query("SELECT * FROM `custom controls`")
    LiveData<List<CustomControls>> loadAllControls();

    @Query("SELECT * FROM `custom controls` WHERE _ID = :id")
    LiveData<CustomControls> loadControlById(long id);

    @Insert
    void insertControl(CustomControls customControl);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateControl(CustomControls customControl);

    @Delete
    void deleteControl(CustomControls customControl);
}
