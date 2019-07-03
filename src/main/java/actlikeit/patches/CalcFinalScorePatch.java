package actlikeit.patches;

import actlikeit.savefields.ElitesSlain;
import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;

import java.util.Map;

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
        Map<Integer, Integer> elitesKilled = ElitesSlain.getKilledElites();

        BaseMod.logger.error("BEFORE: " + tmp);
        for(final Map.Entry<Integer, Integer> entry : elitesKilled.entrySet()) {
            tmp += entry.getValue() * 10 * entry.getKey();
        }
        BaseMod.logger.error("AFTER: " + tmp);

        return tmp;
    }
}