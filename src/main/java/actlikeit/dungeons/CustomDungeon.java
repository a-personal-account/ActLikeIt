package actlikeit.dungeons;

import basemod.BaseMod;
import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.audio.TempMusic;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.Ironclad;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class CustomDungeon extends AbstractDungeon {
    public static CustomDungeon datasource;

    public String name;
    public String id;

    public int weakpreset;
    public int strongpreset;
    public int elitepreset;
    private boolean genericEvents;
    private AbstractScene savedScene;
    private Color savedFadeColor;

    private String eventImg;

    public CustomDungeon(AbstractScene scene, String NAME, String ID, String eventImg) {
        this(scene, NAME, ID, eventImg, true);
    }
    public CustomDungeon(AbstractScene scene, String NAME, String ID, String eventImg, boolean genericEvents) {
        this(scene, NAME, ID, eventImg, genericEvents, 2, 12, 10);
    }
    public CustomDungeon(AbstractScene scene, String NAME, String ID, String eventImg, boolean genericEvents, int weakpreset, int strongpreset, int elitepreset) {
        super(NAME, ID, AbstractDungeon.player, new ArrayList<>());
        this.id = ID;
        this.name = NAME;
        this.savedScene = scene;
        this.eventImg = eventImg;
        this.genericEvents = genericEvents;
        this.savedFadeColor = Color.valueOf("0f220aff");

        this.weakpreset = weakpreset;
        this.strongpreset = strongpreset;
        this.elitepreset = elitepreset;

        if(AbstractDungeon.actNum > 0) {
            setupMisc(this, AbstractDungeon.actNum);
        }
    }

    public CustomDungeon(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd.name, cd.id, p, emptyList);

        setupMisc(cd, AbstractDungeon.actNum);

        AbstractDungeon.currMapNode = new MapRoomNode(0, -1);
        AbstractDungeon.currMapNode.room = new EmptyRoom();
    }
    public CustomDungeon(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
        super(cd.name, p, saveFile);

        CardCrawlGame.dungeon = this;
        setupMisc(cd, saveFile.act_num);

        miscRng = new com.megacrit.cardcrawl.random.Random(Settings.seed + saveFile.floor_num);
        firstRoomChosen = true;

        populatePathTaken(saveFile);
    }
    private void setupMisc(CustomDungeon cd, int actNum) {
        if (scene != null && scene != cd.savedScene) {
            scene.dispose();
        }
        scene = cd.savedScene;
        fadeColor = cd.savedFadeColor;
        this.name = cd.name;
        initializeLevelSpecificChances();
        mapRng = new com.megacrit.cardcrawl.random.Random(Settings.seed + actNum * 100);
        generateMap();

        if(cd.mainmusic != null) {
            CardCrawlGame.music.changeBGM(cd.id);
        } else {
            switch(actNum) {
                case EXORDIUM:
                    CardCrawlGame.music.changeBGM(Exordium.ID);
                    break;
                case THECITY:
                    CardCrawlGame.music.changeBGM(TheCity.ID);
                    break;
                case THEBEYOND:
                    CardCrawlGame.music.changeBGM(TheBeyond.ID);
                    break;
                case THEENDING:
                    CardCrawlGame.music.changeBGM(TheEnding.ID);
                    break;
            }
        }
    }

    public CustomDungeon fromProgression(AbstractPlayer p) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        datasource = this;
        return this.getClass().getConstructor(CustomDungeon.class, AbstractPlayer.class, ArrayList.class)
                .newInstance(this, p, this.genericEvents  ? AbstractDungeon.specialOneTimeEventList : new ArrayList<>());
    }
    public CustomDungeon fromSaveFile(AbstractPlayer p, SaveFile saveFile) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        datasource = this;
        return this.getClass().getConstructor(CustomDungeon.class, AbstractPlayer.class, SaveFile.class).newInstance(this, p, saveFile);
    }

    @Override
    protected void initializeLevelSpecificChances() {
        shopRoomChance = 0.05F;
        restRoomChance = 0.12F;
        treasureRoomChance = 0.0F;
        eventRoomChance = 0.22F;
        eliteRoomChance = 0.08F;
        smallChestChance = 50;
        mediumChestChance = 33;
        largeChestChance = 17;
        commonRelicChance = 50;
        uncommonRelicChance = 33;
        rareRelicChance = 17;
        colorlessRareChance = 0.3F;
        if (AbstractDungeon.ascensionLevel >= 12) {
            cardUpgradedChance = 0.25F;
        } else {
            cardUpgradedChance = 0.5F;
        }

    }


    @Override
    protected void generateMonsters() {
        // TODO: This is copied from TheCity
        generateWeakEnemies(weakpreset);
        generateStrongEnemies(strongpreset);
        generateElites(elitepreset);
    }

    @Override
    protected void generateWeakEnemies(int count) {
        generateMonstertype(count, Monstertype.Weak);
    }
    @Override
    protected void generateStrongEnemies(int count) {
        generateMonstertype(count, Monstertype.Strong);
    }
    @Override
    protected void generateElites(int count) {
        generateMonstertype(count, Monstertype.Elite);
    }

    protected void generateMonstertype(int count, Monstertype type) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        if(enemies.containsKey(type) && enemies.get(type).containsKey(this.id)) {
            for(final String s : enemies.get(type).get(this.id)) {
                float weight = 1.0F;
                if(weights.containsKey(s)) {
                    weight = weights.get(s);
                }
                monsters.add(new MonsterInfo(s, weight));
                BaseMod.logger.error(s + '(' + weight + ')');
            }
        }

        MonsterInfo.normalizeWeights(monsters);
        if(type == Monstertype.Strong) {
            populateFirstStrongEnemy(monsters, generateExclusions());
        }
        populateMonsterList(monsters, count, type == Monstertype.Elite);
    }

    @Override
    protected ArrayList<String> generateExclusions() {
        // TODO: This is copied from TheCity
        ArrayList<String> retVal = new ArrayList<>();
        /* switch (monsterList.get(monsterList.size() - 1)) {
            case "Spheric Guardian":
                retVal.add("Sentry and Sphere");
                break;
            case "3 Byrds":
                retVal.add("Chosen and Byrds");
                break;
            case "Chosen":
                retVal.add("Chosen and Byrds");
                retVal.add("Cultist and Chosen");
                break;
        } */
        // TODO: THIS IS EXCLUSIVE ENCOUNTERS
        return retVal;
    }

    @Override
    protected void initializeBoss() {
        bossList.clear();
        // Bosses are added via BaseMod in MyAct.receivePostInitialize()
    }

    @Override
    protected void initializeEventList() {
        // Events are added via BaseMod in MyAct.receivePostInitialize()
    }

    @Override
    protected void initializeEventImg() {
        if (eventBackgroundImg != null) {
            eventBackgroundImg.dispose();
            eventBackgroundImg = null;
        }
        eventBackgroundImg = ImageMaster.loadImage(eventImg);
    }



    public static Map<Integer, ArrayList<String>> actnumbers = new HashMap<>();
    public static Map<String, CustomDungeon> dungeons = new HashMap<>();
    public static void addAct(String replaces, CustomDungeon cd) {
        int actReplacement;
        switch (replaces) {
            case Exordium.ID:
                actReplacement = EXORDIUM;
                break;
            case TheCity.ID:
                actReplacement = THECITY;
                break;
            case TheBeyond.ID:
                actReplacement = THEBEYOND;
                break;
            case TheEnding.ID:
                actReplacement = THEENDING;
                break;

            default:
                if(replaces.matches("\\d")) {
                    actReplacement = Integer.parseInt(replaces);
                } else {
                    BaseMod.logger.error("Unable to add act \"" + cd.id + "\".");
                    return;
                }
        }
        addAct(actReplacement, cd);
    }
    public static void addAct(int actReplacement, CustomDungeon cd) {
        if(!dungeons.containsKey(cd.id)) {
            if(!actnumbers.containsKey(actReplacement)) {
                actnumbers.put(actReplacement, new ArrayList<>());
                actnumbers.get(actReplacement).add(cd.id);
            }
            dungeons.put(cd.id, cd);
        } else {
            BaseMod.logger.error("Act \"" + cd.id + "\" already present.");
        }
    }
    public void addAct(int actReplacement) {
        addAct(actReplacement, this);
    }
    public void addAct(String replaces) {
        addAct(replaces, this);
    }


    //enemies[Monstertype][DungeonID] = List<EncounterID>
    public static Map<Monstertype, Map<String, ArrayList<String>>> enemies = new HashMap<>();
    public static Map<String, Float> weights = new HashMap<>();

    public void addMonster(String encounterID, BaseMod.GetMonsterGroup group, Monstertype type) {
        CustomDungeon.addMonster(this.id, encounterID, "", group, type, 1.0F);
    }
    public void addMonster(String encounterID, String name, BaseMod.GetMonsterGroup group, Monstertype type) {
        CustomDungeon.addMonster(this.id, encounterID, name, group, type, 1.0F);
    }
    public void addMonster(String encounterID, BaseMod.GetMonsterGroup group, Monstertype type, float weight) {
        CustomDungeon.addMonster(this.id, encounterID, "", group, type, weight);
    }
    public void addMonster(String encounterID, String name, BaseMod.GetMonsterGroup group, Monstertype type, float weight) {
        CustomDungeon.addMonster(this.id, encounterID, name, group, type, weight);
    }
    public static void addMonster(String dungeon, String encounterID, String name, BaseMod.GetMonsterGroup group, Monstertype type, float weight) {
        Map tmp = enemies;
        if(!tmp.containsKey(type)) {
            tmp.put(type, new HashMap<>());
        }
        tmp = (Map)tmp.get(type);
        if(!tmp.containsKey(dungeon)) {
            tmp.put(dungeon, new ArrayList<>());
        }

        ArrayList<String> encounterIDs = (ArrayList)tmp.get(dungeon);
        encounterIDs.add(encounterID);
        if(weight != 1.0F) {
            weights.put(encounterID, weight);
        }

        if(name.isEmpty()) {
            BaseMod.addMonster(encounterID, group);
        } else {
            BaseMod.addMonster(encounterID, name, group);
        }
    }

    public void addBoss(String bossID, BaseMod.GetMonsterGroup boss, String mapIcon, String mapOutlineIcon) {
        addBoss(this.id, bossID, boss, mapIcon, mapOutlineIcon);
    }
    public static void addBoss(String dungeon, String bossID, BaseMod.GetMonsterGroup boss, String mapIcon, String mapOutlineIcon) {
        BaseMod.addMonster(bossID, boss);
        BaseMod.addBoss(dungeon, bossID, mapIcon, mapOutlineIcon);
    }

    public enum Monstertype {
        Weak,
        Strong,
        Elite
    }

    public static final int EXORDIUM = 1;
    public static final int THECITY = 2;
    public static final int THEBEYOND = 3;
    public static final int THEENDING = 4;



    public String mainmusic = null;
    public static Map<String, String> tempmusic = new HashMap<>();

    public void setMainMusic(String path) {
        mainmusic = path;
    }
    public void addTempMusic(String key, String path) {
        if(tempmusic.containsKey(key)) {
            BaseMod.logger.error("Temp Music key \"" + key + "\" already taken!");
        } else {
            BaseMod.logger.error("Adding Temp Music key: \"" + key + "\"");
            tempmusic.put(key, path);
        }
    }
}
