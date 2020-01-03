package actlikeit.patches;

import actlikeit.savefields.BehindTheScenesActNum;
import actlikeit.savefields.BreadCrumbs;
import actlikeit.savefields.CustomScore;
import actlikeit.savefields.ElitesSlain;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ActNumPatch {
    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "dungeonTransitionSetup"
    )
    public static class IncrementActNum {
        public static void Prefix() {
            BehindTheScenesActNum.incrementActNum();
        }
    }

    @SpirePatch(
            clz = AbstractDungeon.class,
            method = "reset"
    )
    public static class ResetIncrementActNum {
        public static void Prefix() {
            BehindTheScenesActNum.resetActNum();
            ElitesSlain.getKilledElites().clear();
            CustomScore.getCustomScores().clear();
            BreadCrumbs.getBreadCrumbs().clear();
        }
    }
}
