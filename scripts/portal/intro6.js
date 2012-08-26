function enter(pi) {
	var qs = parseFloat(pi.getCQInfo(190000));
	if (qs > 3 && qs < 5) {
		//pi.getPlayer().dropMessage(-1, "Take the portal to the right");
		pi.getPlayer().dropMessage(-1, "We must head to this cave and see what is hiding inside!");
	} else {
		pi.getPlayer().dropMessage(-1, "Take the portal to the right.");
	}
}