package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import javassist.CtBehavior;

@SpirePatch(
        clz = DungeonTransitionScreen.class,
        method = "setAreaName"
)
public class SetAreaNamePatch {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(DungeonTransitionScreen __instance, String key) {
        if (CustomDungeon.dungeons.containsKey(key)) {
            CustomDungeon cd = CustomDungeon.dungeons.get(key);
            __instance.levelName = cd.name;

            String[] tmp = (String[])CustomDungeon.actnumbers.values().toArray();
            for(int i = 0; i < tmp.length; i++) {
                if(tmp[i] == key) {
                    __instance.levelNum = Integer.toString((int)CustomDungeon.actnumbers.keySet().toArray()[i]);
                    break;
                }
            }
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "name");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}