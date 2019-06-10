package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import com.badlogic.gdx.audio.Music;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.audio.TempMusic;
import actlikeit.ActLikeIt;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

@SpirePatch(
        clz = TempMusic.class,
        method = "getSong")
public class FactoryTempMusic {

    @SpirePostfixPatch
    public static SpireReturn<Music> Prefix(TempMusic __instance, String key) {
        ActLikeIt.logger.info("Music patch Temp hit");
        if(CustomDungeon.dungeons.containsKey(AbstractDungeon.actNum) && CustomDungeon.dungeons.containsKey(AbstractDungeon.id)) {

        }
        switch (key) {
            case "FACTORYELITE": {
                return SpireReturn.Return(MainMusic.newMusic("superResources/audio/music/factory_elite.ogg"));
            }
            case "BOSS_FACTORY": {
                return SpireReturn.Return(MainMusic.newMusic("superResources/audio/music/boss_factory.ogg"));
            }
            default: {
                return SpireReturn.Continue();
            }
        }

    }

}

