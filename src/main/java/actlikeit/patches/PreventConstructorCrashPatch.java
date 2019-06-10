package actlikeit.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.ArrayList;

@SpirePatch(
        clz = AbstractDungeon.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez={
                String.class,
                String.class,
                AbstractPlayer.class,
                ArrayList.class
        }
)
public class PreventConstructorCrashPatch {
    public static SpireReturn Prefix(AbstractDungeon __instance, String a, String b, AbstractPlayer p, ArrayList c) {
        if(p == null) {
            return SpireReturn.Return(null);
        } else {
            return SpireReturn.Continue();
        }
    }
}
