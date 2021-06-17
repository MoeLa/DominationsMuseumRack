package bhg.sucks;

import android.content.Context;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import bhg.sucks.dao.AppDatabase;
import bhg.sucks.dao.ArtifactDao;
import bhg.sucks.model.Artifact;
import bhg.sucks.model.Category;
import bhg.sucks.model.Skill;
import bhg.sucks.model.SkillContainer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestArtifactDao {

    private final Artifact a1 = Artifact.builder()
            .name("A Name")
            .category(Category.Weapon)
            .skills(List.of(SkillContainer.builder()
                    .skill(Skill.UNDEFINED)
                    .level(0)
                    .quality(6)
                    .build()))
            .build();
    private final Artifact a2 = Artifact.builder()
            .name("Another Name")
            .category(Category.Armor)
            .skills(List.of(SkillContainer.builder()
                    .skill(Skill.AirDefenseDamage)
                    .level(3)
                    .quality(11)
                    .build()))
            .build();

    private AppDatabase db;
    private ArtifactDao artifactDao;

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        this.db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        this.artifactDao = db.artifactDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testExists() {
        // Prepare
        artifactDao.insert(a1);
        artifactDao.insert(a2);

        // Execute
        Boolean actual1 = artifactDao.exists(a1.getName(), a1.getCategory(), a1.getSkills());
        Boolean actual2 = artifactDao.exists(a2.getName(), a2.getCategory(), a2.getSkills());

        // Check
        assertTrue("Artifact not in db, although it should be there", actual1);
        assertTrue("Artifact not in db, although it should be there", actual2);
    }

    @Test
    public void testNotExists() {
        // Prepare - Insert some dummy data
        artifactDao.insert(Artifact.builder()
                .name("adsada")
                .category(Category.UNDEFINED)
                .skills(List.of())
                .build());

        // Execute
        Boolean actual1 = artifactDao.exists(a1.getName(), a1.getCategory(), a1.getSkills());
        Boolean actual2 = artifactDao.exists(a2.getName(), a2.getCategory(), a2.getSkills());

        // Check
        assertFalse("Artifact in db, although it shouldn't be there", actual1);
        assertFalse("Artifact in db, although it shouldn't be there", actual2);
    }
}