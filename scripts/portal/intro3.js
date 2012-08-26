function enter(pi) {
	if (!pi.getCQInfo(190000) == "2") {
		pi.getPlayer().dropMessage(-1, "Not yet! We must kill some mobs first.");
	} else {
		pi.getPlayer().changeMap(3000300, 0);
		pi.openNpc(pi.getC(), 2144010);
	}
}