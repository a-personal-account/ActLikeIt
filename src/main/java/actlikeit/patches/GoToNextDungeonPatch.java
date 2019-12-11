package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import actlikeit.events.GetForked;
import actlikeit.savefields.BehindTheScenesActNum;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import javassist.CtBehavior;

@SpirePatch(
        clz = ProceedButton.class,
        method = "goToNextDungeon"
)
public class GoToNextDungeonPatch {
    @SpireInsertPatch(
            locator = Locator.class
    )
    public static SpireReturn<Void> Insert(ProceedButton __instance, AbstractRoom room) {
        //Trigger the fork event if there is a custom act available here.
        int nextActs = CustomDungeon.actnumbers.containsKey(BehindTheScenesActNum.getActNum() + 1) ? CustomDungeon.actnumbers.get(BehindTheScenesActNum.getActNum() + 1).size() : 0;
        if(((nextActs > 0 && BehindTheScenesActNum.getActNum() <= CustomDungeon.THEBEYOND) || nextActs > 1)
                || (Settings.isEndless && BehindTheScenesActNum.getActNum() >= 3 && (CustomDungeon.actnumbers.containsKey(CustomDungeon.EXORDIUM) || nextActs > 0))) {
            getForked();

            return SpireReturn.Return(null);
        } else {
            switch(BehindTheScenesActNum.getActNum() + 1) {
                case CustomDungeon.THECITY:
                    CardCrawlGame.nextDungeon = TheCity.ID;
                    break;
                case CustomDungeon.THEBEYOND:
                    CardCrawlGame.nextDungeon = TheBeyond.ID;
                    break;
                default:
                    if(Settings.isEndless || AbstractDungeon.floorNum < 1) {
                        CardCrawlGame.nextDungeon = Exordium.ID;
                        if(BehindTheScenesActNum.getActNum() >= 3) {
                            BehindTheScenesActNum.resetActNum();
                        }
                    } else {
                        CardCrawlGame.nextDungeon = TheEnding.ID;
                    }
                    break;
            }
        }

        return SpireReturn.Continue();
    }

    public static void getForked() {
        getForked(false);
    }
    public static void getForked(boolean afterdoor) {
        AbstractDungeon.currMapNode.room = new ForkEventRoom(AbstractDungeon.currMapNode.room, afterdoor);
        AbstractDungeon.getCurrRoom().onPlayerEntry();
        AbstractDungeon.rs = AbstractDungeon.RenderScene.EVENT;

        AbstractDungeon.combatRewardScreen.clear();
        AbstractDungeon.previousScreen = null;
        AbstractDungeon.closeCurrentScreen();
    }

    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "fadeOut");
            return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
        }
    }

    public static class ForkEventRoom extends EventRoom {
        public AbstractRoom originalRoom;
        private boolean afterdoor;

        ForkEventRoom(AbstractRoom originalRoom, boolean afterdoor) {
            this.originalRoom = originalRoom;
            this.afterdoor = afterdoor;
        }

        @Override
        public void onPlayerEntry() {
            AbstractDungeon.overlayMenu.proceedButton.hide();
            this.event = new GetForked(afterdoor);
            this.event.onEnterRoom();
        }
    }
}
