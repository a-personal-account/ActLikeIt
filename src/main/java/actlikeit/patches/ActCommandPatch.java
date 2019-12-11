package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import actlikeit.savefields.BehindTheScenesActNum;
import basemod.devcommands.act.ActCommand;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.dungeons.TheBeyond;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.Map;

@SpirePatch(
        clz = ActCommand.class,
        method = "execute"
)
public class ActCommandPatch {
    @SpireInsertPatch(
            locator = Locator.class
    )

    //Set the fork-event if there's options after the door on this level.
    public static void Insert(ActCommand __instance, String[] tokens, int depth) {
        if(CustomDungeon.dungeons.containsKey(CardCrawlGame.nextDungeon)) {
            boolean found = false;
            for(final Map.Entry<Integer, ArrayList<String>> entry : CustomDungeon.actnumbers.entrySet()) {
                for(final String s : entry.getValue()) {
                    if(s.equals(CardCrawlGame.nextDungeon)) {
                        found = true;
                        BehindTheScenesActNum.setActNum(entry.getKey() - 1);
                        break;
                    }
                }
                if(found) {
                    break;
                }
            }
        } else if(CardCrawlGame.nextDungeon.equals(Exordium.ID)) {
            BehindTheScenesActNum.setActNum(0);
        } else if(CardCrawlGame.nextDungeon.equals(TheCity.ID)) {
            BehindTheScenesActNum.setActNum(1);
        } else if(CardCrawlGame.nextDungeon.equals(TheBeyond.ID)) {
            BehindTheScenesActNum.setActNum(2);
        } else if(CardCrawlGame.nextDungeon.equals(TheEnding.ID)) {
            BehindTheScenesActNum.setActNum(3);
        }
    }


    private static class Locator extends SpireInsertLocator {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
            Matcher finalMatcher = new Matcher.NewExprMatcher(DungeonTransitionScreen.class);
            return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
        }
    }
}
