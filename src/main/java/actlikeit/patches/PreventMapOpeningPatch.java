package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import actlikeit.savefields.BehindTheScenesActNum;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import javassist.CtBehavior;


@SpirePatch(
        clz = CardCrawlGame.class,
        method = "update"
)
public class PreventMapOpeningPatch {
    //Upon entering a new act, the map is opened and it scrolls down unless it's precisely the Exordium.
    //This here prevents that if the custom act has a set starting event, or is act 1.
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn<Void> Insert(CardCrawlGame __instance) {
        if(CustomDungeon.dungeons.containsKey(CardCrawlGame.nextDungeon) && (CustomDungeon.dungeons.get(CardCrawlGame.nextDungeon).hasEvent() || BehindTheScenesActNum.getActNum() == 1)) {
            InputHelper.updateLast();
            if (CInputHelper.controller != null) {
                CInputHelper.updateLast();
            }
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(DungeonMapScreen.class, "open");
            int[] result = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
            return new int[]{result[result.length - 1]};
        }
    }

}
