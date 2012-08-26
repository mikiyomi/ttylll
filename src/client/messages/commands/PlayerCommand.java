package client.messages.commands;

//import client.MapleInventory;
//import client.MapleInventoryType;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.world.World;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import scripting.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.PokemonBattle;
import server.RankingWorker;
import server.RankingWorker.RankingInformation;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.MaplePortal;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.SavedLocationType;
import tools.FileoutputUtil;
import tools.StringUtil;
import tools.packet.CWvsContext;

/**
 *
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class STR extends DistributeStatCommands {

        public STR() {
            stat = MapleStat.STR;
        }
    }

    public static class DEX extends DistributeStatCommands {

        public DEX() {
            stat = MapleStat.DEX;
        }
    }

    public static class INT extends DistributeStatCommands {

        public INT() {
            stat = MapleStat.INT;
        }
    }

    public static class LUK extends DistributeStatCommands {

        public LUK() {
            stat = MapleStat.LUK;
        }
    }

    public abstract static class DistributeStatCommands extends CommandExecute {

        protected MapleStat stat = null;
        private static int statLim = 999;

        private void setStat(MapleCharacter player, int amount) {
            switch (stat) {
                case STR:
                    player.getStat().setStr((short) amount, player);
                    player.updateSingleStat(MapleStat.STR, player.getStat().getStr());
                    break;
                case DEX:
                    player.getStat().setDex((short) amount, player);
                    player.updateSingleStat(MapleStat.DEX, player.getStat().getDex());
                    break;
                case INT:
                    player.getStat().setInt((short) amount, player);
                    player.updateSingleStat(MapleStat.INT, player.getStat().getInt());
                    break;
                case LUK:
                    player.getStat().setLuk((short) amount, player);
                    player.updateSingleStat(MapleStat.LUK, player.getStat().getLuk());
                    break;
            }
        }

        private int getStat(MapleCharacter player) {
            switch (stat) {
                case STR:
                    return player.getStat().getStr();
                case DEX:
                    return player.getStat().getDex();
                case INT:
                    return player.getStat().getInt();
                case LUK:
                    return player.getStat().getLuk();
                default:
                    throw new RuntimeException(); //Will never happen.
            }
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "Invalid number entered.");
                return 0;
            }
            int change = 0;
            try {
                change = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "Invalid number entered.");
                return 0;
            }
            if (change <= 0) {
                c.getPlayer().dropMessage(5, "You must enter a number greater than 0.");
                return 0;
            }
            if (c.getPlayer().getRemainingAp() < change) {
                c.getPlayer().dropMessage(5, "You don't have enough AP for that.");
                return 0;
            }
            if (getStat(c.getPlayer()) + change > statLim) {
                c.getPlayer().dropMessage(5, "The stat limit is " + statLim + ".");
                return 0;
            }
            setStat(c.getPlayer(), getStat(c.getPlayer()) + change);
            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            c.getPlayer().dropMessage(5, StringUtil.makeEnumHumanReadable(stat.name()) + " has been raised by " + change + ".");
            return 1;
        }
    }

    public static class Mob extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            MapleMonster mob = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.isAlive()) {
                    c.getPlayer().dropMessage(6, "Monster " + mob.toString());
                    break; //only one
                }
            }
            if (mob == null) {
                c.getPlayer().dropMessage(6, "No monster was found.");
            }
            return 1;
        }
    }

    public static class Challenge extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length <= 1) {
                c.getPlayer().dropMessage(6, "@challenge [playername OR accept/decline OR block/unblock]");
                return 0;
            }
            if (c.getPlayer().getBattler(0) == null) {
                c.getPlayer().dropMessage(6, "You have no monsters!");
                return 0;
            }
            if (splitted[1].equalsIgnoreCase("accept")) {
                if (c.getPlayer().getChallenge() > 0) {
                    final MapleCharacter chr = c.getPlayer().getMap().getCharacterById(c.getPlayer().getChallenge());
                    if (chr != null) {
                        if ((c.getPlayer().isInTownMap() || c.getPlayer().isGM() || chr.isInTownMap() || chr.isGM()) && chr.getBattler(0) != null && chr.getChallenge() == c.getPlayer().getId() && chr.getBattle() == null && c.getPlayer().getBattle() == null) {
                            if (c.getPlayer().getPosition().y != chr.getPosition().y) {
                                c.getPlayer().dropMessage(6, "Please be near them.");
                                return 0;
                            } else if (c.getPlayer().getPosition().distance(chr.getPosition()) > 600.0 || c.getPlayer().getPosition().distance(chr.getPosition()) < 400.0) {
                                c.getPlayer().dropMessage(6, "Please be at a moderate distance from them.");
                                return 0;
                            }
                            chr.setChallenge(0);
                            chr.dropMessage(6, c.getPlayer().getName() + " has accepted!");
                            c.getPlayer().setChallenge(0);
                            final PokemonBattle battle = new PokemonBattle(chr, c.getPlayer());
                            chr.setBattle(battle);
                            c.getPlayer().setBattle(battle);
                            battle.initiate();
                        } else {
                            c.getPlayer().dropMessage(6, "You may only use it in towns, or the other character has no monsters, or something failed.");
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "They do not exist in the map.");
                    }
                } else {
                    c.getPlayer().dropMessage(6, "You don't have a challenge.");
                }
            } else if (splitted[1].equalsIgnoreCase("decline")) {
                if (c.getPlayer().getChallenge() > 0) {
                    c.getPlayer().cancelChallenge();
                } else {
                    c.getPlayer().dropMessage(6, "You don't have a challenge.");
                }
            } else if (splitted[1].equalsIgnoreCase("block")) {
                if (c.getPlayer().getChallenge() == 0) {
                    c.getPlayer().setChallenge(-1);
                    c.getPlayer().dropMessage(6, "You have blocked challenges.");
                } else {
                    c.getPlayer().dropMessage(6, "You have a challenge or they are already blocked.");
                }
            } else if (splitted[1].equalsIgnoreCase("unblock")) {
                if (c.getPlayer().getChallenge() < 0) {
                    c.getPlayer().setChallenge(0);
                    c.getPlayer().dropMessage(6, "You have unblocked challenges.");
                } else {
                    c.getPlayer().dropMessage(6, "You didn't block challenges.");
                }
            } else {
                if (c.getPlayer().getChallenge() == 0) {
                    final MapleCharacter chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                    if (chr != null && chr.getMap() == c.getPlayer().getMap() && chr.getId() != c.getPlayer().getId()) {
                        if ((c.getPlayer().isInTownMap() || c.getPlayer().isGM() || chr.isInTownMap() || chr.isGM()) && chr.getBattler(0) != null && chr.getChallenge() == 0 && chr.getBattle() == null && c.getPlayer().getBattle() == null) {
                            chr.setChallenge(c.getPlayer().getId());
                            chr.dropMessage(6, c.getPlayer().getName() + " has challenged you! Type @challenge [accept/decline] to answer!");
                            c.getPlayer().setChallenge(chr.getId());
                            c.getPlayer().dropMessage(6, "Successfully sent the request.");
                        } else {
                            c.getPlayer().dropMessage(6, "You may only use it in towns, or the other character has no monsters, or they have a challenge.");
                        }
                    } else {
                        c.getPlayer().dropMessage(6, splitted[1] + " does not exist in the map.");
                    }
                } else {
                    c.getPlayer().dropMessage(6, "You have a challenge or you have blocked them.");
                }
            }
            return 1;
        }
    }

    public abstract static class OpenNPCCommand extends CommandExecute {

        protected int npc = -1;
        private static int[] npcs = { // Make sure these are in order correctly.
            9270035, // 0
            9010017, // 1
            9000000, // 2
            9000030, //3
            9010000, // 4
            9000085, // 5
            9000018, // 6
            9000017, //7
            1012121, // 8
            9900002, //9
            2159019, //10
            9010038}; //11

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (npc != 6 && npc != 5 && npc != 4 && npc != 3 && npc != 1 && c.getPlayer().getMapId() != 910000000) { //drpcash can use anywhere
                if (c.getPlayer().getLevel() < 10 && c.getPlayer().getJob() != 200) {
                    c.getPlayer().dropMessage(5, "You must be over level 10 to use this command.");
                    return 0;
                }
                if (c.getPlayer().isInBlockedMap()) {
                    c.getPlayer().dropMessage(5, "You may not use this command here.");
                    return 0;
                }
            } else if (npc == 1) {
                if (c.getPlayer().getLevel() < 70) {
                    c.getPlayer().dropMessage(5, "You must be over level 70 to use this command.");
                    return 0;
                }
            }
            if (c.getPlayer().hasBlockedInventory()) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return 0;
            }
            NPCScriptManager.getInstance().start(c, npcs[npc]);
            return 1;
        }
    }

    public static class npc extends xephyr {
        
    }
    public static class xephyr extends OpenNPCCommand {

        public xephyr() {
            npc = 0;
        }
    }

    public static class Event extends OpenNPCCommand {

        public Event() {
            npc = 2;
        }
    }

    public static class CheckDrop extends OpenNPCCommand {

        public CheckDrop() {
            npc = 4;
        }
    }
 public static class Lucia extends OpenNPCCommand {

        public Lucia() {
            npc = 7;
        }
    }
  public static class Collector extends OpenNPCCommand {

        public Collector() {
            npc = 8;
        }
    }
  
  public static class job extends OpenNPCCommand {

        public job() {
            npc = 9;
        }
    }
  
   public static class shop extends OpenNPCCommand {

        public shop() {
            npc = 10;
        }
    }
   
     
   public static class exchange extends OpenNPCCommand {

        public exchange() {
            npc = 11;
        }
   }
   
    public static class Clear extends CommandExecute {

        private static MapleInventoryType[] invs = {
            MapleInventoryType.EQUIP,
            MapleInventoryType.USE,
            MapleInventoryType.SETUP,
            MapleInventoryType.ETC,
            MapleInventoryType.CASH,};

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.length < 2 || player.hasBlockedInventory()) {
                c.getPlayer().dropMessage(5, "@clear <eq/use/setup/etc/cash/all>");
                return 0;
            } else {
                MapleInventoryType type;
                if (splitted[1].equalsIgnoreCase("eq")) {
                    type = MapleInventoryType.EQUIP;
                } else if (splitted[1].equalsIgnoreCase("use")) {
                    type = MapleInventoryType.USE;
                } else if (splitted[1].equalsIgnoreCase("setup")) {
                    type = MapleInventoryType.SETUP;
                } else if (splitted[1].equalsIgnoreCase("etc")) {
                    type = MapleInventoryType.ETC;
                } else if (splitted[1].equalsIgnoreCase("cash")) {
                    type = MapleInventoryType.CASH;
                } else if (splitted[1].equalsIgnoreCase("all")) {
                    type = null;
                } else {
                    c.getPlayer().dropMessage(5, "Invalid. @clear <eq/use/setup/etc/cash/all>");
                    return 0;
                }
                if (type == null) { //All, a bit hacky, but it's okay
                    for (MapleInventoryType t : invs) {
                        type = t;
                        MapleInventory inv = c.getPlayer().getInventory(type);
                        byte start = -1;
                        for (byte i = 0; i < inv.getSlotLimit(); i++) {
                            if (inv.getItem(i) != null) {
                                start = i;
                                break;
                            }
                        }
                        if (start == -1) {
                            c.getPlayer().dropMessage(5, "There are no items in that inventory.");
                            return 0;
                        }
                        int end = 0;
                        for (byte i = start; i < inv.getSlotLimit(); i++) {
                            if (inv.getItem(i) != null) {
                                MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
                            } else {
                                end = i;
                                break;//Break at first empty space.
                            }
                        }
                        c.getPlayer().dropMessage(5, "Cleared " + start + " to " + end + ".");
                    }
                } else {
                    MapleInventory inv = c.getPlayer().getInventory(type);
                    byte start = -1;
                    for (byte i = 0; i < inv.getSlotLimit(); i++) {
                        if (inv.getItem(i) != null) {
                            start = i;
                            break;
                        }
                    }
                    if (start == -1) {
                        c.getPlayer().dropMessage(5, "There are no items in that inventory.");
                        return 0;
                    }
                    byte end = 0;
                    for (byte i = start; i < inv.getSlotLimit(); i++) {
                        if (inv.getItem(i) != null) {
                            MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
                        } else {
                            end = i;
                            break;//Break at first empty space.
                        }
                    }
                    c.getPlayer().dropMessage(5, "Cleared  " + start + " to " + end + ".");
                }
                return 1;
            }
        }
    }
    
public static class joinevent extends CommandExecute {
        @Override
        public int execute(MapleClient c, String[] splitted) {
            if(c.getPlayer().getClient().getChannelServer().eventOn == false) {
             c.getPlayer().dropMessage(6, "There is no open Event available.");
            } else {
                MapleMap EventMap = c.getChannelServer().getMapFactory().getMap(c.getPlayer().getClient().getChannelServer().eventMap);
                MaplePortal EventPortal = EventMap.getPortal(0);
                c.getPlayer().changeMap(EventMap, EventPortal);
                c.getPlayer().dropMessage(6, "Welcome to the Event! Please be quiet and listen to GM's orders or be warped.");
            }
            return 1;
        }
    }
/*public static class GM extends CommandExecute {
        
        @Override
        public int execute(MapleClient c,String[] splitted) {
            if (!c.getPlayer().getCheatTracker().GMSpam(300000, 3)) { // 5 minutes.
            World.Broadcast.broadcastGMMessage(CWvsContext.serverNotice(6,"Channel " + c.getPlayer().getClient().getChannel() + " // " + c.getPlayer().getName() + ": " + StringUtil.joinStringFrom(splitted, 1)));
            } else {
                c.getPlayer().dropMessage(6,"Please wait 5 minutes in between each @gm.");
            }
            return 1;
        }
        }
*///need to add gmcall splited later//
    public static class FM extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            for (int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i) {
                    c.getPlayer().dropMessage(5, "You may not use this command here.");
                    return 0;
                }
            }
            if (c.getPlayer().getLevel() < 10 && c.getPlayer().getJob() != 200) {
                c.getPlayer().dropMessage(5, "Get to level 10 to use this command");
                return 0;
            }
            if (c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000/*
                     * ||
                     * FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())
                     */) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return 0;
            }
            if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return 0;
            }
            c.getPlayer().saveLocation(SavedLocationType.FREE_MARKET, c.getPlayer().getMap().getReturnMap().getId());
            MapleMap map = c.getChannelServer().getMapFactory().getMap(910000000);
            c.getPlayer().changeMap(map, map.getPortal(0));
            return 1;
        }
    }

  /*  public static class Smega extends CommandExecute {
        
         public int execute(MapleClient c, String[] splitted) {
         if (c.getPlayer().haveItem(5072000)) {
                    c.getPlayer().gainItem(5072000, -1);
                    
                    String medal = "";
                   // String prefix = player.getPrefix();
                    MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                    IItem medalItem = player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -49);
                    if (medalItem != null && prefix == null) {// Normal Player withour prefix
                        medal = "<" + ii.getName(medalItem.getItemId()) + "> ";
                    } else if (medalItem == null && prefix != null) {// Normal Player.
                        medal = prefix + " ";
                    } else if (medalItem != null && prefix != null) { // Normal Player with medal.
                        medal = "<" + ii.getName(medalItem.getItemId()) + "> " + prefix + " ";
                    }
                  
                        ChannelServer.getInstance(c.getChannel()).getWorldInterface().broadcastMessage(player.getName(), MaplePacketCreator.serverNotice(3, c.getChannel(), medal + player.getName() + " : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
                  
                } else {
                    c.getPlayer().dropMessage(5,"You have not enough super megaphone(s)");
                    c.getPlayer().dropMessage(-1,"You have not enough super megaphone(s)");
                    return 0;
                }
         }
    }*/ // todo : fix.
    
    public static class Dispose extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.removeClickedNPC();
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(CWvsContext.enableActions());
            return 1;
        }
    }

    public static class ToogleSmega extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().setSmega();
            return 1;
        }
    }

    public static class Rank extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 4) { //job start end
                c.getPlayer().dropMessage(5, "Use @rank [job] [start number] [end number] where start and end are ranks of the players");
                final StringBuilder builder = new StringBuilder("JOBS: ");
                for (String b : RankingWorker.getJobCommands().keySet()) {
                    builder.append(b);
                    builder.append(" ");
                }
                c.getPlayer().dropMessage(5, builder.toString());
            } else {
                int start = 1, end = 20;
                try {
                    start = Integer.parseInt(splitted[2]);
                    end = Integer.parseInt(splitted[3]);
                } catch (NumberFormatException e) {
                    c.getPlayer().dropMessage(5, "You didn't specify start and end number correctly, the default values of 1 and 20 will be used.");
                }
                if (end < start || end - start > 20) {
                    c.getPlayer().dropMessage(5, "End number must be greater, and end number must be within a range of 20 from the start number.");
                } else {
                    final Integer job = RankingWorker.getJobCommand(splitted[1]);
                    if (job == null) {
                        c.getPlayer().dropMessage(5, "Please use @rank to check the job names.");
                    } else {
                        final List<RankingInformation> ranks = RankingWorker.getRankingInfo(job.intValue());
                        if (ranks == null || ranks.size() <= 0) {
                            c.getPlayer().dropMessage(5, "Please try again later.");
                        } else {
                            int num = 0;
                            for (RankingInformation rank : ranks) {
                                if (rank.rank >= start && rank.rank <= end) {
                                    if (num == 0) {
                                        c.getPlayer().dropMessage(6, "Rankings for " + splitted[1] + " - from " + start + " to " + end);
                                        c.getPlayer().dropMessage(6, "--------------------------------------");
                                    }
                                    c.getPlayer().dropMessage(6, rank.toString());
                                    num++;
                                }
                            }
                            if (num == 0) {
                                c.getPlayer().dropMessage(5, "No ranking was returned.");
                            }
                        }
                    }
                }
            }
            return 1;
        }
    }

    public static class Checkme extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getCSPoints(1) + " Cash.");
            c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getPoints() + " Donor Points.");
            c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getVPoints() + " Vote Points.");
            c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getIntNoRecord(GameConstants.BOSS_PQ) + " Boss Party Quest points.");
            c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getItemQuantity(4001165, true) + " Sunshines.");
            c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getItemQuantity(4031039, true) + " Coins");
            c.getPlayer().dropMessage(6, "You currently have " + c.getPlayer().getRemainingAp() + " Remaining AP.");
            c.getPlayer().dropMessage(6, "The time is currently " + FileoutputUtil.CurrentReadable_TimeGMT() + " GMT.");
            return 1;
        }
    }

    public static class FreeSmega extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 9250120);
            return 1;
        }
    }

    public static class buyhammer extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getMeso() >= 10000000) {
                c.getPlayer().gainMeso(-10000000, true);
                MapleInventoryManipulator.addById(c, 4031596, (short) 1, c.getPlayer().getName());
                c.getPlayer().dropMessage(6, "You have lost 10 million mesos.");
                c.getPlayer().dropMessage(6, "You now have a total of  " + c.getPlayer().getItemQuantity(4031596, true) + "Hammers.");
                c.getPlayer().dropMessage(-1, "You now have a total of  " + c.getPlayer().getItemQuantity(4031596, true) + "Hammers.");
                return 1;
            } else {
                c.getPlayer().dropMessage(6, "Not enough mesos.");
                c.getPlayer().dropMessage(-1, "Not enough mesos.");
                return 0;
            }
        }
    }

    public static class ChalkTalk extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (StringUtil.joinStringFrom(splitted, 1).length() <= 40) {
                c.getPlayer().setChalkboard(StringUtil.joinStringFrom(splitted, 1));
                return 1;
            } else {
                c.getPlayer().dropMessage(6, "Your chalkboard must be less than or equal to 40 characters.");
                return 0;
            }
        }
    }

    public static class Go extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
                 for (int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i) {
                    c.getPlayer().dropMessage(5, "You may not use this command here.");
                    return 0;
                }
            }
            if (c.getPlayer().getLevel() < 10 && c.getPlayer().getJob() != 200) {
                c.getPlayer().dropMessage(5, "You must be over level 10 to use this command.");
                return 0;
            }
            if (c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return 0;
            }
            if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                c.getPlayer().dropMessage(5, "You may not use this command here.");
                return 0;
            }
            HashMap<String, MapleMap> maps = new HashMap<String, MapleMap>();
            maps.put("lionheart", c.getChannelServer().getMapFactory().getMap(211060000));
            maps.put("future", c.getChannelServer().getMapFactory().getMap(271000000));
            maps.put("playground", c.getChannelServer().getMapFactory().getMap(922231001));
            maps.put("hangout", c.getChannelServer().getMapFactory().getMap(930010000));
            maps.put("casino", c.getChannelServer().getMapFactory().getMap(100000203));
            maps.put("henesys", c.getChannelServer().getMapFactory().getMap(100000000));
            maps.put("hene", c.getChannelServer().getMapFactory().getMap(100000000));
            maps.put("ellinia", c.getChannelServer().getMapFactory().getMap(101000000));
            maps.put("perion", c.getChannelServer().getMapFactory().getMap(102000000));
            maps.put("kerning", c.getChannelServer().getMapFactory().getMap(103000000));
            maps.put("lith", c.getChannelServer().getMapFactory().getMap(104000000));
            maps.put("sleepywood", c.getChannelServer().getMapFactory().getMap(105040300));
            maps.put("florina", c.getChannelServer().getMapFactory().getMap(110000000));
            maps.put("orbis", c.getChannelServer().getMapFactory().getMap(200000000));
            maps.put("happy", c.getChannelServer().getMapFactory().getMap(209000000));
            maps.put("elnath", c.getChannelServer().getMapFactory().getMap(211000000));
            maps.put("ereve", c.getChannelServer().getMapFactory().getMap(130000000));
            maps.put("ludi", c.getChannelServer().getMapFactory().getMap(220000000));
            maps.put("omega", c.getChannelServer().getMapFactory().getMap(221000000));
            maps.put("korean", c.getChannelServer().getMapFactory().getMap(222000000));
            maps.put("aqua", c.getChannelServer().getMapFactory().getMap(230000000));
            maps.put("leafre", c.getChannelServer().getMapFactory().getMap(240000000));
            maps.put("mulung", c.getChannelServer().getMapFactory().getMap(250000000));
            maps.put("herb", c.getChannelServer().getMapFactory().getMap(251000000));
            maps.put("nlc", c.getChannelServer().getMapFactory().getMap(600000000));
            maps.put("shrine", c.getChannelServer().getMapFactory().getMap(800000000));
            maps.put("showa", c.getChannelServer().getMapFactory().getMap(801000000));
            maps.put("fm", c.getChannelServer().getMapFactory().getMap(910000000));
            maps.put("guild", c.getChannelServer().getMapFactory().getMap(200000301));
            maps.put("fog", c.getChannelServer().getMapFactory().getMap(105040306));
            maps.put("spa", c.getChannelServer().getMapFactory().getMap(809000201));
            maps.put("moon", c.getChannelServer().getMapFactory().getMap(922230002));
            maps.put("fly", c.getChannelServer().getMapFactory().getMap(200090500));
            maps.put("metapod", c.getChannelServer().getMapFactory().getMap(140030000));
            maps.put("house", c.getChannelServer().getMapFactory().getMap(100000001));
            if (splitted.length != 2) {
                StringBuilder builder = new StringBuilder("Syntax: @go <mapname>");
                int i = 0;
                for (String mapss : maps.keySet()) {
                    if (1 % 10 == 0) {// 10 maps per line
                        c.getPlayer().dropMessage(6, builder.toString());
                    } else {
                        builder.append(mapss).append(", ");
                        return 0;
                    }
                }
                c.getPlayer().dropMessage(6, builder.toString());
            } else if (maps.containsKey(splitted[1])) {
                MapleMap map = maps.get(splitted[1]);
                c.getPlayer().changeMap(map, map.getPortal(0));
                return 1;
            } else {
                c.getPlayer().dropMessage(6, "========================================================================");
                c.getPlayer().dropMessage(6, "                ..::| XephyrMS Map Selections |::..                 ");
                c.getPlayer().dropMessage(6, "========================================================================");
                c.getPlayer().dropMessage(6, "| henesys  | house   | ellinia | perion    | kerning | lith      | sleepywood |");
                c.getPlayer().dropMessage(6, "| fog      | orbis   | happy   | elnath    | ereve   | ludi      | omega      |");
                c.getPlayer().dropMessage(6, "| korean   | aqua    | leafre  | mulung    | herb    | nlc       | shrine     |");
                c.getPlayer().dropMessage(6, "| florina  | fly     | spa     | moon      | casino  | lionheart | future");
                return 0;
            }
            maps.clear();
            return 0;
        }
    }

    public static class Commands extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
     c.getPlayer().dropMessage(5, "-------------.::|XephyrMS Commands|::.-------------");
            c.getPlayer().dropMessage(5, "@str, @dex, @int, @luk [amount to add]");
            c.getPlayer().dropMessage(5, "@xephyr [Universal Town Warp / Event NPC]");
            c.getPlayer().dropMessage(5, "@go <map> [Type @go maps for list]");
            c.getPlayer().dropMessage(5, "@job [typeOpens Job Advance NPC]");
            c.getPlayer().dropMessage(5, "@shop [Opens the AIO Shop NPC]");
            c.getPlayer().dropMessage(5, "@exchange [Exchange your Renegade Coins for Huge Rewards!]");
            c.getPlayer().dropMessage(5, "@mob [Information on the closest monster]");
            c.getPlayer().dropMessage(5, "@checkme [Displays various information]");
            c.getPlayer().dropMessage(5, "@fm [Warp to FM// You may also use trade button.]");
            c.getPlayer().dropMessage(5, "@buyhammer [Buy a Wing Hammer for 10 Million Mesos ( Server Currency )");
            c.getPlayer().dropMessage(5, "@chalktalk <text> [Set at chalkboard with <text>]");
            c.getPlayer().dropMessage(5, "@freesmega [Gives you free megaphones :)]");
            c.getPlayer().dropMessage(5, "@togglesmega [Toggle super megaphone on/off]");
            c.getPlayer().dropMessage(5, "@dispose [If you are unable to attack or talk to NPC]");
            c.getPlayer().dropMessage(5, "@clearslot [Cleanup that trash in your inventory]");
            c.getPlayer().dropMessage(5, "@rank [Use @rank for more details]");
            c.getPlayer().dropMessage(5, "@checkdrop [Use @checkdrop for more details]");
         c.getPlayer().dropMessage(5, "---------------------------------------------- ===");
            return 1;
        }
    }
}
