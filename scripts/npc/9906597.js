/*
    This file is part of the SeaMS Maple Story Server
*/
/**
-- SeaMs JavaScript --------------------------------------------------------------------------------
    Pikachu - Free Market (9906599)
-- By ---------------------------------------------------------------------------------------------
    Magikarp
-- Version Info -----------------------------------------------------------------------------------
    1.0 - First Version by Magikarp
---------------------------------------------------------------------------------------------------
**/


function start() {
    //vp = cm.getClient().getVotePoints();
    //if(vp == null)
        vp = 0;
    
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode < 0){
        cm.dispose();
        return;
    }

    cm.openShopNPC(9906597);
    cm.dispose();
}