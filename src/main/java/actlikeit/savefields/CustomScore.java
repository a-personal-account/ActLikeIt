package actlikeit.savefields;

import actlikeit.ActLikeIt;
import basemod.BaseMod;
import basemod.abstracts.CustomSavable;

import java.util.ArrayList;

public class CustomScore implements CustomSavable<ArrayList<CustomScore.Entry>> {

    private ArrayList<Entry> customScores = new ArrayList<>();
    private static CustomScore bc;
    public static ArrayList<Entry> getCustomScores() {
        return bc.customScores;
    }

    public static void initialize() {
        BaseMod.addSaveField(ActLikeIt.makeID("customScores"), bc = new CustomScore());
    }


    @Override
    public ArrayList<Entry> onSave() {
        return customScores;
    }

    @Override
    public void onLoad(ArrayList<Entry> loaded) {
        if (loaded != null) {
            customScores = loaded;
        } else {
            customScores = new ArrayList<>();
        }
    }

    public static void add(String ID, String localizedString, int score, boolean stackable) {
        CustomScore.add(ID, localizedString, null, score, stackable);
    }
    public static void add(String ID, String localizedString, String description, int score, boolean stackable) {
        for(final Entry e : bc.customScores) {
            if(e.id.equals(ID)) {
                e.amount++;
                return;
            }
        }
        bc.customScores.add(new Entry(ID, localizedString, description, score, stackable));
    }

    public static class Entry {
        public String id;
        public String localizedString;
        public String description;
        public int amount;
        public int score;
        public boolean stackable;

        public Entry(String id, String localizedString, String description, int score, boolean stackable) {
            this.amount = 1;
            this.id = id;
            this.localizedString = localizedString;
            this.description = description;
            this.score = score;
            this.stackable = stackable;
        }
    }
}
