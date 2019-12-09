package actlikeit.events;

import actlikeit.ActLikeIt;
import actlikeit.dungeons.CustomDungeon;
import actlikeit.patches.ContinueOntoHeartPatch;
import actlikeit.patches.GoToNextDungeonPatch;
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

    public GetForked(boolean afterdoor) {
        super(NAME, "", BASE_IMG);
        StringBuilder desc = new StringBuilder();

        this.afterdoor = afterdoor;
        this.extraText = true;

        if(AbstractDungeon.actNum < CustomDungeon.THEENDING || (afterdoor && AbstractDungeon.actNum == CustomDungeon.THEENDING)) {
            //Create a list of all possible acts following the current one.
            String actName = null;
            switch (AbstractDungeon.actNum + 1) {
                case CustomDungeon.EXORDIUM:
                    actName = Exordium.NAME;
                    break;
                case CustomDungeon.THECITY:
                    actName = TheCity.NAME;
                    break;
                case CustomDungeon.THEBEYOND:
                    actName = TheBeyond.NAME;
                    break;

                default:
                    actName = TheEnding.NAME;
                    break;
            }
            imageEventText.setDialogOption(FontHelper.colorString('[' + actName + "] " + OPTIONS[AbstractDungeon.actNum + 1], BasegameStringColor));
            desc.append(FontHelper.colorString(DESCRIPTIONS[AbstractDungeon.actNum], BasegameStringColor) + " NL NL ");
        }

        if(CustomDungeon.actnumbers.containsKey(AbstractDungeon.actNum + 1)) {
            this.addActOption(AbstractDungeon.actNum + 1, desc);
        }
        if(Settings.isEndless && !afterdoor && AbstractDungeon.actNum >= CustomDungeon.THEBEYOND && CustomDungeon.actnumbers.containsKey(CustomDungeon.EXORDIUM)) {
            resetActnum = imageEventText.optionList.size();
            imageEventText.setDialogOption(FontHelper.colorString('[' + Exordium.NAME +  "] " + OPTIONS[OPTIONS.length - 1], BasegameStringColor));
            desc.append(FontHelper.colorString(DESCRIPTIONS[DESCRIPTIONS.length - 1], BasegameStringColor) + " NL NL ");
            this.addActOption(CustomDungeon.EXORDIUM, desc);
        } else {
            resetActnum = Integer.MAX_VALUE;
        }
        imageEventText.setDialogOption(OPTIONS[0]);
        this.body = desc.toString();
    }

    private void addActOption(int actnum, StringBuilder desc) {
        for(final String s : CustomDungeon.actnumbers.get(actnum)) {
            CustomDungeon cd = CustomDungeon.dungeons.get(s);
            imageEventText.setDialogOption('[' + cd.name + "] " + cd.getOptionText());
            if(!cd.getBodyText().isEmpty()) {
                desc.append(cd.getBodyText() + " NL NL ");
            }
        }
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        ArrayList<String> possibilities = new ArrayList<>();

        switch (AbstractDungeon.actNum + 1) {
            case CustomDungeon.EXORDIUM:
                possibilities.add(Exordium.ID);
                break;
            case CustomDungeon.THECITY:
                possibilities.add(TheCity.ID);
                break;
            case CustomDungeon.THEBEYOND:
                possibilities.add(TheBeyond.ID);
                break;

            default:
                if(!Settings.isEndless) {
                    possibilities.add(TheEnding.ID);
                }
                break;
        }

        if (CustomDungeon.actnumbers.containsKey(AbstractDungeon.actNum + 1)) {
            for (final String s : CustomDungeon.actnumbers.get(AbstractDungeon.actNum + 1)) {
                possibilities.add(s);
            }
        }

        if(extraText) {
            //Chose the option "random act plx"
            if (buttonPressed == possibilities.size()) {
                if (AbstractDungeon.actNum + 1 >= CustomDungeon.THEENDING && !afterdoor) {
                    //Approaching the heart is not among the random options.
                    buttonPressed = AbstractDungeon.mapRng.random(possibilities.size() - 2) + 1;
                } else {
                    buttonPressed = AbstractDungeon.mapRng.random(possibilities.size() - 1);
                }
            }
            nextAct = buttonPressed;
        }

        if(extraText && CustomDungeon.dungeons.containsKey(possibilities.get(buttonPressed))
                && !CustomDungeon.dungeons.get(possibilities.get(buttonPressed)).getAfterSelectText().isEmpty()) {
            extraText = false;
            imageEventText.clearAllDialogs();
            imageEventText.setDialogOption(OPTIONS[OPTIONS.length - 2]);
            imageEventText.updateBodyText(CustomDungeon.dungeons.get(possibilities.get(buttonPressed)).getAfterSelectText());
        } else if(!Settings.isEndless && AbstractDungeon.actNum + 1 >= CustomDungeon.THEENDING && !afterdoor && nextAct == 0) {
            ContinueOntoHeartPatch.heartRoom(new ProceedButton());
        } else {
            if(AbstractDungeon.actNum + 1 >= CustomDungeon.THEENDING && afterdoor && nextAct > 0) {
                //Entering a custom act that required the keys removes the keys from your inventory.
                Settings.hasEmeraldKey = false;
                Settings.hasSapphireKey = false;
                Settings.hasRubyKey = false;
            }

            if(!Settings.isEndless && possibilities.get(nextAct) == Exordium.ID && AbstractDungeon.floorNum < 1) {
                //Chosen the Exordium itself means it doesn't need to reload the act.
                GenericEventDialog.hide();
                AbstractDungeon.currMapNode.room = new NeowRoom(false);
                AbstractDungeon.currMapNode.room.onPlayerEntry();
            } else {
                if(nextAct >= resetActnum) {
                    AbstractDungeon.actNum = 0;
                }
                nextDungeon(possibilities.get(nextAct));
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
}