package actlikeit.RazIntent;

import basemod.ReflectionHacks;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class AssetLoader {
    private static AssetManager assets = new AssetManager();

    public static Texture loadImage(String fileName)
    {
        if (!assets.isLoaded(fileName, Texture.class)) {
            TextureLoader.TextureParameter param = new TextureLoader.TextureParameter();
            param.minFilter = Texture.TextureFilter.Linear;
            param.magFilter = Texture.TextureFilter.Linear;
            assets.load(fileName, Texture.class, param);
            try {
                assets.finishLoadingAsset(fileName);
            } catch (GdxRuntimeException e) {
                return null;
            }
        }
        return assets.get(fileName, Texture.class);
    }

    public static TextureAtlas loadAtlas(String fileName)
    {
        if (!assets.isLoaded(fileName, TextureAtlas.class)) {
            assets.load(fileName, TextureAtlas.class);
            assets.finishLoadingAsset(fileName);
        }
        return assets.get(fileName, TextureAtlas.class);
    }

    public static boolean isLoaded(String fileName, Class type) {
        return assets.isLoaded(fileName, type);
    }
    public static boolean isLoading(String fileName) {
        for(final AssetDescriptor assetDesc : (Array<AssetDescriptor>) ReflectionHacks.getPrivate(assets, AssetManager.class, "loadQueue")) {
            if (assetDesc.fileName.equals(fileName)) {
                return true;
            }
        }
        return false;
    }
    public static void loadAtlasAsync(String fileName) {
        if (!assets.isLoaded(fileName, TextureAtlas.class)) {
            assets.load(fileName, TextureAtlas.class);
        }
    }
    public static void unLoad(String fileName) {
        assets.unload(fileName);
    }

    public static void preloadAtlas(String filename) {
        if(!isLoading(filename) && !isLoaded(filename, TextureAtlas.class)) {
            loadAtlasAsync(filename);
        }
    }
}
