package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import basemod.BaseMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.TreasureRoomBoss;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;
import java.util.HashMap;

public class GetDungeonPatches {
    // Note: this should at some point be moved to BaseMod
    public static HashMap<String, AbstractDungeonBuilder> customDungeons = new HashMap<>();
    public static HashMap<String, String> nextDungeons = new HashMap<>();

    public static void addDungeon(String id, AbstractDungeonBuilder builder) {
        customDungeons.put(id, builder);
    }

    public static void addNextDungeon(String fromId, String toId) {
        nextDungeons.put(fromId, toId);
    }

    public interface AbstractDungeonBuilder {
        AbstractDungeon build(AbstractPlayer p, ArrayList<String> theList);

        AbstractDungeon build(AbstractPlayer p, SaveFile save);
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class})
    public static class getDungeonThroughProgression {
        public static AbstractDungeon Postfix(AbstractDungeon dungeon, CardCrawlGame self, String key, AbstractPlayer p) {
            if (dungeon == null) {
                CustomDungeon cd = CustomDungeon.dungeons.get(key);
                try {
                    dungeon = cd.fromProgression(p);
                }catch (Exception ex) {
                    BaseMod.logger.error("Failed to instantiate act " + key);
                }
            }
            return dungeon;
        }
    }

    @SpirePatch(clz = CardCrawlGame.class, method = "getDungeon", paramtypez = {String.class, AbstractPlayer.class, SaveFile.class})
    public static class getDungeonThroughSavefile {
        public static AbstractDungeon Postfix(AbstractDungeon dungeon, CardCrawlGame self, String key, AbstractPlayer p, SaveFile save) {
            if (dungeon == null) {
                CustomDungeon cd = CustomDungeon.dungeons.get(key);
                try {
                    dungeon = cd.fromSaveFile(p, save);
                }catch (Exception ex) {
                    BaseMod.logger.error("Failed to instantiate act " + key);
                }
            }
            return dungeon;
        }
    }

    @SpirePatch(clz = TreasureRoomBoss.class, method = "getNextDungeonName", paramtypez = {})
    public static class getNextDungeonName {
        public static SpireReturn<String> Prefix(TreasureRoomBoss self) {
            String next = nextDungeons.get(AbstractDungeon.id);
            if (next != null) {
                return SpireReturn.Return(next);
            } else {
                return SpireReturn.Continue();
            }
        }
    }
}
