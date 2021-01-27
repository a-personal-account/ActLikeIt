package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import javassist.CtBehavior;


public class BossEncounteredPatch {
    @SpirePatch(
            clz = AbstractRoom.class,
            method = "update"
    )
    public static class OnKill {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(AbstractRoom __instance) {
            if (CardCrawlGame.dungeon instanceof CustomDungeon) {
                ((CustomDungeon) CardCrawlGame.dungeon).bossKilled();
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(Settings.class, "isDailyRun");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }


    @SpirePatch(
            clz = MonsterRoomBoss.class,
            method = "onPlayerEntry"
    )
    public static class AscensionPenaltiesPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            if (CardCrawlGame.dungeon instanceof CustomDungeon) {
                ((CustomDungeon) CardCrawlGame.dungeon).bossSeen();
            }
        }
    }
}