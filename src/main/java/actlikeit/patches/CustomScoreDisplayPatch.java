package actlikeit.patches;

import actlikeit.savefields.CustomScore;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.GameOverStat;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

public class CustomScoreDisplayPatch {
    @SpirePatch(
            clz = VictoryScreen.class,
            method = "createGameOverStats"
    )
    public static class VictoryScreenPatch {

        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(VictoryScreen __instance) {
            doThing(__instance.stats);
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");
                int[] result = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);

                return new int[] { result[result.length - 2] };
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
            doThing(__instance.stats);
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");
                int[] result = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher);

                return new int[] { result[result.length - 2] };
            }
        }
    }

    private static void doThing(ArrayList<GameOverStat> stats) {
        String endstring;
        int score;

        for(final CustomScore.Entry data : CustomScore.getCustomScores()) {
            endstring = data.localizedString;
            score = data.score;
            if(data.stackable) {
                score *= data.amount;
                endstring += " (" + data.amount + ")";
            }
            stats.add(new GameOverStat(endstring, data.description, Integer.toString(score)));
        }
    }
}