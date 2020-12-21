package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.Instanceof;

import java.util.ArrayList;
import java.util.Map;


@SpirePatch(
        clz = AbstractRoom.class,
        method = "update"
)
public class PreventActThreeBossRewardsPatch {
    @SpireInstrumentPatch
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            public void edit(Instanceof m) throws CannotCompileException {
                try {
                    if (m.getType().getName().equals(TheEnding.class.getName())) {
                        m.replace("{ $_ = $proceed($$) || " + PreventActThreeBossRewardsPatch.class.getName() + ".isThirdActOrHigher(); }");
                    }
                }catch(NotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static boolean isThirdActOrHigher() {
        if(CardCrawlGame.dungeon instanceof CustomDungeon) {
            for (final Map.Entry<Integer, ArrayList<String>> ri : CustomDungeon.actnumbers.entrySet()) {
                if (ri.getKey() >= 3) {
                    for (final String dungeonId : ri.getValue()) {
                        if (dungeonId.equals(AbstractDungeon.id)) {
                            return ((CustomDungeon) CardCrawlGame.dungeon).preventFinalActRewards;
                        }
                    }
                }
            }
        }
        return false;
    }
}