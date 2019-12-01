package paleoftheancients.patches;

import basemod.patches.com.megacrit.cardcrawl.dungeons.AbstractDungeon.ActChangeHooks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import paleoftheancients.dungeons.CustomDungeon;

import java.util.ArrayList;

@SpirePatch(
        clz = ActChangeHooks.InGameConstructor.class,
        method = "Postfix"
)
public class PatchingaPatchInBaseModBecauseRAAAAAAGH {
    public static SpireReturn<Void> Prefix(AbstractDungeon __instance,
                                              String name, String levelId, AbstractPlayer p, ArrayList<String> newSpecialOneTimeEventList) {
        if(__instance instanceof CustomDungeon && p == null) {
            return SpireReturn.Return(null);
        }
        return SpireReturn.Continue();
    }
}
