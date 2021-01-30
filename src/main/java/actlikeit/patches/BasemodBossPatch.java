package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.CustomBosses;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;

import java.util.Collections;
import java.util.List;
import java.util.Random;


public class BasemodBossPatch {
    @SpirePatch(
            clz = CustomBosses.AddBosses.class,
            method = "Do"
    )
    public static class ProcessBossPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Void> Insert(AbstractDungeon dungeon) {
            if (dungeon instanceof CustomDungeon) {
                List<String> triggered = ((CustomDungeon) dungeon).processMandatoryBosses();
                if(triggered != null) {
                    Collections.shuffle(triggered, new Random(AbstractDungeon.monsterRng.randomLong()));
                    AbstractDungeon.bossList.addAll(triggered);
                }
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(Collections.class, "shuffle");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}