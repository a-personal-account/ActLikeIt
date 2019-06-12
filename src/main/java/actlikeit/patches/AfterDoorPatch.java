package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import actlikeit.events.GetForked;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.DoorUnlockScreen;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CtBehavior;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@SpirePatch(
        clz = DoorUnlockScreen.class,
        method = "exit"
)
public class AfterDoorPatch {
    @SpireInsertPatch(
            locator = Locator.class
    )

    public static SpireReturn<Void> Prefix(DoorUnlockScreen __instance) {
        ArrayList<String> availableActs = new ArrayList<>();
        if(CustomDungeon.actnumbers.containsKey(AbstractDungeon.actNum + 1)) {
            for(final String s : CustomDungeon.actnumbers.get(AbstractDungeon.actNum + 1)) {
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
