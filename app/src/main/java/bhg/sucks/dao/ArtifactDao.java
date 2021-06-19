package bhg.sucks.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import bhg.sucks.model.Artifact;
import bhg.sucks.model.Category;
import bhg.sucks.model.SkillContainer;

@Dao
public interface ArtifactDao {

    @Insert
    long insert(Artifact artifact);

    @Query("DELETE FROM artifact")
    void deleteAll();

    @Query("SELECT * FROM artifact")
    LiveData<List<Artifact>> loadAllArtifacts();

    @Query("SELECT count(*) > 0 FROM artifact WHERE name LIKE :name AND category = :category AND skills LIKE :skills")
    Boolean exists(String name, Category category, List<SkillContainer> skills);

}
