function enter(pi) {
    if (pi.getPlayer().getLevel() < 30) {
        pi.playerMessage(5, "[SeaMS] This portal is blocked as part of features.");
    } else {
        var map = pi.getPlayer().getClient().getChannelServer().getMapFactory().getMap(whatevermapiditwarpedtobefore);
         pi.getPlayer().changeMap(map, 0);
    }
} 