package bhg.sucks.model;

import androidx.annotation.NonNull;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
//@ToString(includeFieldNames = false)
@EqualsAndHashCode
public class SkillContainer {

    private Skill skill;
    private int quality; // Either 1 (LOW), 6 (MIDDLE), 11 (TOP) or 1 (LOW), 3 (MIDDLE), 6 (TOP) or custom (some LEGENDARY items)
    private int level; // 1-10

    public int getBonus() {
        return quality + level - 1;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s (%s/%s)",
                skill,
                quality,
                level);
    }
}
