package actlikeit.patches;

import actlikeit.ActLikeIt;
import actlikeit.dungeons.CustomDungeon;
import actlikeit.savefields.BreadCrumbs;
import actlikeit.savefields.ElitesSlain;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.GameOverStat;
import com.megacrit.cardcrawl.screens.VictoryScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.*;

public class VictoryOrDeathScreenPatch {
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
            doThing(__instance.stats);
        }

        public static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ArrayList.class, "add");

                return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[5]};
            }
        }
    }

    private static void doThing(ArrayList<GameOverStat> stats) {
        Map<String, ElitesSlain.Entry> elitesKilled = ElitesSlain.getKilledElites();
        Map<Integer, String> breadcrumbs = BreadCrumbs.getBreadCrumbs();
        String[] parts = CardCrawlGame.languagePack.getScoreString(ActLikeIt.makeID("ElitesKilled")).DESCRIPTIONS;

        ArrayList<ActData> relevantActs = new ArrayList<>();

        Set<String> used = new HashSet<>();
        //Filter the breadcrumbs
        for(final Integer i : breadcrumbs.keySet()) {
            String dungeonID = breadcrumbs.get(i);
            if(!used.contains(dungeonID)) {
                used.add(dungeonID);
                if(elitesKilled.containsKey(dungeonID)) {
                    String localizedString;
                    if (elitesKilled.containsKey(dungeonID)) {
                        localizedString = parts[0] + CustomDungeon.dungeons.get(dungeonID).name + parts[2];
                    } else {
                        localizedString = parts[0] + parts[1] + " " + i + parts[2];
                    }
                    relevantActs.add(new ActData(localizedString, elitesKilled.get(dungeonID).actnum, elitesKilled.get(dungeonID).kills));
                }
            }
        }

        //Sort by actnumber, then add to the stats.
        Collections.sort(relevantActs, Comparator.comparingInt(a -> a.actnum));
        for(final ActData data : relevantActs) {
            stats.add(new GameOverStat(data.localizedString + " (" + data.kills + ")", null, Integer.toString(data.kills * 10 * data.actnum)));
        }
    }

    static class ActData {
        public String localizedString;
        public int actnum;
        public int kills;

        public ActData(String localizedString, int actnum, int kills) {
            this.localizedString = localizedString;
            this.actnum = actnum;
            this.kills = kills;
        }
    }
}