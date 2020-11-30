package actlikeit.RazIntent;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

public abstract class FlashingIntent extends CustomIntent {
    protected Texture flashImage;

    public FlashingIntent(AbstractMonster.Intent intent, String header, String display, String tip) {
        this(intent, header, display, tip, tip);
    }
    public FlashingIntent(AbstractMonster.Intent intent, String header, String display, String tip, String toFlash) {
        this(intent, header, display, tip, AssetLoader.loadImage(toFlash));
    }
    public FlashingIntent(AbstractMonster.Intent intent, String header, String display, String tip, Texture toFlash) {
        super(intent, header, display, tip);

        flashImage = toFlash;
    }

    @Override
    public float updateVFXInInterval(AbstractMonster mo, ArrayList<AbstractGameEffect> intentVfx) {
        AbstractGameEffect sb = new FlashingIntentVFX(flashImage, mo.intentHb.cX, mo.intentHb.cY);

        intentVfx.add(sb);

        return 0.2F;
    }
}
