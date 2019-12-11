package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;


@SpirePatch(
        clz = AbstractDungeon.class,
        method = "dungeonTransitionSetup"
)
public class PreventAscensionModifiersOnFirstTransition {
    //To prevent the HP and stuff modifiers when choosing a different act 1
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn<Void> Insert() {
        if(AbstractDungeon.actNum == 2 && AbstractDungeon.floorNum < 1 && CustomDungeon.dungeons.containsKey(CardCrawlGame.nextDungeon)) {
            AbstractDungeon.actNum--;

            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "ascensionLevel");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

}
