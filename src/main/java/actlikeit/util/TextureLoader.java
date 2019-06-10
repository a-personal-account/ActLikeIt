package actlikeit.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.utils.GdxRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class TextureLoader {
    /*
    private static final Logger logger = LogManager.getLogger(TextureLoader.class.getName());
    private static HashMap<String, Texture> textures = new HashMap<>();

    public static Texture getTexture(final String textureString) {
        if (textures.get(textureString) == null) {
            try {
                loadTexture(textureString);
            } catch (GdxRuntimeException e) {
                logger.error("Could not find texture: " + textureString);
                return getTexture("superResources/images/ui/missing_texture.png");
            }
        }
        return textures.get(textureString);
    }

    private static void loadTexture(final String textureString) throws GdxRuntimeException {
        logger.info("MyAct | Loading Texture: " + textureString);
        Texture texture = new Texture(textureString);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        textures.put(textureString, texture);
    }
     */
}
