package actlikeit.patches;

import actlikeit.savefields.ElitesSlain;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CannotCompileException;
import javassist.CtBehavior;

import java.util.ArrayList;

@SpirePatch(
        clz = AbstractRoom.class,
        method = "update"
)
public class AbstractRoomUpdateIncrementElitesPatch {


    @SpireInsertPatch(
            locator = Locator.class
    )
    public static void Insert(AbstractRoom __instance) {
        ElitesSlain.kill();
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "elitesModdedSlain");

            return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[1]};
        }
    }
}
