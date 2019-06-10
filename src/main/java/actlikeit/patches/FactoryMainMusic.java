package actlikeit.patches;

import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.audio.MainMusic;
import actlikeit.ActLikeIt;

@SpirePatch(clz = MainMusic.class, method = "getSong")
public class FactoryMainMusic {

    @SpirePostfixPatch
    public static Music Postfix(Music __result, MainMusic __instance, String key) {
        ActLikeIt.logger.info("Music patch Main hit");

        switch (key) {
            case "FACTORYMAIN": {
                return MainMusic.newMusic("superResources/audio/music/factory_main.ogg");
            }
            default: {
                return __result;
            }
        }

    }

}