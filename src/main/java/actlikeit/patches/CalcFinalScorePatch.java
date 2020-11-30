package actlikeit.patches;

import actlikeit.savefields.CustomScore;
import actlikeit.savefields.ElitesSlain;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.GameOverScreen;

public class CalcFinalScorePatch {
    //Forget this for now. Asked Casey to make it much easier for us to do score stuff, and then this would change massively.

    @SpirePatch(
            clz = GameOverScreen.class,
            method = "calcScore"
    )
    public static class DeathScreenPatch {
        public static int Postfix(int tmp, boolean isVictory) {
            for (final ElitesSlain.Entry entry : ElitesSlain.getKilledElites().values()) {
                tmp += entry.kills * 10 * entry.actnum;
            }
            for (final CustomScore.Entry entry : CustomScore.getCustomScores()) {
                tmp += entry.score * (entry.stackable ? entry.amount : 1);
            }

            return tmp;
        }
    }
}