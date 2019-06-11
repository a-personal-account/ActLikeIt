package actlikeit;

import actlikeit.events.GetForked;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostDungeonInitializeSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.StartActSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.ScoreBonusStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@SpireInitializer
public class ActLikeIt implements
        PostInitializeSubscriber,
        EditStringsSubscriber,
        StartActSubscriber,
        PostDungeonInitializeSubscriber/*,
        CustomSavable<ArrayList<String>>*/ {
    public static final Logger logger = LogManager.getLogger(ActLikeIt.class.getSimpleName());

    public static ArrayList<String> breadCrumbs = new ArrayList<>();

    public static void initialize() {
        BaseMod.subscribe(new ActLikeIt());
    }

    public static String makeID(String id) {
        return "ActLikeIt:" + id;
    }


    private String languageSupport() {
        switch (Settings.language) {
            default:
                return "eng";
        }
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture("superResources/images/Badge.png");
        ModPanel modPanel = new ModPanel();
        BaseMod.registerModBadge(
                badgeTexture, "Act like it", "Razash",
                "Framework for creating custom acts", modPanel);

        //BaseMod.addSaveField("breadCrumbs", this);
        BaseMod.addEvent(GetForked.ID, GetForked.class);
    }

    private void loadLocStrings(String language) {
        BaseMod.loadCustomStringsFile(EventStrings.class, "superResources/localization/eng/events.json");
        BaseMod.loadCustomStringsFile(ScoreBonusStrings.class, "superResources/localization/eng/score_bonuses.json");
    }

    @Override
    public void receiveEditStrings() {
        String language = languageSupport();

        // Load english first to avoid crashing if translation doesn't exist for something
        loadLocStrings("eng");
        loadLocStrings(language);
    }

    @Override
    public void receiveStartAct() {
        BaseMod.logger.info("Adding to breadcrumbs: " + AbstractDungeon.id);
        breadCrumbs.add(AbstractDungeon.id);
    }

    @Override
    public void receivePostDungeonInitialize() {
        BaseMod.logger.info("breadcrumbs cleared!");
        breadCrumbs.clear();
    }

    /*
    @Override
    public ArrayList<String> onSave() {
        logger.info("Saving breadcrumbs ArrayList with size: " + breadCrumbs.size());
        return breadCrumbs;
    }

    @Override
    public void onLoad(ArrayList<String> loadedBoolean) {
        if (loadedBoolean != null) {
            breadCrumbs = loadedBoolean;
        } else {
            breadCrumbs = new ArrayList<>();
        }
        logger.info("Loading breadcrumbs ArrayList with size: " + breadCrumbs.size());
    }
     */

}
