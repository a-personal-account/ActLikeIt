package actlikeit.patches;

import actlikeit.dungeons.CustomDungeon;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireRawPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import javassist.*;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;

import java.util.ArrayList;


@SpirePatch(
        clz = AbstractDungeon.class,
        method = SpirePatch.CONSTRUCTOR,
        paramtypez = { String.class, String.class, AbstractPlayer.class, ArrayList.class}
)
public class UniqueDungeonConstructorPatch {
    @SpireRawPatch
    public static void Raw(CtBehavior ctBehavior) {
        CtClass ctClass = ctBehavior.getDeclaringClass();
        ClassPool pool = ctClass.getClassPool();
        pool.insertClassPath(new ClassClassPath(CustomDungeon.class));
        try {
            //Add a constructor no one could POSSIBLY patch into.
            ctClass.addConstructor(CtNewConstructor.make("public AbstractDungeon(" + UniqueDungeonConstructorPatch.class.getName() + " dummy) {  }", ctClass));
        } catch(CannotCompileException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }

        try {
            ctClass = pool.get(CustomDungeon.class.getName());
            for (final CtConstructor ct : ctClass.getDeclaredConstructors()) {
                if (ct.getParameterTypes().length == 0) {
                    //Call said constructor in CustomDungeon.
                    ct.instrument(
                        new ExprEditor() {
                            public void edit(ConstructorCall m) throws CannotCompileException {
                                m.replace("super((" + UniqueDungeonConstructorPatch.class.getName() + ") null);");
                            }
                        }
                    );
                    break;
                }
            }

        } catch (NotFoundException | CannotCompileException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }
}