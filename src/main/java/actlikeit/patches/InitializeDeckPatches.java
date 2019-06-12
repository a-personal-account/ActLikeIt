package actlikeit.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;


public class InitializeDeckPatches {

    @SpirePatch(
            clz = AbstractPlayer.class,
            method = "initializeStarterDeck"
    )
    public static class PreventDeckInitializationPatch {

        public static SpireReturn<Void> Prefix(AbstractPlayer __instance) {
            if(__instance.masterDeck.size() > 1) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }


    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "dungeonTransitionSetup"
    )
    public static class AscensionPenaltiesPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static SpireReturn<Void> Insert() {
            if(AbstractDungeon.player.currentHealth < AbstractDungeon.player.maxHealth && AbstractDungeon.floorNum <= 1) {
                AbstractDungeon.dungeonMapScreen.map.atBoss = false;
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "dungeon");
                int[] result = LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
                return new int[]{result[result.length - 1]};
            }
        }
    }

}
