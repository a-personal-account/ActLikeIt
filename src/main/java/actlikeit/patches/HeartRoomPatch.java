package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;

@SpirePatch(
        clz = ProceedButton.class,
        method = "goToVictoryRoomOrTheDoor"
)
public class HeartRoomPatch {

    public static SpireReturn<Void> Prefix(ProceedButton __instance) {
        //Just before the heart room, trigger a fork event. If you come here from said fork event, it continues as it normally would.
        if(!(AbstractDungeon.currMapNode.room instanceof GoToNextDungeonPatch.ForkEventRoom)) {
            if (CustomDungeon.isForkNecessary()) {
                GoToNextDungeonPatch.getForked();
                return SpireReturn.Return(null);
            }
        }
        return SpireReturn.Continue();
    }
}
