function enter(pi) {
	var qs = parseFloat(pi.getCQInfo(190000));
	if (qs > 5) {
		pi.getPlayer().changeMap(100000000, 0);
		pi.openNpc(2144010);
	} else {
		pi.getPlayer().dropMessage(-1, "Let's head in that cave in the middle!");		
	}
}