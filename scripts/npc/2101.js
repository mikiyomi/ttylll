/* Author: Xterminator
	NPC Name: 		Heena
	Map(s): 		Maple Road : Lower level of the Training Camp (2)
	Description: 		Takes you outside of Training Camp
*/
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 0 && mode == 0) {
	cm.sendOk("come back later.");
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	cm.sendYesNo("Do you want to create a legend character?\r\n #r#e ( Please note you may only create a legend character if you are level 1 and beginner. ( explorer classes))");
    } else if (status == 1) {
	cm.sendNext("Then, I will send you out from here. Good job.");
    } else if (status == 2) {
	cm.warp(3, 0);
	cm.dispose();
    }
}