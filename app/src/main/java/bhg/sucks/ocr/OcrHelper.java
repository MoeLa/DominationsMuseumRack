package bhg.sucks.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import bhg.sucks.model.Artifact;
import bhg.sucks.model.Category;
import bhg.sucks.model.Skill;
import bhg.sucks.model.SkillContainer;

public class OcrHelper {

    /**
     * Finds the name out of the following strings:
     * <ul>
     * <li>Name of Artifact
     * <li>xx/50 Name of Artifact
     * <li>xx/50\nName of Artifact
     * </ul>
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("(?:\\d{1,2}\\/50(?:\\s|\\\\n))?(.+)");

    /**
     * RegEx for an artifact's skill.
     * <p>
     * Any string, that starts with an upper case letter, followed by at least nine of letter/minus/space.
     */
    private static final Pattern SKILL_PATTERN = Pattern.compile("([A-ZÄÖÜ][A-Za-zÄäÖöÜüß\\- ']{9,})");

    /**
     * RegEx for the bonus of an artifact's skill.
     * <p>
     * A string containing + or -, followed by a two-digit number and a %-sign.
     */
    private static final Pattern BONUS_PATTERN = Pattern.compile("((?:\\+|-)([0-9]{1,2})%)");

    /**
     * RegEx for the level of an artifact's skill.
     * <p>
     * A string containing a two-digit number followed by '/10'.
     */
    private static final Pattern LEVEL_PATTERN = Pattern.compile("([0-9]{1,2})\\/10");

    private final Context context;
    private final TextRecognizer textRecognizer;
    private final DiffMatchPatch diffMatchPatch;
    private final Map<String, Category> categoryLookup;
    private final Multimap<Category, Skill> skillLookup;

    public OcrHelper(Context context) {
        this.context = context;
        this.textRecognizer = new TextRecognizer.Builder(context).build();
        this.diffMatchPatch = new DiffMatchPatch();

        this.categoryLookup = Arrays.stream(Category.values())
                .collect(Collectors.toMap(c -> c.getText(context), Function.identity()));

        this.skillLookup = HashMultimap.create();
        for (Skill s : Skill.values()) {
            skillLookup.put(s.getCategory(), s);
        }
    }

    /**
     * Analyses a bitmap.
     *
     * @param b a {@link Bitmap}
     * @return The {@link Artifact}, it represents
     * @throws OcrException if <i>b</i> doesn't show an {@link Artifact}
     */
    public Artifact analyze(Bitmap b) throws OcrException {
        Frame frame = new Frame.Builder()
                .setBitmap(b)
                .build();
        int hcenter = frame.getMetadata().getWidth() / 2; // horizontal center
        int vcenter = frame.getMetadata().getHeight() / 2; // vertical center

        SparseArray<TextBlock> allTextBlocks = textRecognizer.detect(frame);
        List<TextBlock> relevantTextBlocks = new ArrayList<>();
        for (int i = 0; i < allTextBlocks.size(); i++) {
            TextBlock tb = allTextBlocks.valueAt(i);
            if (isOutOfInterest(tb, frame)) {
                continue;
            }

            relevantTextBlocks.add(tb);
        }

        Function<TextBlock, Integer> hCompValue = tb -> {
            if (tb.getValue().length() <= 7) {
                return tb.getBoundingBox().right;
            }

            return tb.getBoundingBox().left;
        };

        relevantTextBlocks.sort((tb1, tb2) -> {
            if (Math.abs(hCompValue.apply(tb1) - hCompValue.apply(tb2)) < 15) {
                return tb1.getBoundingBox().top - tb2.getBoundingBox().top;
            }

            return tb1.getBoundingBox().left - tb2.getBoundingBox().left;
        });

        Optional<Category> optCategory = Optional.empty();
        Optional<String> optName = Optional.empty();
        List<String> skillTexts = new ArrayList<>();
        List<String> bonusTexts = new ArrayList<>();
        List<String> levelTexts = new ArrayList<>();

        List<ArtifactElement> order = ImmutableList.of(ArtifactElement.CATEGORY, ArtifactElement.NAME,
                ArtifactElement.SKILL, ArtifactElement.SKILL, ArtifactElement.SKILL, ArtifactElement.SKILL, ArtifactElement.SKILL,
                ArtifactElement.LEVEL, ArtifactElement.LEVEL, ArtifactElement.LEVEL, ArtifactElement.LEVEL, ArtifactElement.LEVEL,
                ArtifactElement.BONUS, ArtifactElement.BONUS, ArtifactElement.BONUS, ArtifactElement.BONUS, ArtifactElement.BONUS);
        int orderIndex = 0;

        for (int i = 0; i < relevantTextBlocks.size(); i++) {
            TextBlock tb = relevantTextBlocks.get(i);

            switch (order.get(orderIndex)) {
                case NAME:
                    if (i == relevantTextBlocks.size() - 1) {
                        break;
                    }

                    TextBlock nextBlock = relevantTextBlocks.get(i + 1);
                    optName = checkName(tb, nextBlock, hcenter, vcenter);
                    if (optName.isPresent()) {
//                        Log.v("analyze2 > checkName", "Name found: " + tb.getValue());
                        orderIndex++;

                        if (tb.getBoundingBox().bottom + 20 > nextBlock.getBoundingBox().top) {
                            i++; // nextBlock is also part of the name => increment i to skip it
                        }
                    } else {
//                        Log.v("analyze2 > checkName", "Not a name: " + tb.getValue());
                    }
                    break;
                case SKILL:
                    if (checkSkill(tb, skillTexts, hcenter)) {
//                        Log.v("analyze2 > checkSkill", "Skill found: " + tb.getValue());
                        orderIndex++;
                    } else {
//                        Log.v("analyze2 > checkSkill", "Not a skill: " + tb.getValue());
                    }
                    break;
                case BONUS:
                    if (checkBonus(tb, bonusTexts)) {
//                        Log.v("analyze2 > checkBonus", "Bonus found: " + tb.getValue());
                        orderIndex++;
                    } else {
//                        Log.v("analyze2 > checkBonus", "Not a bonus: " + tb.getValue());
                    }
                    break;
                case LEVEL:
                    if (checkLevel(tb, levelTexts)) {
//                        Log.v("analyze2 > checkLevel", "Level found: " + tb.getValue());
                        orderIndex++;
                    } else {
//                        Log.v("analyze2 > checkLevel", "Not a level: " + tb.getValue());
                    }
                    break;
                case CATEGORY:
                    optCategory = checkCategory(tb, hcenter, vcenter);
                    if (optCategory.isPresent()) {
//                        Log.v("analyze2 > checkCategory", "Category found: " + tb.getValue());
                        orderIndex++;
                    } else {
//                        Log.v("analyze2 > checkCategory", "Not a category: " + tb.getValue());
                    }
                    break;
            }

            if (orderIndex >= order.size()) {
                break; // Exit, when all texts have been detected
            }
        }

        if (!optName.isPresent()
                || !optCategory.isPresent()
                || skillTexts.size() < 5
                || bonusTexts.size() < 5
                || levelTexts.size() < 5) {
            throw new OcrException("Artifact not (completely) identified");
        }

        Category category = optCategory.get();

        List<SkillContainer> skills = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String s = skillTexts.get(i);

            Pair<Integer, Skill> bestMatch = Pair.create(Integer.MAX_VALUE, Skill.UNDEFINED);
            for (Skill skill : skillLookup.get(category)) {
                int lev = applyDiffAndLev(context.getString(skill.getResId()), s);
                if (lev == 0) {
                    bestMatch = Pair.create(0, skill);
                    break;
                }

                if (lev < bestMatch.first) {
                    bestMatch = Pair.create(lev, skill);
                }
            }

            int bonus = Integer.parseInt(bonusTexts.get(i));
            int level = Integer.parseInt(levelTexts.get(i));

            skills.add(SkillContainer.builder()
                    .skill(bestMatch.second)
                    .quality(bonus - level + 1)
                    .level(level)
                    .build());
        }

        return Artifact.builder()
                .name(optName.get())
                .category(category)
                .skills(skills)
                .build();
    }

    /**
     * @param tb      {@link TextBlock} to check
     * @param hcenter frame width devided by 2
     * @param vcenter frame height devided by 2
     * @return A {@link Category}, if the text box represents one, or<br/>
     * an empty {@link Optional}.
     */
    private Optional<Category> checkCategory(TextBlock tb, int hcenter, int vcenter) {
        if (hcenter < tb.getBoundingBox().right) {
            return Optional.empty();
        }

        if (vcenter < tb.getBoundingBox().top) {
            return Optional.empty();
        }

        Pair<Integer, String> bestGuess = Pair.create(Integer.MAX_VALUE, null);
        for (String match : categoryLookup.keySet()) {
            int lev = applyDiffAndLev(match, tb.getValue());
            if (lev == 0) {
                // Perfect match, we're fine
                return Optional.ofNullable(categoryLookup.get(match));
            }

            if (lev < bestGuess.first) {
                bestGuess = Pair.create(lev, match);
            }
        }

        if (bestGuess.first > 4) {
            // Lev wasn't good enough => Discard bestGuess
            return Optional.empty();
        }

        return Optional.ofNullable(categoryLookup.get(bestGuess.second));
    }

    private boolean checkLevel(TextBlock tb, List<String> levelTexts) {
        Matcher m = LEVEL_PATTERN.matcher(tb.getValue());
        if (!m.find()) {
            return false;
        }

        return levelTexts.add(m.group(1));
    }

    private boolean checkBonus(TextBlock tb, List<String> bonusTexts) {
        Matcher m = BONUS_PATTERN.matcher(tb.getValue());
        if (!m.find()) {
            return false;
        }

        return bonusTexts.add(m.group(2));
    }

    private boolean checkSkill(TextBlock tb, List<String> skillTexts, int hcenter) {
        if (tb.getBoundingBox().right < hcenter * 9 / 10 || hcenter < tb.getBoundingBox().left) {
            return false;
        }

        Matcher m = SKILL_PATTERN.matcher(tb.getValue());
        if (!m.find()) {
            return false;
        }

        return skillTexts.add(m.group(1));
    }

    /**
     * @param tb        {@link TextBlock} to check
     * @param nextBlock The tex block after tb, because it might contain parts of the name
     * @param hcenter   frame width devided by 2
     * @param vcenter   frame height devided by 2
     * @return The artifact's name, if the text box represents it, or<br/>
     * an empty optional
     */
    private Optional<String> checkName(TextBlock tb, TextBlock nextBlock, int hcenter, int vcenter) {
        if (hcenter < tb.getBoundingBox().left || tb.getBoundingBox().right < hcenter) {
            return Optional.empty();
        }

        if (vcenter < tb.getBoundingBox().bottom) {
            return Optional.empty();
        }

        Matcher matcher = NAME_PATTERN.matcher(tb.getValue());
        if (!matcher.find()) {
            Optional.empty();
        }

        if (tb.getBoundingBox().bottom + 20 > nextBlock.getBoundingBox().top) {
            // Next text block is very close to tb => Assume, that the next text block also belongs to the name

            return Optional.of(String.format("%s %s",
                    matcher.group(1),
                    nextBlock.getValue()));
        }

        return Optional.ofNullable(matcher.group(1));
    }

    /**
     * The 'Levenshtein Distance' is the minimal amount of insert/delete/replace operations to transfer
     * a first string into a second. The fewer operations, the more equal the texts are. Thus being a
     * good indicator, if two texts are equal besides some typos that occur during OCR.
     * <p>
     * Note: That number is always positive.
     *
     * @return the levenshtein distance between <i>text1</i> and <i>text2</i>
     */
    private int applyDiffAndLev(String text1, String text2) {
        return diffMatchPatch.diffLevenshtein(diffMatchPatch.diffMain(text1, text2));
    }

    /**
     * A textBlock is out of interest, if it touches the left or right 17.5% or the width.
     * <p>
     * Idea: The artifact dialog pops up in the center of the screen. Everything left and right is from the uninteresting background.
     */
    private boolean isOutOfInterest(TextBlock textBlock, Frame frame) {
        // 17.5% and 100% - 17.5% = 82.5%
        return textBlock.getBoundingBox().left < frame.getMetadata().getWidth() * 0.175
                || textBlock.getBoundingBox().right > frame.getMetadata().getWidth() * 0.825
                || textBlock.getValue().length() <= 2;
    }

    /**
     * Interesting elements of (an image of) an artifact.
     */
    enum ArtifactElement {
        NAME, SKILL, BONUS, LEVEL, CATEGORY
    }

}
