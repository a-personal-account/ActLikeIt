package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.screens.DungeonMapScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

import java.awt.*;

public class DungeonMapPatches {
    @SpirePatch(
            clz = DungeonMap.class,
            method = "calculateMapSize"
    )
    public static class CalculateMapSize {
        @SpirePrefixPatch
        public static SpireReturn<Float> Prefix(DungeonMap map) {
            if (CustomDungeon.dungeons.containsKey(AbstractDungeon.id)) {
                return SpireReturn.Return(1.6F * Settings.MAP_DST_Y * (CardCrawlGame.dungeon.getMap().size() + 1) - 2820F * Settings.scale);
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch(
            clz = DungeonMap.class,
            method = "update"
    )
    public static class Update {
        @SpireRawPatch
        public static void Raw(CtBehavior ctMethodToPatch) throws CannotCompileException {
            final Point c = new Point(0, 0);
            ctMethodToPatch.instrument(new ExprEditor() {
                public void edit(FieldAccess m) throws CannotCompileException {
                    if (m.getClassName().equals(MapRoomNode.class.getName())
                            && m.getFieldName().equals("y")) {
                        if(c.x++ == 0) {
                            m.replace("{ $_ = (" + CustomDungeon.class.getName() + ".dungeons.containsKey(" + AbstractDungeon.class.getName() + ".id) && " + AbstractDungeon.class.getName() + ".map.size() <= " + AbstractDungeon.class.getName() + ".getCurrMapNode().y + 1 || (((" + MapRoomNode.class.getName() + ")((java.util.ArrayList)" + AbstractDungeon.class.getName() + ".map.get(" + AbstractDungeon.class.getName() + ".getCurrMapNode().y + 1)).get(3)).getRoom() instanceof " + MonsterRoomBoss.class.getName() + ") || !" + CustomDungeon.class.getName() + ".dungeons.containsKey(" + AbstractDungeon.class.getName() + ".id) && " + AbstractDungeon.class.getName() + ".getCurrMapNode().y == 14) ? 14 : 13; }");
                        }
                    }
                }
            });
        }
    }


    @SpirePatch(
            clz = DungeonMapScreen.class,
            method = "open"
    )
    public static class ScrollLimit {
        @SpireInsertPatch(
                locator = Locator.class
        )
        public static void Insert(DungeonMapScreen __instance) {
            if(CustomDungeon.dungeons.containsKey(AbstractDungeon.id)) {
                ReflectionHacks.setPrivate(__instance, DungeonMapScreen.class, "mapScrollUpperLimit", AbstractDungeon.map.size() * -140F);
            }
        }

        private static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "releaseCard");
                return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
            }
        }
    }
}
