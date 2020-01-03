package actlikeit.patches;

import actlikeit.savefields.CustomScore;
import actlikeit.savefields.ElitesSlain;
import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;

public class CalcFinalScorePatch {
    //Forget this for now. Asked Casey to make it much easier for us to do score stuff, and then this would change massively.

    @SpirePatch(
            clz = VictoryScreen.class,
            method = "calcScore"
    )
    public static class VictoryScreenPatch {
        public static int Postfix(int tmp, boolean isVictory) {
            return doThing(tmp);
        }
    }

    @SpirePatch(
            clz = DeathScreen.class,
            method = "calcScore"
    )
    public static class DeathScreenPatch {
        public static int Postfix(int tmp, boolean isVictory) {
            return doThing(tmp);
        }
    }

    private static int doThing(int tmp) {
        for(final ElitesSlain.Entry entry : ElitesSlain.getKilledElites().values()) {
            tmp += entry.kills * 10 * entry.actnum;
        }
        for(final CustomScore.Entry entry : CustomScore.getCustomScores()) {
            tmp += entry.score * (entry.stackable ? entry.amount : 1);
        }

        return tmp;
    }
}