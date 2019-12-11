package actlikeit;

import actlikeit.savefields.BreadCrumbs;
import actlikeit.savefields.ElitesSlain;
import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class ActLikeIt implements
        PostInitializeSubscriber {
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
    }
}
