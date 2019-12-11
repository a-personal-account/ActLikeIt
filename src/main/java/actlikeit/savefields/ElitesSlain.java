package actlikeit.savefields;

import actlikeit.ActLikeIt;
import actlikeit.dungeons.CustomDungeon;
import basemod.BaseMod;
import basemod.abstracts.CustomSavable;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import java.util.HashMap;
import java.util.Map;

public class ElitesSlain implements CustomSavable<Map<String, ElitesSlain.Entry>> {

    private Map<String, Entry> killedElites = new HashMap<>();
    private static ElitesSlain bc;
    public static Map<String, Entry> getKilledElites() {
        return bc.killedElites;
    }

    public static void initialize() {
        BaseMod.addSaveField(ActLikeIt.makeID("elitesSlain"), bc = new ElitesSlain());
    }


    @Override
    public Map<String, Entry> onSave() {
        BaseMod.logger.info("Saving ElitesSlain Map with size: " + killedElites.size());
        return killedElites;
    }

    @Override
    public void onLoad(Map<String, Entry> loaded) {
        if (loaded != null) {
            killedElites = loaded;
        } else {
            killedElites = new HashMap<>();
        }
        BaseMod.logger.info("Loading ElitesSlain Map with size: " + killedElites.size());
    }

    public static void kill() {
        if(CustomDungeon.dungeons.containsKey(AbstractDungeon.id)) {
            if (!bc.killedElites.containsKey(AbstractDungeon.id)) {
                bc.killedElites.put(AbstractDungeon.id, new ElitesSlain.Entry(BehindTheScenesActNum.getActNum()));
            } else {
                bc.killedElites.get(AbstractDungeon.id).kills++;
            }
        }
    }

    public static void reset() {
        bc.killedElites.clear();
    }

    public static class Entry {
        public int actnum;
        public int kills;
        public Entry(int actnum) {
            this.actnum = actnum;
            kills = 1;
        }
    }
}
