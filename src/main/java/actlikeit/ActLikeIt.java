package actlikeit;

import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.ScoreBonusStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@SpireInitializer
public class ActLikeIt implements
        PostInitializeSubscriber,
        EditStringsSubscriber,
        CustomSavable<ArrayList<String>> {
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
            case ZHS:
                return "zhs";
            default:
                return "eng";
        }
    }

    @Override
    public void receivePostInitialize() {
        BaseMod.addSaveField("breadCrumbs", this);
    }

    private void loadLocStrings(String language) {
        BaseMod.loadCustomStringsFile(EventStrings.class, "superResources/localization/eng/events.json");
        BaseMod.loadCustomStringsFile(UIStrings.class, "superResources/localization/eng/ui.json");
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
    public ArrayList<String> onSave() {
        logger.info("Saving wentToTheFactory ArrayList with size: " + breadCrumbs.size());
        return breadCrumbs;
    }

    @Override
    public void onLoad(ArrayList<String> loadedBoolean) {
        if (loadedBoolean != null) {
            breadCrumbs = loadedBoolean;
        } else {
            breadCrumbs = new ArrayList<>();
        }
        logger.info("Loading wentToTheFactory ArrayList with size: " + breadCrumbs.size());
    }

}
