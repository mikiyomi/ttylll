function start() {
	var map = cm.getSavedLocation("EVENT");
	if (map > -1 && map != cm.getMapId()) {
		cm.warp(map, 0);
                cm.sendOk("Use these trophy in the reward shop of @coco.");
                cm.gainItem(4000038, 1);
                cm.dispose();
	} else {
    		cm.warp(910000000, 0);
                cm.sendOk("Use these trophy in the reward shop of @coco.");
                cm.gainItem(4000038, 1);
                cm.dispose();
	}
    cm.dispose();
}

function action(mode, type, selection) {
}
