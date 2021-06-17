package bhg.sucks.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Artifact {

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private Category category;
    private List<SkillContainer> skills;

}
