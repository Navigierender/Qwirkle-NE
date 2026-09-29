package quirkle.game.util;

import org.navigierender.rslair.Entity;

public class SimpleSpriteEntity extends Entity {
    public SimpleSpriteEntity(String spriteIdentifier, OriginPresets originPreset) {
        setSprite(spriteIdentifier);
        this.origin = originPreset;
    }
}
