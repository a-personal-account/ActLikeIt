package actlikeit.savefields;

import actlikeit.ActLikeIt;
import basemod.BaseMod;
import basemod.abstracts.CustomSavable;

public class BehindTheScenesActNum implements CustomSavable<Integer> {

    private int actNum = 0;
    private static BehindTheScenesActNum bc;
    public static int getActNum() {
        return bc.actNum;
    }
    public static void incrementActNum() {
        bc.actNum++;
    }
    public static void resetActNum() {
        bc.actNum = 0;
    }
    public static void setActNum(int actNum) {
        bc.actNum = actNum;
    }

    public static void initialize() {
        BaseMod.addSaveField(ActLikeIt.makeID("actnum"), bc = new BehindTheScenesActNum());
    }


    @Override
    public Integer onSave() {
        BaseMod.logger.info("Saving Actnum: " + bc.actNum);
        return bc.actNum;
    }

    @Override
    public void onLoad(Integer loaded) {
        bc.actNum = loaded;
        BaseMod.logger.info("Loading Actnum: " + bc.actNum);
    }
}
