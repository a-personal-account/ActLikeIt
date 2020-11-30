package actlikeit.RazIntent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class FlashingIntentVFX extends AbstractGameEffect {
    private Texture img;
    private static final int RAW_W = 64;
    private float x;
    private float y;
    private float scale;
    private float scaleVelocity;

    public FlashingIntentVFX(String tex, float x, float y) {
        this(tex, x, y, 2F, 0.5F, 1.1F);
    }
    public FlashingIntentVFX(Texture tex, float x, float y) {
        this(tex, x, y, 2F, 0.5F, 1.1F);
    }
    public FlashingIntentVFX(String tex, float x, float y, float startingDuration, float startingScale, float scaleVelocity) {
        this(AssetLoader.loadImage(tex), x, y, startingDuration, startingScale, scaleVelocity);
    }
    public FlashingIntentVFX(Texture tex, float x, float y, float startingDuration, float startingScale, float scaleVelocity) {
        this.scale = Settings.scale * startingScale * (64F / tex.getWidth());
        this.duration = startingDuration;
        this.x = x;
        this.y = y;
        this.scaleVelocity = scaleVelocity * (64F / tex.getWidth());

        this.img = tex;

        this.renderBehind = false;
        this.color = new Color(1.0F, 1.0F, 1.0F, 0.0F);
    }

    public void update() {
        this.scale += Gdx.graphics.getDeltaTime() * Settings.scale * scaleVelocity;
        if (this.duration > 1.0F) {
            this.color.a = Interpolation.fade.apply(0.0F, 0.3F, 1.0F - (this.duration - 1.0F));
        } else {
            this.color.a = Interpolation.fade.apply(0.3F, 0.0F, 1.0F - this.duration);
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }

    }

    public void render(SpriteBatch sb) {
        sb.setBlendFunction(770, 1);
        sb.setColor(this.color);
        sb.draw(this.img, this.x - img.getWidth() / 2F, this.y - img.getHeight() / 2F, img.getWidth() / 2F, img.getHeight() / 2F, img.getWidth(), img.getHeight(), this.scale, this.scale, 0.0F, 0, 0, img.getWidth(), img.getHeight(), false, false);
        sb.setBlendFunction(770, 771);
    }

    public void dispose() {}
}
