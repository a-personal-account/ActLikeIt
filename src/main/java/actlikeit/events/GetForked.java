package actlikeit.events;

import actlikeit.ActLikeIt;
import actlikeit.dungeons.CustomDungeon;
import actlikeit.patches.GoToNextDungeonPatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;

import java.util.ArrayList;

public class GetForked extends AbstractImageEvent {
    public static final String ID = ActLikeIt.makeID("ForkInTheRoad");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;
    private static final String BASE_IMG = "superResources/images/events/ForkInTheRoad.png";

    public GetForked() {
        super(NAME, DESCRIPTIONS[0], BASE_IMG);
        String actName = null;
        switch(AbstractDungeon.actNum + 1) {
            case CustomDungeon.EXORDIUM:
                actName = Exordium.NAME;
                break;
            case CustomDungeon.THECITY:
                actName = TheCity.NAME;
                break;
            case CustomDungeon.THEBEYOND:
                actName = TheBeyond.NAME;
                break;
            case CustomDungeon.THEENDING:
                actName = TheEnding.NAME;
                break;
        }
        imageEventText.setDialogOption('[' + actName + ']');
        if(CustomDungeon.actnumbers.containsKey(AbstractDungeon.actNum + 1)) {
            for(final String s : CustomDungeon.actnumbers.get(AbstractDungeon.actNum + 1)) {
                imageEventText.setDialogOption('[' + CustomDungeon.dungeons.get(s).name + ']');
            }
        }
        imageEventText.setDialogOption(OPTIONS[0]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        ArrayList<String> possibilities = new ArrayList<>();

        switch(AbstractDungeon.actNum + 1) {
            case CustomDungeon.EXORDIUM:
                possibilities.add(Exordium.ID);
                break;
            case CustomDungeon.THECITY:
                possibilities.add(TheCity.ID);
                break;
            case CustomDungeon.THEBEYOND:
                possibilities.add(TheBeyond.ID);
                break;
            case CustomDungeon.THEENDING:
                possibilities.add(TheEnding.ID);
                break;
        }

        if(CustomDungeon.actnumbers.containsKey(AbstractDungeon.actNum + 1)) {
            for (final String s : CustomDungeon.actnumbers.get(AbstractDungeon.actNum + 1)) {
                possibilities.add(s);
            }
        }

        if(buttonPressed == possibilities.size()) {
            buttonPressed = AbstractDungeon.mapRng.random(possibilities.size() - 1);
        }

        CardCrawlGame.nextDungeon = possibilities.get(buttonPressed);

        AbstractDungeon.rs = AbstractDungeon.RenderScene.NORMAL;
        if (AbstractDungeon.currMapNode.room instanceof GoToNextDungeonPatch.ForkEventRoom) {
            AbstractDungeon.currMapNode.room = ((GoToNextDungeonPatch.ForkEventRoom) AbstractDungeon.currMapNode.room).originalRoom;
        }
        GenericEventDialog.hide();

        AbstractDungeon.fadeOut();
        AbstractDungeon.isDungeonBeaten = true;
    }
}