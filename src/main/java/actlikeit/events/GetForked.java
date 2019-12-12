package actlikeit.events;

import actlikeit.ActLikeIt;
import actlikeit.dungeons.CustomDungeon;
import actlikeit.patches.ContinueOntoHeartPatch;
import actlikeit.patches.GoToNextDungeonPatch;
import actlikeit.savefields.BehindTheScenesActNum;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;

import java.util.ArrayList;

public class GetForked extends AbstractImageEvent {
    public static final String ID = ActLikeIt.makeID("ForkInTheRoad");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String BASE_IMG = ActLikeIt.MOD_ID + "/images/events/ForkInTheRoad.png";
    private static final String BasegameStringColor = "y";

    private boolean afterdoor;
    private boolean extraText;
    private int nextAct;
    private int resetActnum;

    private ArrayList<ActOption> options = new ArrayList<>();

    public GetForked(boolean afterdoor) {
        super(NAME, "", BASE_IMG);
        StringBuilder desc = new StringBuilder();

        this.afterdoor = afterdoor;
        this.extraText = true;

        nextAct = BehindTheScenesActNum.getActNum() + 1;
        if(AbstractDungeon.floorNum < 1) {
            nextAct--;
        }
        if(!Settings.isEndless || nextAct < CustomDungeon.THEENDING) {
            //Create a list of all possible acts following the current one.
            String actName = null;
            String actID = null;
            switch (nextAct) {
                case CustomDungeon.EXORDIUM:
                    actName = Exordium.NAME;
                    actID = Exordium.ID;
                    break;
                case CustomDungeon.THECITY:
                    actName = TheCity.NAME;
                    actID = TheCity.ID;
                    break;
                case CustomDungeon.THEBEYOND:
                    actName = TheBeyond.NAME;
                    actID = TheBeyond.ID;
                    break;

                default:
                    actName = TheEnding.NAME;
                    actID = TheEnding.ID;
                    break;
            }
            int nextBasegameAct = Math.min(nextAct, CustomDungeon.THEENDING);
            if(afterdoor) {
                nextBasegameAct++;
            }
            options.add(new ActOption(actID, FontHelper.colorString('[' + actName + "] " + OPTIONS[nextBasegameAct], BasegameStringColor)));
            desc.append(FontHelper.colorString(DESCRIPTIONS[nextBasegameAct - 1], BasegameStringColor) + " NL NL ");
        }

        if(CustomDungeon.actnumbers.containsKey(nextAct)) {
            this.addActOption(nextAct, desc, AbstractDungeon.floorNum < 1);
        }
        if(Settings.isEndless && !afterdoor && nextAct - 1 >= CustomDungeon.THEBEYOND && (CustomDungeon.actnumbers.containsKey(CustomDungeon.EXORDIUM) || CustomDungeon.actnumbers.containsKey(nextAct))) {
            resetActnum = imageEventText.optionList.size();
            options.add(new ActOption(Exordium.ID, FontHelper.colorString('[' + Exordium.NAME +  "] " + OPTIONS[OPTIONS.length - 1], BasegameStringColor), true));
            desc.append(FontHelper.colorString(DESCRIPTIONS[DESCRIPTIONS.length - 1], BasegameStringColor) + " NL NL ");
            this.addActOption(CustomDungeon.EXORDIUM, desc, true);
        } else {
            resetActnum = Integer.MAX_VALUE;
        }
        for(final ActOption ao : options) {
            imageEventText.setDialogOption(ao.text);
        }
        imageEventText.setDialogOption(OPTIONS[0]);
        this.body = desc.toString();
    }

    private void addActOption(int actnum, StringBuilder desc) {
        this.addActOption(actnum, desc, false);
    }
    private void addActOption(int actnum, StringBuilder desc, boolean resetActNum) {
        if(CustomDungeon.actnumbers.containsKey(actnum)) {
            for (final String s : CustomDungeon.actnumbers.get(actnum)) {
                CustomDungeon cd = CustomDungeon.dungeons.get(s);
                if(Settings.isEndless || afterdoor == cd.finalAct) {
                    options.add(new ActOption(cd.id, '[' + cd.name + "] " + cd.getOptionText(), resetActNum));
                    if (!cd.getBodyText().isEmpty()) {
                        desc.append(cd.getBodyText() + " NL NL ");
                    }
                }
            }
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        if(extraText) {
            //Chose the option "random act plx"
            if (buttonPressed == options.size()) {
                if (BehindTheScenesActNum.getActNum() + 1 >= CustomDungeon.THEENDING && !afterdoor) {
                    //Approaching the heart is not among the random options.
                    buttonPressed = AbstractDungeon.mapRng.random(options.size() - 2) + 1;
                } else {
                    buttonPressed = AbstractDungeon.mapRng.random(options.size() - 1);
                }
            }
            nextAct = buttonPressed;
        }

        if(extraText && CustomDungeon.dungeons.containsKey(options.get(buttonPressed).ID)
                && !CustomDungeon.dungeons.get(options.get(buttonPressed).ID).getAfterSelectText().isEmpty()) {
            extraText = false;
            imageEventText.clearAllDialogs();
            imageEventText.setDialogOption(OPTIONS[OPTIONS.length - 2]);
            imageEventText.updateBodyText(CustomDungeon.dungeons.get(options.get(buttonPressed).ID).getAfterSelectText());
        } else if(!Settings.isEndless && BehindTheScenesActNum.getActNum() + 1 >= CustomDungeon.THEENDING && !afterdoor && nextAct == 0) {
            ContinueOntoHeartPatch.heartRoom(new ProceedButton());
        } else {
            if(BehindTheScenesActNum.getActNum() + 1 >= CustomDungeon.THEENDING && afterdoor && nextAct > 0) {
                //Entering a custom act that required the keys removes the keys from your inventory.
                Settings.hasEmeraldKey = false;
                Settings.hasSapphireKey = false;
                Settings.hasRubyKey = false;
            }

            if(options.get(nextAct).ID.equals(Exordium.ID) && AbstractDungeon.floorNum < 1) {
                //Chosen the Exordium itself means it doesn't need to reload the act.
                GenericEventDialog.hide();
                AbstractDungeon.currMapNode.room = new NeowRoom(false);
                AbstractDungeon.currMapNode.room.onPlayerEntry();
            } else {
                if(options.get(nextAct).resetActNum) {
                    BehindTheScenesActNum.resetActNum();
                }
                nextDungeon(options.get(nextAct).ID);
            }
        }
    }

    public static void nextDungeon(String dungeonID) {
        //Set the stage for the next act.
        CardCrawlGame.nextDungeon = dungeonID;

        AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
        if (AbstractDungeon.currMapNode.room instanceof GoToNextDungeonPatch.ForkEventRoom && ((GoToNextDungeonPatch.ForkEventRoom) AbstractDungeon.currMapNode.room).originalRoom != null) {
            AbstractDungeon.currMapNode.room = ((GoToNextDungeonPatch.ForkEventRoom) AbstractDungeon.currMapNode.room).originalRoom;
        }
        GenericEventDialog.hide();

        CardCrawlGame.mode = CardCrawlGame.GameMode.GAMEPLAY;
        AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
        AbstractDungeon.fadeOut();
        AbstractDungeon.isDungeonBeaten = true;
    }

    static class ActOption {
        String ID;
        String text;
        boolean resetActNum;

        public ActOption(String ID, String text) {
            this(ID, text, false);
        }
        public ActOption(String ID, String text, boolean resetActNum) {
            this.ID = ID;
            this.text = text;
            this.resetActNum = resetActNum;
        }
    }
}