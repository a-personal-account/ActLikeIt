package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.CustomBosses;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;

import java.util.Collections;


@SpirePatch(
        clz = CustomBosses.AddBosses.class,
        method = "Do"
)
public class BasemodBossPatch {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(AbstractDungeon dungeon) {
        if (dungeon instanceof CustomDungeon) {
            ((CustomDungeon) dungeon).processMandatoryBosses();
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(Collections.class, "shuffle");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}