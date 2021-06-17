package bhg.sucks;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import bhg.sucks.model.Skill;
import bhg.sucks.model.SkillContainer;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class SkillContainerMatcher {

    public static Matcher<SkillContainer> skill(Matcher<? super Skill> matcher) {
        return new FeatureMatcher<SkillContainer, Skill>(matcher, "SkillContainer, whose skill", "skill") {
            @Override
            protected Skill featureValueOf(SkillContainer actual) {
                return actual.getSkill();
            }
        };
    }

    public static Matcher<SkillContainer> quality(Matcher<? super Integer> matcher) {
        return new FeatureMatcher<SkillContainer, Integer>(matcher, "SkillContainer, whose quality", "quality") {
            @Override
            protected Integer featureValueOf(SkillContainer actual) {
                return actual.getQuality();
            }
        };
    }

    public static Matcher<SkillContainer> level(Matcher<? super Integer> matcher) {
        return new FeatureMatcher<SkillContainer, Integer>(matcher, "SkillContainer, whose level", "level") {
            @Override
            protected Integer featureValueOf(SkillContainer actual) {
                return actual.getLevel();
            }
        };
    }

    public static Matcher<SkillContainer> isSame(SkillContainer skillContainer) {
        return allOf(
                skill(is(skillContainer.getSkill())),
                quality(is(skillContainer.getQuality())),
                level(is(skillContainer.getLevel()))
        );
    }

    /**
     * only immutable attributes are considered
     */
    public static Matcher<SkillContainer> isSameImmutable(SkillContainer skillContainer) {
        return allOf(
                skill(is(skillContainer.getSkill())),
                quality(is(skillContainer.getQuality()))
        );
    }

}
