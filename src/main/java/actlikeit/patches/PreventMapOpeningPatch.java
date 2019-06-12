package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.controller.CInputHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import javassist.CtBehavior;


@SpirePatch(
        clz = CardCrawlGame.class,
        method = "update"
)
public class PreventMapOpeningPatch {

    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn<Void> Insert(CardCrawlGame __instance) {
        if(AbstractDungeon.floorNum <= 1 ||
                (CustomDungeon.dungeons.containsKey(CardCrawlGame.nextDungeon) && CustomDungeon.dungeons.get(CardCrawlGame.nextDungeon).hasEvent())) {

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
