package actlikeit.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.GameOverStat;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import actlikeit.ActLikeIt;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class VictoryOrDeathScreenPatch {
    //Forget this for now. Asked Casey to make it much easier for us to do score stuff, and then this would change massively.
/*
    @SpirePatch(
            clz = VictoryScreen.class,
            method = "createGameOverStats"
    )
    public static class VictoryScreenPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(VictoryScreen __instance) {
            if (CardCrawlGame.dungeon instanceof Exordium || CardCrawlGame.dungeon instanceof TheCity || CardCrawlGame.dungeon instanceof TheBeyond || (CardCrawlGame.dungeon instanceof TheEnding && !ActLikeIt.wentToTheFactory)) {
                return;
            }
            if (CardCrawlGame.dungeon instanceof Factory) {
                try {
                    String localizedString = CardCrawlGame.languagePack.getScoreString("City Elites Killed").NAME;
                    Field elite2PointsField = VictoryScreen.class.getDeclaredField("elite2Points");
                    elite2PointsField.setAccessible(true);
                    String elite2Points = Integer.toString((int) elite2PointsField.get(null));
                    __instance.stats.add(new GameOverStat(localizedString + " (" + CardCrawlGame.elites2Slain + ")", null, elite2Points));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (CardCrawlGame.dungeon instanceof Factory || ActLikeIt.wentToTheFactory) {
                try {
                    String localizedString = CardCrawlGame.languagePack.getScoreString(ActLikeIt.makeID("ElitesKilled")).NAME;
                    Field elite3PointsField = VictoryScreen.class.getDeclaredField("elite3Points");
                    elite3PointsField.setAccessible(true);
                    String elite3Points = Integer.toString((int) elite3PointsField.get(null));
                    __instance.stats.add(new GameOverStat(localizedString + " (" + CardCrawlGame.elites3Slain + ")", null, elite3Points));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");

                return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[5]};
            }
        }
    }

    @SpirePatch(
            clz = DeathScreen.class,
            method = "createGameOverStats"
    )
    public static class DeathScreenPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(DeathScreen __instance) {
            if (CardCrawlGame.dungeon instanceof Exordium || CardCrawlGame.dungeon instanceof TheCity || CardCrawlGame.dungeon instanceof TheBeyond || (CardCrawlGame.dungeon instanceof TheEnding && !ActLikeIt.wentToTheFactory)) {
                return;
            }
            if (CardCrawlGame.dungeon instanceof Factory) {
                try {
                    String localizedString = CardCrawlGame.languagePack.getScoreString("City Elites Killed").NAME;
                    Field elite2PointsField = DeathScreen.class.getDeclaredField("elite2Points");
                    elite2PointsField.setAccessible(true);
                    String elite2Points = Integer.toString((int) elite2PointsField.get(null));
                    __instance.stats.add(new GameOverStat(localizedString + " (" + CardCrawlGame.elites2Slain + ")", null, elite2Points));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (CardCrawlGame.dungeon instanceof Factory || ActLikeIt.wentToTheFactory) {
                try {
                    String localizedString = CardCrawlGame.languagePack.getScoreString(ActLikeIt.makeID("ElitesKilled")).NAME;
                    Field elite3PointsField = DeathScreen.class.getDeclaredField("elite3Points");
                    elite3PointsField.setAccessible(true);
                    String elite3Points = Integer.toString((int) elite3PointsField.get(null));
                    __instance.stats.add(new GameOverStat(localizedString + " (" + CardCrawlGame.elites3Slain + ")", null, elite3Points));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");

                return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[5]};
            }
        }
    }*/
}