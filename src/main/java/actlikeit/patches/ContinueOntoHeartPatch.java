package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import actlikeit.savefields.BehindTheScenesActNum;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CtBehavior;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SpirePatch(
        clz = ProceedButton.class,
        method = "update"
)
public class ContinueOntoHeartPatch {
    public static String exmsg = "F";
    @SpireInsertPatch(
            locator = Locator.class
    )

    public static void Insert(ProceedButton __instance) {
        //Mostly copied from the basegame and applied to custom dungeons on or higher than Beyond's level.
        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
            if (BehindTheScenesActNum.getActNum() >= CustomDungeon.THEBEYOND && !(CardCrawlGame.dungeon instanceof TheBeyond)) {
                try {
                    if(Settings.isEndless || !(CardCrawlGame.dungeon instanceof CustomDungeon)) {
                        throw new RuntimeException(exmsg);
                    }
                    ((CustomDungeon) CardCrawlGame.dungeon).Ending();
                } catch(RuntimeException ex) {
                    if(!ex.getMessage().equals(exmsg)) {
                        throw ex;
                    }

                    if (AbstractDungeon.ascensionLevel >= 20 && AbstractDungeon.bossList.size() == 2) {
                        try {
                            Method yuckyPrivateMethod = ProceedButton.class.getDeclaredMethod("goToDoubleBoss");
                            yuckyPrivateMethod.setAccessible(true);
                            yuckyPrivateMethod.invoke(__instance);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } else if (!Settings.isEndless) {
                        heartRoom(__instance);
                    }
                }
            }
        }
    }

    public static void heartRoom(ProceedButton pd) {
        try {
            Method yuckyPrivateMethod = ProceedButton.class.getDeclaredMethod("goToVictoryRoomOrTheDoor");
            yuckyPrivateMethod.setAccessible(true);
            yuckyPrivateMethod.invoke(pd);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.InstanceOfMatcher(MonsterRoomBoss.class);
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
