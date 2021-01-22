package actlikeit;

import actlikeit.dungeons.CustomDungeon;
import actlikeit.savefields.BreadCrumbs;
import actlikeit.savefields.ElitesSlain;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.ScoreBonusStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class ActLikeIt implements
        PostInitializeSubscriber,
        EditStringsSubscriber {
    public static final Logger logger = LogManager.getLogger(ActLikeIt.class.getSimpleName());

    public static void initialize() {
        BaseMod.subscribe(new ActLikeIt());
        BreadCrumbs.initialize();
        ElitesSlain.initialize();
    }

    public static String MOD_ID = "actlikeit";
    public static String makeID(String id) {
        return MOD_ID + ":" + id;
    }

    @Override
    public void receivePostInitialize() {
        Texture badgeTexture = new Texture(MOD_ID + "/images/Badge.png");
        ModPanel modPanel = new ModPanel();
        BaseMod.registerModBadge(
                badgeTexture, "Act like it", "Razash",
                "Framework for creating custom acts", modPanel);

        CustomDungeon.initialize();
    }


    private void loadLocStrings(Settings.GameLanguage language) {
        BaseMod.loadCustomStringsFile(EventStrings.class, ActLikeIt.MOD_ID + "/localization/" + language.name().toLowerCase() + "/events.json");
        BaseMod.loadCustomStringsFile(ScoreBonusStrings.class, ActLikeIt.MOD_ID + "/localization/" + language.name().toLowerCase() + "/score_bonuses.json");
    }
    private Settings.GameLanguage languageSupport() {
        switch (Settings.language) {
            case ZHS:
                return Settings.language;
            default:
                return Settings.GameLanguage.ENG;
        }
    }
    @Override
    public void receiveEditStrings() {
        Settings.GameLanguage language = languageSupport();

        // Load english first to avoid crashing if translation doesn't exist for something. Blatantly stolen from Vex.
        loadLocStrings(Settings.GameLanguage.ENG);
        if(!Settings.language.equals(Settings.GameLanguage.ENG)) {
            try {
                loadLocStrings(language);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
