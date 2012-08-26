/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 *
 * @author Administrator
 */
public class SkillSwipe {
    
    private int victimid, skillid, skilllevel, slot, category;

    SkillSwipe(int victimid, int skillid, int skilllevel, int category, int slot) {
        this.victimid = victimid;
        this.skillid = skillid;
        this.skilllevel = skilllevel;
        this.category = category;
        this.slot = slot;
    }
    
    public int getVictimId() {
        return victimid;
    }
    
    public int getSkillId() {
        return skillid;
    }
    
    public int getSkillLevel() {
        return skilllevel;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public int getCategory() {
        return category;
    }
}
