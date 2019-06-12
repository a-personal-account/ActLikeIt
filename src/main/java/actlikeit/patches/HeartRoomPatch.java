package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;

import java.util.ArrayList;

@SpirePatch(
        clz = ProceedButton.class,
        method = "goToVictoryRoomOrTheDoor"
)
public class HeartRoomPatch {

    public static SpireReturn<Void> Prefix(ProceedButton __instance) {
        if(!(AbstractDungeon.currMapNode.room instanceof GoToNextDungeonPatch.ForkEventRoom)) {
            ArrayList<String> availableActs = new ArrayList<>();
            if (CustomDungeon.actnumbers.containsKey(AbstractDungeon.actNum + 1)) {
                for (final String s : CustomDungeon.actnumbers.get(AbstractDungeon.actNum + 1)) {
                    CustomDungeon cd = CustomDungeon.dungeons.get(s);
                    if (!cd.finalAct) {
                        availableActs.add(s);
                    }
                }
            }

            if (!availableActs.isEmpty()) {
                GoToNextDungeonPatch.getForked();
                return SpireReturn.Return(null);
            }
        }
        return SpireReturn.Continue();
    }
}
