package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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
import java.util.ArrayList;

public class DungeonMapPatches {
    @SpirePatch(
            clz = DungeonMap.class,
            method = "calculateMapSize"
    )
    public static class CalculateMapSize {
        @SpirePrefixPatch
        public static SpireReturn<Float> Prefix(DungeonMap map) {
            if (CustomDungeon.dungeons.containsKey(AbstractDungeon.id)) {
                int count = 1;
                boolean end = false;
                for(final ArrayList<MapRoomNode> list : AbstractDungeon.map) {
                    for(final MapRoomNode mrn : list) {
                        if(mrn.getRoom() instanceof MonsterRoomBoss) {
                            end = true;
                            break;
                        }
                    }
                    if(end) {
                        break;
                    }
                    count++;
                }

                return SpireReturn.Return(Settings.MAP_DST_Y * count - 1380F * Settings.scale);
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
                            m.replace("{ $_ = " + DungeonMapPatches.class.getName() + ".atMapEnd(); }");
                        }
                    }
                }
            });
        }
    }

    public static int atMapEnd() {
        return (CustomDungeon.dungeons.containsKey(AbstractDungeon.id) && (AbstractDungeon.map.size() <= AbstractDungeon.getCurrMapNode().y + 1 || (AbstractDungeon.map.get(AbstractDungeon.getCurrMapNode().y + 1).size() > 3 && ((AbstractDungeon.map.get(AbstractDungeon.getCurrMapNode().y + 1).get(3)).getRoom() instanceof MonsterRoomBoss))) || !CustomDungeon.dungeons.containsKey(AbstractDungeon.id) && AbstractDungeon.getCurrMapNode().y == 14) ? 14 : 13;
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
                ReflectionHacks.setPrivate(__instance, DungeonMapScreen.class, "mapScrollUpperLimit", ((AbstractDungeon.map.size() * -166.666F) + 200) * Settings.scale);
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
