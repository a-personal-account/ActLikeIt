package actlikeit.dungeons;

import actlikeit.ActLikeIt;
import actlikeit.patches.ContinueOntoHeartPatch;
import actlikeit.savefields.BehindTheScenesActNum;
import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomSavable;
import basemod.devcommands.act.ActCommand;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.audio.MainMusic;
import com.megacrit.cardcrawl.audio.MusicMaster;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.shrines.*;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.relics.Circlet;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.EmptyRoom;
import com.megacrit.cardcrawl.rooms.EventRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;
import com.megacrit.cardcrawl.scenes.AbstractScene;
import com.megacrit.cardcrawl.screens.DungeonTransitionScreen;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CustomDungeon extends AbstractDungeon {
    public static CustomDungeon datasource;

    public String name;
    public String id;

    public int weakpreset;
    public int strongpreset;
    public int elitepreset;
    protected boolean genericEvents;
    protected Color savedFadeColor;
    public boolean finalAct;
    protected boolean isEthereal = false;
    public boolean preventFinalActRewards = true;
    private Class<? extends AbstractEvent> onEnter = null;

    public boolean hasEvent() {
        return onEnter != null;
    }

    private String eventImg;

    public CustomDungeon(String NAME, String ID) {
        this(NAME, ID, "images/ui/event/panel.png", true);
    }

    public CustomDungeon(String NAME, String ID, boolean genericEvents) {
        this(NAME, ID, "images/ui/event/panel.png", genericEvents);
    }

    public CustomDungeon(String NAME, String ID, String eventImg) {
        this(NAME, ID, eventImg, true);
    }

    public CustomDungeon(String NAME, String ID, String eventImg, boolean genericEvents) {
        this(NAME, ID, eventImg, genericEvents, 2, 12, 10);
    }

    public CustomDungeon(String NAME, String ID, String eventImg, boolean genericEvents, int weakpreset, int strongpreset, int elitepreset) {
        this();
        this.id = ID;
        this.name = NAME;
        this.eventImg = eventImg;
        this.genericEvents = genericEvents;
        this.savedFadeColor = Color.valueOf("0f220aff");
        this.finalAct = false;

        this.weakpreset = weakpreset;
        this.strongpreset = strongpreset;
        this.elitepreset = elitepreset;

        if (AbstractDungeon.actNum > 0) {
            setupMisc(this, AbstractDungeon.actNum);
        }
    }

    private CustomDungeon() {
        super(null, null, AbstractDungeon.player, new ArrayList<>());
    }

    //Constructor for when you encounter this act through progression.
    public CustomDungeon(CustomDungeon cd, AbstractPlayer p, ArrayList<String> emptyList) {
        super(cd.name, cd.id, p, emptyList);

        setupMisc(cd, BehindTheScenesActNum.getActNum());


        AbstractDungeon.currMapNode = new MapRoomNode(0, -1);

        if (cd.onEnter != null || BehindTheScenesActNum.getActNum() == 1) {
            try {
                if (cd.onEnter == null) {
                    throw new ArithmeticException();
                }
                //For some reason, the hand causes a crash, even though there is no reason for a hand to exist at this point.
                AbstractDungeon.player.hand.group.clear();

                //Set the starting event
                AbstractEvent ae = cd.onEnter.newInstance();

                AbstractDungeon.currMapNode.room = new EventRoom();
                AbstractDungeon.currMapNode.room.event = ae;
                AbstractDungeon.overlayMenu.proceedButton.hide();
                //If you try entering the room, it sets it to a random event, so just call onEnterRoom on the event instead.
                ae.onEnterRoom();
            } catch (Exception ex) {
                if (!(ex instanceof ArithmeticException)) {
                    ex.printStackTrace();
                }
                //Default Neow event.
                AbstractDungeon.currMapNode.room = new NeowRoom(false);
            }
            AbstractDungeon.rs = RenderScene.EVENT;
            AbstractDungeon.screen = CurrentScreen.NONE;
            AbstractDungeon.isScreenUp = false;
            AbstractDungeon.previousScreen = null;
        } else {
            AbstractDungeon.currMapNode.room = new EmptyRoom();
        }
        if (cd.isEthereal) {
            BehindTheScenesActNum.setActNum(BehindTheScenesActNum.getActNum() - 1);
            AbstractDungeon.actNum--;
        }
    }

    //Constructor for when you load this act from a savefile.
    public CustomDungeon(CustomDungeon cd, AbstractPlayer p, SaveFile saveFile) {
        super(cd.name, p, saveFile);

        setupMisc(cd, BehindTheScenesActNum.getActNum());

        miscRng = new com.megacrit.cardcrawl.random.Random(Settings.seed + saveFile.floor_num);
        firstRoomChosen = true;

        populatePathTaken(saveFile);
    }

    private void setupMisc(CustomDungeon cd, int actNum) {
        //Copying data from the instance that was used for initialization.
        if (scene != null) {
            scene.dispose();
        }
        if (AbstractDungeon.lastCombatMetricKey == null) {
            AbstractDungeon.lastCombatMetricKey = "";
        }
        scene = DungeonScene();
        fadeColor = cd.savedFadeColor;
        this.name = cd.name;
        this.preventFinalActRewards = cd.preventFinalActRewards;
        //event bg needs to be set here, because it can't be set when the constructor of AbstractDungeon is executed yet.
        AbstractDungeon.eventBackgroundImg = ImageMaster.loadImage(cd.eventImg);
        initializeLevelSpecificChances();
        mapRng = new com.megacrit.cardcrawl.random.Random(Settings.seed + AbstractDungeon.actNum * 100);
        makeMap();

        ArrayList<MainMusic> tracks = (ArrayList) ReflectionHacks.getPrivate(CardCrawlGame.music, MusicMaster.class, "mainTrack");
        for (final MainMusic t : tracks) {
            t.kill();
        }
        if (cd.mainmusic != null) {
            CardCrawlGame.music.changeBGM(cd.id);
        } else {
            switch (actNum) {
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

    public abstract AbstractScene DungeonScene();

    public void Ending() {
        throw new RuntimeException(ContinueOntoHeartPatch.exmsg);
    }

    private static ArrayList<String> specialOneTimeEventListBackup = null;

    public static void initialize() {
        BaseMod.addSaveField(ActLikeIt.makeID("event_list_backup"), new CustomSavable<ArrayList<String>>() {
            @Override
            public ArrayList<String> onSave() {
                return specialOneTimeEventListBackup;
            }

            @Override
            public void onLoad(ArrayList<String> strings) {
                specialOneTimeEventListBackup = strings;
            }
        });
    }

    //Use of Reflection allows for instantiation, only requiring the 3 simple, mandatory constructors.
    public CustomDungeon fromProgression(AbstractPlayer p) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        datasource = this;
        if (!this.genericEvents) {
            specialOneTimeEventListBackup = AbstractDungeon.specialOneTimeEventList;
            AbstractDungeon.specialOneTimeEventList = new ArrayList<>();
        } else if (specialOneTimeEventListBackup != null) {
            AbstractDungeon.specialOneTimeEventList = specialOneTimeEventListBackup;
            specialOneTimeEventListBackup = null;
        }
        return this.getClass().getConstructor(CustomDungeon.class, AbstractPlayer.class, ArrayList.class)
                .newInstance(this, p, AbstractDungeon.specialOneTimeEventList);
    }

    public CustomDungeon fromSaveFile(AbstractPlayer p, SaveFile saveFile) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        datasource = this;
        return this.getClass().getConstructor(CustomDungeon.class, AbstractPlayer.class, SaveFile.class).newInstance(this, p, saveFile);
    }

    protected void makeMap() {
        generateMap();
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

    //Flag determining if this act requires the 3 keys (if it's at or later than The Ending).
    public void isFinalAct(boolean fin) {
        this.finalAct = fin;
    }

    //Event that is executed at the start of the act.
    public void onEnterEvent(Class<? extends AbstractEvent> event) {
        this.onEnter = event;
    }

    public String getBodyText() {
        return "";
    }

    public String getOptionText() {
        return "";
    }

    public String getAfterSelectText() {
        return "";
    }

    public String getActNumberText() {
        switch (BehindTheScenesActNum.getActNum() + 1) {
            case 1:
            case 2:
            case 3:
                return DungeonTransitionScreen.TEXT[BehindTheScenesActNum.getActNum() * 2 + 2];

            default:
                return DungeonTransitionScreen.TEXT[8];
        }
    }

    @Override
    protected void generateMonsters() {
        generateWeakEnemies(weakpreset);
        generateStrongEnemies(strongpreset);
        generateElites(elitepreset);
    }

    @Override
    protected void generateWeakEnemies(int count) {
        this.handleEnemyList(count, false, false);
    }

    @Override
    protected void generateStrongEnemies(int count) {
        this.handleEnemyList(count, true, false);
    }

    @Override
    protected void generateElites(int count) {
        this.handleEnemyList(count, false, true);
    }

    private void handleEnemyList(int count, boolean strong, boolean elites) {
        ArrayList<MonsterInfo> monsters = new ArrayList<>();
        MonsterInfo.normalizeWeights(monsters);
        if (strong) {
            populateFirstStrongEnemy(monsters, generateExclusions());
        }
        populateMonsterList(monsters, count, elites);
    }

    @Override
    protected ArrayList<String> generateExclusions() {
        ArrayList<String> retVal = new ArrayList<>();
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

    @Override
    protected void initializeShrineList() {
        shrineList.add(GremlinMatchGame.ID);
        shrineList.add(GremlinWheelGame.ID);
        shrineList.add(GoldShrine.ID);
        shrineList.add(Transmogrifier.ID);
        shrineList.add(PurificationShrine.ID);
        shrineList.add(UpgradeShrine.ID);
    }

    //The main datafields this mod uses.
    public static Map<Integer, ArrayList<String>> actnumbers = new HashMap<>();
    public static Map<String, CustomDungeon> dungeons = new HashMap<>();

    //Give this the ID of a basegame act, and your act, and it'll register it.
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
                if (replaces.matches("\\d")) {
                    actReplacement = Integer.parseInt(replaces);
                } else {
                    ActLikeIt.logger.error("Unable to add act \"" + cd.id + "\".");
                    return;
                }
        }
        addAct(actReplacement, cd);
    }

    //Works with just a number, too. Exordium is 1 in this case.
    public static void addAct(int actReplacement, CustomDungeon cd) {
        if (!dungeons.containsKey(cd.id)) {
            if (!actnumbers.containsKey(actReplacement)) {
                actnumbers.put(actReplacement, new ArrayList<>());
            }
            actnumbers.get(actReplacement).add(cd.id);
            dungeons.put(cd.id, cd);
            ActCommand.addAct(cd.id, actReplacement);
        } else {
            ActLikeIt.logger.error("Act \"" + cd.id + "\" already present.");
        }
    }

    //Both the above functions can be called as object methods as well.
    public void addAct(int actReplacement) {
        addAct(actReplacement, this);
    }

    public void addAct(String replaces) {
        addAct(replaces, this);
    }


    public static final int EXORDIUM = 1;
    public static final int THECITY = 2;
    public static final int THEBEYOND = 3;
    public static final int THEENDING = 4;


    //Very simple music functionality.
    public String mainmusic = null;
    public static Map<String, String> tempmusic = new HashMap<>();

    public void setMainMusic(String path) {
        mainmusic = path;
    }

    public void addTempMusic(String key, String path) {
        if (tempmusic.containsKey(key)) {
            ActLikeIt.logger.error("Temp Music key \"" + key + "\" already taken!");
        } else {
            ActLikeIt.logger.info("Adding Temp Music key: \"" + key + "\"");
            tempmusic.put(key, path);
        }
    }

    public static void playTempMusic(String key) {
        CardCrawlGame.music.silenceTempBgmInstantly();
        CardCrawlGame.music.silenceBGM();
        AbstractDungeon.scene.fadeOutAmbiance();
        CardCrawlGame.music.playTempBGM(key);
    }

    public static void playTempMusicInstantly(String key) {
        CardCrawlGame.music.silenceTempBgmInstantly();
        CardCrawlGame.music.silenceBGMInstantly();
        AbstractDungeon.scene.fadeOutAmbiance();
        CardCrawlGame.music.playTempBgmInstantly(key);
    }

    public static void resumeMainMusic() {
        CardCrawlGame.music.silenceTempBgmInstantly();
        AbstractDungeon.scene.fadeInAmbiance();
        CardCrawlGame.music.unsilenceBGM();
    }

    //Call this function in a boss' die(...) function to add a specific relic to it.
    public static void addRelicReward(String relicID) {
        if (AbstractDungeon.player.hasRelic(relicID) && !relicID.equals(Circlet.ID)) {
            relicID = Circlet.ID;
        }

        boolean found = false;
        for (final RewardItem ri : AbstractDungeon.getCurrRoom().rewards) {
            if (ri.type == RewardItem.RewardType.RELIC && ri.relic != null && ri.relic.relicId.equals(relicID)) {
                found = true;
                break;
            }
        }
        if (!found) {
            AbstractDungeon.getCurrRoom().rewards.add(new RewardItem(RelicLibrary.getRelic(relicID).makeCopy()));
        }
    }

    public void allowFinalActRewards() {
        preventFinalActRewards = false;
    }

    //Wrappers that combine BaseMod's addMonster and addEncounter methods.
    //Weak encounters
    public void addMonster(String encounterID, BaseMod.GetMonster monster) {
        addMonster(encounterID, () -> new MonsterGroup(monster.get()));
    }

    public void addMonster(String encounterID, BaseMod.GetMonster monster, float weight) {
        addMonster(encounterID, () -> new MonsterGroup(monster.get()), weight);
    }

    public void addMonster(String encounterID, String encounterName, BaseMod.GetMonster monster) {
        addMonster(encounterID, encounterName, () -> new MonsterGroup(monster.get()));
    }

    public void addMonster(String encounterID, String encounterName, BaseMod.GetMonster monster, float weight) {
        addMonster(encounterID, encounterName, () -> new MonsterGroup(monster.get()), weight);
    }

    public void addMonster(String encounterID, BaseMod.GetMonsterGroup monster) {
        addMonster(encounterID, monster, 1F);
    }

    public void addMonster(String encounterID, BaseMod.GetMonsterGroup monster, float weight) {
        BaseMod.addMonster(encounterID, monster);
        BaseMod.addMonsterEncounter(this.id, new MonsterInfo(encounterID, weight));
    }

    public void addMonster(String encounterID, String encounterName, BaseMod.GetMonsterGroup monster) {
        addMonster(encounterID, encounterName, monster, 1F);
    }

    public void addMonster(String encounterID, String encounterName, BaseMod.GetMonsterGroup monster, float weight) {
        BaseMod.addMonster(encounterID, encounterName, monster);
        BaseMod.addMonsterEncounter(this.id, new MonsterInfo(encounterID, weight));
    }

    //Strong encounters
    public void addStrongMonster(String encounterID, BaseMod.GetMonster monster) {
        addStrongMonster(encounterID, () -> new MonsterGroup(monster.get()));
    }

    public void addStrongMonster(String encounterID, BaseMod.GetMonster monster, float weight) {
        addStrongMonster(encounterID, () -> new MonsterGroup(monster.get()), weight);
    }

    public void addStrongMonster(String encounterID, String encounterName, BaseMod.GetMonster monster) {
        addStrongMonster(encounterID, encounterName, () -> new MonsterGroup(monster.get()));
    }

    public void addStrongMonster(String encounterID, String encounterName, BaseMod.GetMonster monster, float weight) {
        addStrongMonster(encounterID, encounterName, () -> new MonsterGroup(monster.get()), weight);
    }

    public void addStrongMonster(String encounterID, BaseMod.GetMonsterGroup monster) {
        addStrongMonster(encounterID, monster, 1F);
    }

    public void addStrongMonster(String encounterID, BaseMod.GetMonsterGroup monster, float weight) {
        BaseMod.addMonster(encounterID, monster);
        BaseMod.addStrongMonsterEncounter(this.id, new MonsterInfo(encounterID, weight));
    }

    public void addStrongMonster(String encounterID, String encounterName, BaseMod.GetMonsterGroup monster) {
        addStrongMonster(encounterID, encounterName, monster, 1F);
    }

    public void addStrongMonster(String encounterID, String encounterName, BaseMod.GetMonsterGroup monster, float weight) {
        BaseMod.addMonster(encounterID, encounterName, monster);
        BaseMod.addStrongMonsterEncounter(this.id, new MonsterInfo(encounterID, weight));
    }

    //Elite encounters
    public void addEliteEncounter(String encounterID, BaseMod.GetMonster monster) {
        addEliteEncounter(encounterID, () -> new MonsterGroup(monster.get()));
    }

    public void addEliteEncounter(String encounterID, BaseMod.GetMonster monster, float weight) {
        addEliteEncounter(encounterID, () -> new MonsterGroup(monster.get()), weight);
    }

    public void addEliteEncounter(String encounterID, String encounterName, BaseMod.GetMonster monster) {
        addEliteEncounter(encounterID, encounterName, () -> new MonsterGroup(monster.get()));
    }

    public void addEliteEncounter(String encounterID, String encounterName, BaseMod.GetMonster monster, float weight) {
        addEliteEncounter(encounterID, encounterName, () -> new MonsterGroup(monster.get()), weight);
    }

    public void addEliteEncounter(String encounterID, BaseMod.GetMonsterGroup monster) {
        addEliteEncounter(encounterID, monster, 1F);
    }

    public void addEliteEncounter(String encounterID, BaseMod.GetMonsterGroup monster, float weight) {
        BaseMod.addMonster(encounterID, monster);
        BaseMod.addEliteEncounter(this.id, new MonsterInfo(encounterID, weight));
    }

    public void addEliteEncounter(String encounterID, String encounterName, BaseMod.GetMonsterGroup monster) {
        addEliteEncounter(encounterID, encounterName, monster, 1F);
    }

    public void addEliteEncounter(String encounterID, String encounterName, BaseMod.GetMonsterGroup monster, float weight) {
        BaseMod.addMonster(encounterID, encounterName, monster);
        BaseMod.addEliteEncounter(this.id, new MonsterInfo(encounterID, weight));
    }

    //Boss encounters
    public void addBoss(String encounterID, BaseMod.GetMonster monster, String mapIcon, String mapIconOutline) {
        addBoss(encounterID, monster, mapIcon, mapIconOutline, false);
    }

    public void addBoss(String encounterID, BaseMod.GetMonster monster, String mapIcon, String mapIconOutline, boolean needsToDie) {
        addBoss(encounterID, () -> new MonsterGroup(monster.get()), mapIcon, mapIconOutline, needsToDie);
    }

    public void addBoss(String encounterID, BaseMod.GetMonsterGroup monster, String mapIcon, String mapIconOutline) {
        addBoss(encounterID, monster, mapIcon, mapIconOutline, false);
    }

    public void addBoss(String encounterID, BaseMod.GetMonsterGroup monster, String mapIcon, String mapIconOutline, boolean needsToDie) {
        BaseMod.addMonster(encounterID, monster);
        BaseMod.addBoss(this.id, encounterID, mapIcon, mapIconOutline);
        bossIsMandatory(encounterID, needsToDie);
    }

    public void addBoss(String encounterID, String encounterName, BaseMod.GetMonster monster, String mapIcon, String mapIconOutline) {
        addBoss(encounterID, encounterName, monster, mapIcon, mapIconOutline, false);
    }

    public void addBoss(String encounterID, String encounterName, BaseMod.GetMonster monster, String mapIcon, String mapIconOutline, boolean needsToDie) {
        addBoss(encounterID, encounterName, () -> new MonsterGroup(monster.get()), mapIcon, mapIconOutline, needsToDie);
    }

    public void addBoss(String encounterID, String encounterName, BaseMod.GetMonsterGroup monster, String mapIcon, String mapIconOutline) {
        addBoss(encounterID, encounterName, monster, mapIcon, mapIconOutline, false);
    }

    public void addBoss(String encounterID, String encounterName, BaseMod.GetMonsterGroup monster, String mapIcon, String mapIconOutline, boolean needsToDie) {
        BaseMod.addMonster(encounterID, encounterName, monster);
        BaseMod.addBoss(this.id, encounterID, mapIcon, mapIconOutline);
        bossIsMandatory(encounterID, needsToDie);
    }


    public static boolean isForkNecessary() {
        boolean found = (Settings.isEndless && BehindTheScenesActNum.getActNum() >= 3 && CustomDungeon.actnumbers.containsKey(CustomDungeon.EXORDIUM));
        if (!found && CustomDungeon.actnumbers.containsKey(BehindTheScenesActNum.getActNum() + 1)) {
            for (final String s : CustomDungeon.actnumbers.get(BehindTheScenesActNum.getActNum() + 1)) {
                CustomDungeon cd = CustomDungeon.dungeons.get(s);
                if (!cd.finalAct || Settings.isEndless) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    protected List<BossDupeInfo> bossesInOrder = null;

    protected void bossIsMandatory(String ID, boolean needsToDie) {
        if (bossesInOrder == null) {
            bossesInOrder = new ArrayList<>();
        }
        bossesInOrder.add(new BossDupeInfo(ID, needsToDie));
    }

    public List<String> processMandatoryBosses() {
        if (dungeons.containsKey(AbstractDungeon.id)) {
            bossesInOrder = dungeons.get(AbstractDungeon.id).bossesInOrder;
        }
        if (bossesInOrder != null) {
            for (BossDupeInfo info : bossesInOrder) {
                if (!ActLikeIt.encounteredBosses.contains(info.ID)) {
                    List<String> tmp = AbstractDungeon.bossList;
                    AbstractDungeon.bossList = new ArrayList<>();
                    AbstractDungeon.bossList.add(info.ID);
                    ActLikeIt.logger.info("Forcing Boss Encounter: " + info.ID);
                    tmp.remove(info.ID);
                    return tmp;
                }
            }
        }
        ActLikeIt.logger.info("Not forced any Boss Encounter.");
        return null;
    }

    public void bossSeen() {
        bossInteractedWith(false);
    }

    public void bossKilled() {
        bossInteractedWith(true);
    }

    private void bossInteractedWith(boolean needsToDie) {
        if (dungeons.containsKey(AbstractDungeon.id)) {
            bossesInOrder = dungeons.get(AbstractDungeon.id).bossesInOrder;
        }
        if (bossesInOrder != null) {
            for (BossDupeInfo info : bossesInOrder) {
                if (info.needsToDie == needsToDie && info.ID.equals(bossKey)) {
                    ActLikeIt.bossWasEncountered(info.ID);
                    ActLikeIt.logger.info("Marking Boss as encountered: " + info.ID);
                    break;
                }
            }
        }
    }

    public static class BossDupeInfo {
        public boolean needsToDie;
        public String ID;

        public BossDupeInfo(String ID, boolean needsToDie) {
            this.ID = ID;
            this.needsToDie = needsToDie;
        }
    }
}
