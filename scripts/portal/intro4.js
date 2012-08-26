function enter(pi) {
	var qs = parseFloat(pi.getCQInfo(190000));
	if (qs > 3 && qs < 5) {
		pi.getPlayer().changeMap(240050600, 2);
		pi.openNpc(pi.getC(), 2144010);
	} else {
		pi.getPlayer().dropMessage(-1, "Take the portal to the right.");
	}
}