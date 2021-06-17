package bhg.sucks;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import bhg.sucks.model.Artifact;
import bhg.sucks.model.Category;
import bhg.sucks.model.SkillContainer;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class ArtifactMatcher {

    public static Matcher<Artifact> name(Matcher<? super String> matcher) {
        return new FeatureMatcher<Artifact, String>(matcher, "name", "name") {
            @Override
            protected String featureValueOf(Artifact actual) {
                return actual.getName();
            }
        };
    }

    public static Matcher<Artifact> category(Matcher<? super Category> matcher) {
        return new FeatureMatcher<Artifact, Category>(matcher, "category", "category") {
            @Override
            protected Category featureValueOf(Artifact actual) {
                return actual.getCategory();
            }
        };
    }

    public static Matcher<Artifact> skills(Matcher<Iterable<? extends SkillContainer>> matcher) {
        return new FeatureMatcher<Artifact, Iterable<? extends SkillContainer>>(matcher, "skills", "skills") {
            @Override
            protected Iterable<? extends SkillContainer> featureValueOf(Artifact actual) {
                return actual.getSkills();
            }
        };
    }

    public static Matcher<Artifact> isSame(Artifact artifact) {
        return allOf(
                name(is(artifact.getName())),
                category(is(artifact.getCategory())),
                skills(contains(artifact.getSkills().toArray(new SkillContainer[0])))
        );
    }
}
