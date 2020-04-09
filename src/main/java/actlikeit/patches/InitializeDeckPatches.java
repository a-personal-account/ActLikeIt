package actlikeit.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.CtBehavior;

import java.util.ArrayList;


public class InitializeDeckPatches {
    //Loading an act on floornumber 0 has odd behaviour that is corrected with these 2 patches.
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = SpirePatch.CONSTRUCTOR,
            paramtypez = { String.class, String.class, AbstractPlayer.class, ArrayList.class }
    )
    public static class PreventDeckInitializationPatch {
        @SpireInsertPatch(
                locator = PrevLocator.class
        )
        public static void Prefix(AbstractDungeon __instance, String name, String levelId, AbstractPlayer p, ArrayList<String> newSpecialOneTimeEventList) {
            if(AbstractDungeon.player != null && AbstractDungeon.player.masterDeck.size() > 1 && AbstractDungeon.floorNum == 0) {
                AbstractDungeon.floorNum = -1;
            }
        }
        private static class PrevLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "floorNum");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }

        @SpireInsertPatch(
                locator = PostLocator.class
        )
        public static void Postfix(AbstractDungeon __instance, String name, String levelId, AbstractPlayer p, ArrayList<String> newSpecialOneTimeEventList) {
            if(AbstractDungeon.player != null && AbstractDungeon.floorNum < 0) {
                AbstractDungeon.floorNum = 0;
            }
        }
        private static class PostLocator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "initializePotions");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
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
