/*
    This file is part of the SeaMS Maple Story Server
*/
/**
-- SeaMs JavaScript --------------------------------------------------------------------------------
    Abra - Free Market (9906598)
-- By ---------------------------------------------------------------------------------------------
    Magikarp
-- Version Info -----------------------------------------------------------------------------------
    1.0 - First Version by Magikarp
---------------------------------------------------------------------------------------------------
**/

var status = 0, lvl = 0, cost = 0 ,selected=null;
//50k,500k,2m,4m
var pricing = [{ level: 30, price: 50000 }, { level: 70, price: 500000 },
{ level: 120, price: 2000000 }, { level: 255, price: 4000000 }, { level: 999, price: 40000000 }]; //last element just a dummy for condition to work.

function calculateTravelCost() {
    lvl = cm.getPlayer().getLevel();

    for (var i = 0; i < pricing.length; i++)

        if (lvl <= pricing[i].level) {   
            if(i===0)
                cost = pricing[i].price;
            else
                cost = pricing[i - 1].price;
            return;
        }

}


function start() {
    //status = -1;
    cm.sendNext("Hi #h #! I am Abra the teleporter. I charge heavily on lazy bum like you. Do you want to travel?");
    //action(1, 0, 0);  
}

function action(mode, type, selection) {
    //if (mode === 1) //everytime user click, increase status.
    status++;

    if (mode === -1) {
        cm.dispose();
        return;
    }

    if (mode === 0) {
        cm.sendOk("Find me again when you feel to travel");
        cm.dispose();
        return;
    }

    if (status === 1) {
        calculateTravelCost();
        cm.sendSimple("Where would you like to go? Each travel cost #b" + cost + "#k mesos. " +
            "#b\r\n#L0# Orbis\r\n#L1# El Nath " +
            "\r\n#L2# Henesys\r\n#L3# New Leaf City " +
            "\r\n#L4# Ludi\r\n#L5# Magatia " +
            "\r\n#L6# Leafre"); return;
    }

    if (status === 2) {
        selected = selection;
       
        if (cm.getMeso() < cost) {
            cm.sendOk("You don't have enough mesos");
            cm.dispose();
            return;
        }
        cm.sendNext("Abra used teleport!");
        cm.gainMeso(-cost);
    }

    if (status === 3){
        switch (selected) {
            case 0: cm.warp(200000000);
                break;
            case 1: cm.warp(211000000);
                break;
            case 2: cm.warp(100000000);
                break;
            case 3: cm.warp(600000000);
                break;
            case 4: cm.warp(220000000);
                break;
            case 5: cm.warp(261000000);
                break;
            case 6: cm.warp(240000000);
                break;
        }

    }

}
