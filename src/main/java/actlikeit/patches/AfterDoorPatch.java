package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import actlikeit.savefields.BehindTheScenesActNum;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.GameCursor;
import com.megacrit.cardcrawl.screens.DoorUnlockScreen;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz = DoorUnlockScreen.class,
        method = "exit"
)
public class AfterDoorPatch {
    @SpireInsertPatch(
            locator = Locator.class
    )

    //Set the fork-event if there's options after the door on this level.
    public static SpireReturn<Void> Prefix(DoorUnlockScreen __instance) {
        ArrayList<String> availableActs = new ArrayList<>();
        if(CustomDungeon.actnumbers.containsKey(BehindTheScenesActNum.getActNum() + 1)) {
            for(final String s : CustomDungeon.actnumbers.get(BehindTheScenesActNum.getActNum() + 1)) {
                CustomDungeon cd = CustomDungeon.dungeons.get(s);
                if(cd.finalAct) {
                    availableActs.add(s);
                }
            }
        }
        if(!availableActs.isEmpty()) {
            CardCrawlGame.mode = CardCrawlGame.GameMode.GAMEPLAY;
            CardCrawlGame.music.fadeOutBGM();
            CardCrawlGame.music.fadeOutTempBGM();
            GameCursor.hidden = false;
            GoToNextDungeonPatch.getForked(true);
            return SpireReturn.Return(null);
        }

        return SpireReturn.Continue();
    }


    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(DoorUnlockScreen.class, "eventVersion");
            return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[1]};
        }
    }
}
