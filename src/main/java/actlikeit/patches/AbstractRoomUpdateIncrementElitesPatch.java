package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
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
        switch(AbstractDungeon.actNum) {
            case CustomDungeon.EXORDIUM:
                if(!(CardCrawlGame.dungeon instanceof Exordium)) {
                    CardCrawlGame.elites1Slain++;
                }
                break;
            case CustomDungeon.THECITY:
                if(!(CardCrawlGame.dungeon instanceof TheCity)) {
                    CardCrawlGame.elites2Slain++;
                }
                break;
            case CustomDungeon.THEBEYOND:
                if(!(CardCrawlGame.dungeon instanceof TheBeyond)) {
                    CardCrawlGame.elites3Slain++;
                }
                break;
        }
        //ActLikeIt.logger.info("Factory elites: " + CardCrawlGame.elites3Slain);
    }

    public static class Locator extends SpireInsertLocator {
        public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
            Matcher finalMatcher = new Matcher.FieldAccessMatcher(CardCrawlGame.class, "dungeon");

            return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<>(), finalMatcher)[1]};
        }
    }
}
