package bhg.sucks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import bhg.sucks.model.Artifact;
import bhg.sucks.model.Category;
import bhg.sucks.model.Skill;
import bhg.sucks.model.SkillContainer;
import bhg.sucks.ocr.OcrException;
import bhg.sucks.ocr.OcrHelper;

import static org.junit.Assert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestOcrHelper {

    private Context context;
    private OcrHelper ocrHelper;

    @Before
    public void setup() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        this.ocrHelper = new OcrHelper(context);
    }

    @Test
    public void testScreenshotRack1() {
        // Prepare
        Bitmap b = bitmapFrom(R.drawable.screenshot_rack10);

        // Execute
        Artifact actual = ocrHelper.analyze(b);

        // Check
        Artifact expected = Artifact.builder()
                .name("Mangbetu-Messer")
                .category(Category.Weapon)
                .skills(List.of(
                        SkillContainer.builder()
                                .skill(Skill.MortarTroopDamage)
                                .quality(11)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.GeneralsDamage)
                                .quality(1)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.HeavyTankDamage)
                                .quality(1)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.ParatroopersFromTransportsDamage)
                                .quality(1)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.ParatroopersFromTransportsDamage)
                                .quality(11)
                                .level(1)
                                .build()))
                .build();
        assertThat(actual, ArtifactMatcher.isSame(expected));
    }

    @Test
    public void testScreenshotRack2() {
        // Prepare
        Bitmap b = bitmapFrom(R.drawable.screenshot_rack2);

        // Execute
        Artifact actual = ocrHelper.analyze(b);

        // Check
        Artifact expected = Artifact.builder()
                .name("Truppensanitätsausrüstung")
                .category(Category.WarEquipment)
                .skills(List.of(
                        SkillContainer.builder()
                                .skill(Skill.WarDefenderHitpoints)
                                .quality(6)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.WarAllEnemyDefensiveTowersDamage)
                                .quality(6)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.WarAllEnemyDefensiveTowersDamage)
                                .quality(3)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.WarEnemyAirDefenseDamage)
                                .quality(3)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.WarAirDefenseDamage)
                                .quality(3)
                                .level(1)
                                .build()))
                .build();
        assertThat(actual, ArtifactMatcher.isSame(expected));
    }

    @Test
    public void testScreenshotRack3() {
        // Prepare
        Bitmap b = bitmapFrom(R.drawable.screenshot_rack3);

        // Execute
        Artifact actual = ocrHelper.analyze(b);

        // Check
        Artifact expected = Artifact.builder()
                .name("Sphinx von Taharqa")
                .category(Category.Legendary)
                .skills(List.of(
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(6)
                                .level(2)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(6)
                                .level(3)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(1)
                                .level(3)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(1)
                                .level(4)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(1)
                                .level(2)
                                .build()))
                .build();
        assertThat(actual, ArtifactMatcher.isSame(expected));
    }

    @Test
    public void testScreenshotRack4() {
        // Prepare
        Bitmap b = bitmapFrom(R.drawable.screenshot_rack4);

        // Execute
        Artifact actual = ocrHelper.analyze(b);

        // Check
        Artifact expected = Artifact.builder()
                .name("Señora de Caos Moche-Ohrschmuck")
                .category(Category.Jewelry)
                .skills(List.of(
                        SkillContainer.builder()
                                .skill(Skill.NationalTradeGoodLootChance)
                                .quality(11)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.EnemyMissileSiloDamage)
                                .quality(6)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.EnemyAirDefenseDamage)
                                .quality(1)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.EnemyAirDefenseDamage)
                                .quality(1)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.OilLooted)
                                .quality(1)
                                .level(1)
                                .build()))
                .build();
        assertThat(actual, ArtifactMatcher.isSame(expected));
    }

    @Test
    public void testScreenshotRack5() {
        // Prepare
        Bitmap b = bitmapFrom(R.drawable.screenshot_rack5);

        // Execute
        Artifact actual = ocrHelper.analyze(b);

        // Check
        Artifact expected = Artifact.builder()
                .name("Hello Girls' Grabentelefon")
                .category(Category.WarEquipment)
                .skills(List.of(
                        SkillContainer.builder()
                                .skill(Skill.WarAllDefensiveTowersHitpoints)
                                .quality(1)
                                .level(3)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.WarDefenderHitpoints)
                                .quality(3)
                                .level(4)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.WarAllEnemyDefensiveTowersDamage)
                                .quality(6)
                                .level(6)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.WarAllEnemyDefensiveTowersDamage)
                                .quality(3)
                                .level(5)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.WarMissileSiloDamage)
                                .quality(6)
                                .level(2)
                                .build()))
                .build();
        assertThat(actual, ArtifactMatcher.isSame(expected));
    }

    @Test
    public void testScreenshotRack6() {
        // Prepare
        Bitmap b = bitmapFrom(R.drawable.screenshot_rack6);

        // Execute
        Artifact actual = ocrHelper.analyze(b);

        // Check
        Artifact expected = Artifact.builder()
                .name("Stein von Rosette")
                .category(Category.Legendary)
                .skills(List.of(
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(11)
                                .level(2)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(11)
                                .level(2)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(6)
                                .level(2)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(6)
                                .level(2)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(6)
                                .level(2)
                                .build()))
                .build();
        assertThat(actual, ArtifactMatcher.isSame(expected));
    }

    @Test
    public void testScreenshotRack7() {
        // Prepare
        Bitmap b = bitmapFrom(R.drawable.screenshot_rack7);

        // Execute
        Artifact actual = ocrHelper.analyze(b);

        // Check
        Artifact expected = Artifact.builder()
                .name("Tizona-Schwert")
                .category(Category.WarLegedary)
                .skills(List.of(
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(6)
                                .level(2)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(6)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(3)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(6)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.UNDEFINED)
                                .quality(3)
                                .level(1)
                                .build()))
                .build();
        assertThat(actual, ArtifactMatcher.isSame(expected));
    }

    @Test
    public void testScreenshotRack8() {
        // Prepare
        Bitmap b = bitmapFrom(R.drawable.screenshot_rack8);

        // Execute
        Artifact actual = ocrHelper.analyze(b);

        // Check
        Artifact expected = Artifact.builder()
                .name("Kusarigama")
                .category(Category.Weapon)
                .skills(List.of(
                        SkillContainer.builder()
                                .skill(Skill.FighterDamage)
                                .quality(6)
                                .level(10)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.FighterDamage)
                                .quality(11)
                                .level(10)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.BazookaHitpoints)
                                .quality(1)
                                .level(9)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.BazookaHitpoints)
                                .quality(6)
                                .level(9)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.HeavyCavalryHitpoints)
                                .quality(1)
                                .level(1)
                                .build()))
                .build();
        assertThat(actual, ArtifactMatcher.isSame(expected));
    }

    @Test
    public void testScreenshotRack11() {
        // Prepare
        Bitmap b = bitmapFrom(R.drawable.screenshot_rack11);

        // Execute
        Artifact actual = ocrHelper.analyze(b);

        // Check
        Artifact expected = Artifact.builder()
                .name("Pugio-Dolch")
                .category(Category.Weapon)
                .skills(List.of(
                        SkillContainer.builder()
                                .skill(Skill.SupplyVehicleHitpoints)
                                .quality(6)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.FighterHitpoints)
                                .quality(1)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.EnemyDefenderSpawnTime)
                                .quality(11)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.HeavyTankDamage)
                                .quality(1)
                                .level(1)
                                .build(),
                        SkillContainer.builder()
                                .skill(Skill.FighterHitpoints)
                                .quality(1)
                                .level(1)
                                .build()))
                .build();
        assertThat(actual, ArtifactMatcher.isSame(expected));
    }

    @Test(expected = OcrException.class)
    public void testNoArtifact() {
        // Prepare
        Bitmap b = bitmapFrom(R.drawable.red_circle);

        // Execute
        ocrHelper.analyze(b);

        // Check - Done via annotation
    }

    private Bitmap bitmapFrom(int redId) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;

        return BitmapFactory.decodeResource(context.getResources(), redId, opts);
    }

}