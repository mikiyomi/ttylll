/** 
 * @credits : Oliver
 * Thank you Oliver, you're the best coder ev4r
 * @credits2: Matt / ID Collection
 **/

importPackage(net.sf.odinms.server);

var status = 0;

var pagemax = 15;

var equipnames = new Array("Accessories", "Caps", "Capes", "Tops", "Gloves", "Overalls", "Pants", "Rings & Effects", 
"Shoes", "Weapons & Shields", "Skill Books","Shoulders");
//"Search By Name!","Necklaces", "Search By ID!","Shields");

var acessories = new Array(1032096,1032097,1032102,1032103,1032104,1032105,1032073,1032077,1032078,1032079,1032052,1032055,1022121,1022122,1012253,1012286,
						   1012239,1012284,1012252,1012240,1022129,1022129,1022082,1032108);
var caps = new Array(1003186,1003187,1003268,1003269,1003459,1003010,1003078,1003249,1003250,1003251,1003252,1003253,1003254,1003255,1003256,1003264,1003265,
					 1003392,1003404,1003482,1003483,1003484,1003219,1001070,1001071);
var capes = new Array(1102267,1102310,1102243,1102242,1102240,1102373,1102380,1102325,1102326,1102349,1102301,1102214,1102224,1102229,1102184,1102196,1102095,
					  1102096,1102206);//1102373
var tops = new Array(1042215,1042216,1042217,1042230,1042218,1042219,1042152);
var gloves = new Array(1081006,1082407,1082408,1082329,1082423);
var overalls = new Array(1052224,1052439,1052446,1052447,1052408,1052417,1052418,1052425,1052426,1052210,1052211,1052213,1052228,1051131);
var pants = new Array(1062137,1062138,1062139,1062145,1062147,1062148);
var rings = new Array(1112586,1112593,1112597,1112663,1112665,1112666,1112135,1112238,1112116,5010082,5010083,5010031,5010032);
var shoes = new Array(1072407,1072517,1072426,1072278,1072238,1072344);
var weapons = new Array(1702342,1412062,1312062,1442111,1322090,1302147,1092074,1402090,1422063,1432081,1332120,1332125,1092084,1342033,1472117,1382099,1092079,
						1372078,1452106,1462091,1702136);//);
var skillbooks = new Array(2280003,2290096,2290125,2290292,2290302,2290032,2290309,2280010,2290084, 2290024,2290307);
var shoulders = new Array(1152046,1152047,1152048,1152049);

var equiptype = "";

var arrayofitems = new Array();
var selected = 0;
var option = 0;
var optionarrays = new Array(9999, 5, 9999, 800);
var itemid = 0;
var blue = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.sendOk("Come back later");
        cm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            cm.sendOk("Come back later.");
            cm.dispose();
            return;
        }
        if (status == 0) {
           // cm.sendYesNo("Hey there, #b#h ##k, I am #rCoco#k from #rOliver's Farm#k.\r\n \r\nI have the awesome ability of #e#nselling basically every equipment in MapleStory#n for some Renegade Coins. My rates are as follows.\r\n \r\n#bBuy any item with #eno stats#k#n - #r1#k Trophy each!\r\n#bBuy any item with #enormal stats#k#n - #r2#k Renegade Coins each.\r\n#bBuy any item with #eyour tag#k#n - #r5#k Renegade Coins each!\r\n \r\n#eWant to take a looksie?#n");
            cm.sendYesNo("Hey there, #b#h ##k, I am #rLucia#k.\r\n\r\nI have the awesome #e#nand rare items for sale in here in #rXephyrMS#k for Renegade Coins#n. My rates are as follows;\r\n \r\n#bBuy any item with #enormal stats#k#n - #r5#k Renegade Coins each.\r\n\r\n#eWant to take a looksie?#n");//\r\n #bBuy any item with #eyour tag#k#n - #r3#k Renegade Coins each.\r\n\r\n#eWant to take a looksie?#n");
        } else if (status == 1 || selection == 10000) {
            status = 1;
            var selStr = "#bWhatcha want?";
    for (var i = 0; i < equipnames.length; i++) {
                if (i == 14) {
                    selStr += "\r\n";
                }
                selStr += "\r\n#L" + i + "##b" + equipnames[i] + "#k#l";
            }
            cm.sendSimple(selStr);
        } else if (status == 2 || selection == 10001) {
            status = 2;
            if (selection < 14 || selection == 10001) {
                if (selection < 14) {
                    blue = 1;
                    equiptype = equipnames[selection];
                    
                    switch (selection) {
                        case 0:
                            arrayofitems = acessories; 
                            break;
                        // case 3: arrayofitems = necklace; 
                        // break;
                        case 1:
                            arrayofitems = caps; 
                            break;
                        case 2:
                            arrayofitems = capes; 
                            break;
                        case 3:
                            arrayofitems = tops; 
                            break;
                        case 4:
                            arrayofitems = gloves; 
                            break;
                        case 5:
                            arrayofitems = overalls; 
                            break;
                        case 6:
                            arrayofitems = pants; 
                            break;
                        case 7:
                            arrayofitems = rings; 
                            break;
                        //case 10: arrayofitems = shields; 
                        // break;
                        case 8:
                            arrayofitems = shoes; 
                            break;
                        case 9:
                            arrayofitems = weapons; 
                            break;
						case 10:
						arrayofitems = skillbooks;
                            break;
                        case 11:
							arrayofitems = shoulders;
							break;
                       /* case 12:
                            arrayofitems = effects; 
                            break;*/
                    }
                }

                var selections = Math.floor((arrayofitems.length - 1)/ pagemax) + 1;

                var selStr2 = "#ePlease select which page of " + equiptype + " you wish to purchase.#n\r\nThere are exactly " + arrayofitems.length + " items to choose from.\r\n";
                for (var i2 = 1; i2 <= selections; i2++) {
                    selStr2 += "\r\n#b#L" + i2 + "# " + equiptype + " Page " + i2 + "#l";
                }
                selStr2 += "\r\n\r\n#L10000##rGo back to start page#k#l";
                cm.sendSimple(selStr2);
            } else if (selection == 14) {
                blue = 2;
                status = 199;
                cm.sendGetText("#ePlease enter your search query on the box below.#n");
                return;
            } else if (selection == 15) {
                blue = 2;
                equiptype = "";
                status = 299;
                cm.sendGetText("You can search for item ID's by looking them up, or asking someone else for the ItemID.\r\n\r\n#ePlease enter the itemid of the item you want below.#n");
                return;
            } else {
                status = 99;
                cm.dispose();
                cm.sendOk("This function is arriving shortly.");
            }
        } else if (status == 3 || selection == 10002 || selection == 10003 || selection == 10005) {
            status = 3;
            if (selection == 10002) {
                selected++;
            } else if (selection == 10005) {
                selected--;
            } else if (selection != 10003) {
                selected = selection - 1;
            }
            var selStr3 = "#ePlease select an item to purchase (" + equiptype + " Page " + (selected + 1) + ")#n";
            for (var i3 = selected * pagemax; i3 < (selected * pagemax) + pagemax && i3 < arrayofitems.length; i3++) {
                selStr3 += "\r\n\r\n#L" + i3 + "##v" + arrayofitems[i3] + "# - #b#t" + arrayofitems[i3] + "##k";
                // selStr3 += "Base Weapon Attack: " + getWeaponAttack(arrayofitems[i3]) + ".";
                selStr3 += "#l";
            }
            if ((selected + 1) != (Math.floor((arrayofitems.length - 1)/ pagemax) + 1)) {
                selStr3 += "\r\n\r\n#L10002##rSee next page#k#l";
            }
            if (selected > 0) {
                selStr3 += "\r\n#L10005##rSee previous page#k#l";
            }
            selStr3 += "\r\n\r\n#L10000##rGo back to start page#k#l";
            cm.sendSimple(selStr3);
        } else if (status == 4) {
            itemid = arrayofitems[selection];
            cm.sendSimple("#eYou have chosen the following item:#n #b#t" + itemid + "##k (#v" + itemid + "#)\r\n\r\nNow please choose the stat type of the item you wish to recieve...\r\n"
                // + "\r\n#L0##bZero Stats#k - #r" + optionarrays[0] + " Renegade Coins Each#k#l"
                + "\r\n#L1##bNormal Stats#k - #r" + optionarrays[1] + " Renegade Coins Each#k#l"
                + "\r\n#L3##bNormal Stats#k - #r" + optionarrays[3] + " Donation Points Each.#k#l");
       // + "\r\n#L2##bTagged Item#k - #r" + optionarrays[2] + " Renegade Coins Each#k#l"); // Add after Restart, should work
        } else if (status == 5) {
            option = selection;
            cm.sendYesNo("So are you sure you wish to order the following...\r\n\r\n"
                + "#eItem:#n #t" + itemid + "# (#v" + itemid + "#)\r\n"
                + "#eWeapon Statistics and Cost:#n #b" + (option == 0 ? "Zero Stats" : option == 1 ? "Default Stats" : option == 3 ? "Default Stats" : "Tagged As Your name") + "#k - #r" + optionarrays[option] + (option == 3 ? " Donation Points" : " Renegade Coins") + "#e#k\r\n\r\nMake sure you have enough space in your inventory before proceeding.");
        } else if (status == 6) {
            //if (cm.haveItem(4310034, optionarrays[option]) && cm.checkSpace(itemid, 1))
            if (cm.haveItem(4310034, optionarrays[option]) && option < 3) {
                // if (!cm.isBannedItem(itemid)) {
                var selStr4 = "Do you wish to shop again?\r\n\r\n#b#L10000#Yes, from the start page.#l";
                if (blue == 1) {
                    selStr4 += "\r\n#L10001#Yes, from the " + equiptype + " Selection of Pages#l\r\n#L10003#Yes, from the page I was just on.#l";
                }
                selStr4 += "\r\n#L1000000#No thanks.#l";
                cm.sendSimple(selStr4);
                switch (option) {
                    // case 0: cm.gainZeroItem(itemid); 
                    // break;
                    case 0:
					
                cm.gainItem(4310034, -optionarrays[option]);
                        cm.gainItem(itemid, 1); 
                        break;
                    case 1:
					
                cm.gainItem(4310034, -optionarrays[option]);
                        cm.gainItem(itemid, 1); 
                        break;
                /*case 2: 
                cm.gainItem(4310034, -optionarrays[option]);
				cm.gainTaggedItem(itemid, cm.getPlayer().getName());*/
                break;
                }
            /// } else {
            //     cm.dispose();
            //     cm.sendOk("You are not #epermitted to obtain a #b#t" + itemid + "##k.")
            //}
            } else if  (cm.getPlayer().getPoints() >= optionarrays[option] && option == 3) {
                // if (!cm.isBannedItem(itemid)) {
                var selStr4 = "Do you wish to shop again?\r\n\r\n#b#L10000#Yes, from the start page.#l";
                if (blue == 1) {
                    selStr4 += "\r\n#L10001#Yes, from the " + equiptype + " Selection of Pages#l\r\n#L10003#Yes, from the page I was just on.#l";
                }
                selStr4 += "\r\n#L1000000#No thanks.#l";
                cm.sendSimple(selStr4);
                switch (option) {
                    // case 0: cm.gainZeroItem(itemid); 
                    // break;
                        case 3:	
                cm.getPlayer().gainPoints(-optionarrays[option]);
                //cm.sendNext("You current have " + cm.getPlayer().getPoints() + "Donation Points after purchasing a #t"+itemid+"# for "+ optionarrays[option] + " DP.")
                        cm.getPlayer().dropMessage(1, "You current have " + cm.getPlayer().getPoints() + "Donation Points after purchasing an IOC for "+ optionarrays[option] + " DP.");
                        cm.gainItem(itemid, 1); 
                        break;
                /*case 2: 
                cm.gainItem(4310034, -optionarrays[option]);
				cm.gainTaggedItem(itemid, cm.getPlayer().getName());*/
                break;
                }
            } else {
                cm.dispose();
                cm.sendOk("You either #edo not have " + optionarrays[option] + " " + (option == 3 ? " Donation Points" : " Renegade Coins") + "#n, or #edo not have enough inventory space for a #b#t" + itemid + "##k.#n");
            }
        } else if (status == 200) {
            var allitems = cm.searchItemResulats(cm.getText());
            var allitemsarrayed = allitems.toArray();
            var mattmadethis = "I have found " + allitemsarrayed.length + " results for the search '" + cm.getText() + "'\r\n";
            for (var i4 = 0; i4 < allitemsarrayed.length; i4++)
                mattmadethis += "\r\n#L"+i+"##t" + allitemsarrayed[i4] + "\r\n";
            cm.sendSimple(mattmadethis);
        } else if (status == 300) {
            status = 4;
            itemid = Math.round(cm.getText());
            cm.sendSimple("#eYou have chosen the following item:#n #b#t" + itemid + "##k (#v" + itemid + "#)\r\n\r\nDo you want to randomize stats for this item?\r\n"
                + "\r\n#L10000##bSure! (This costs 5 Renegade Coins)"
                + "\r\n#L10001##Nah, I'm good. Just give me the item!");
        } else if (status == 7) {
            cm.voteMSG();
        } else if (status == 201) {
            var oppurtunity = selection;
            cm.sendSimple("#eYou have chosen the following item:#n #b#t" + oppurtunity + "##k (#v" + oppurtunity + "#)\r\n\r\nDo you want to randomize stats for this item?\r\n"
                + "\r\n#L10001##Nah, I'm good. Just give me the item!");
        } else if (selection == 10000) {
            if (cm.haveItem(4310034, 1)) {
                cm.gainItem(cm.getPlayer().itemsearched, 1);
                cm.sendOk("Awesome! Have fun!");
            } else {
                cm.sendOk("PENIS!");
            }
        } else if (selection == 10001) {
            cm.gainItem(allitemsarrayed[selection], 1);
            cm.sendOk("Awesome! Have fun!");
        } else {
            cm.sendOk("Cya then!");
        }
    }
}


function getWeaponAttack(equip) {
    var ii = MapleItemInformationProvider.getInstance();
    ii.getWatkForProjectile(equip);
}

function getEquipStat(i) {
    switch (i) {
        case 0:
            return equip.getStr();
        case 1:
            return equip.getDex();
        case 2:
            return equip.getInt();
        case 3:
            return equip.getLuk();
        case 4:
            return equip.getHp();
        case 5:
            return equip.getMp();
        case 6:
            return equip.getWatk();
        case 7:
            return equip.getMatk();
        case 8:
            return equip.getWdef();
        case 9:
            return equip.getMdef();
        case 10:
            return equip.getAcc();
        case 11:
            return equip.getAvoid();
        case 12:
            return equip.getSpeed();
        case 13:
            return equip.getJump();
        case 14:
            return equip.getUpgradeSlots();
    //case 15: return equip.getOwner() == "" ? "(none)" : equip.getOwner();;
    }
}  
