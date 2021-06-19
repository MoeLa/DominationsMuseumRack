package bhg.sucks.dao;


import androidx.room.TypeConverter;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.util.List;

import bhg.sucks.model.SkillContainer;

public class Converters {

    private static final Gson GSON = new Gson();

    @TypeConverter
    public static List<SkillContainer> fromSkillJson(String skillJson) {
        SkillContainer[] skills = GSON.fromJson(skillJson, SkillContainer[].class);
        return Lists.newArrayList(skills);
    }

    @TypeConverter
    public static String toSkillJson(List<SkillContainer> skill) {
        return GSON.toJson(skill);
    }

}
