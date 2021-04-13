/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.server.channel.handlers;

import java.util.HashMap;


import config.YamlConfig;
import constants.game.GameConstants;
import client.MapleCharacter;
import client.MapleClient;

import client.processor.action.BuybackProcessor;
import net.AbstractMaplePacketHandler;
import net.server.Server;
import server.maps.FieldLimit;
import server.maps.MapleMap;
import server.maps.MapleMiniDungeonInfo;
import server.maps.MaplePortal;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;


public final class EnterMTSHandler extends AbstractMaplePacketHandler {
    @Override
    public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        
        if(!chr.isAlive() && YamlConfig.config.server.USE_BUYBACK_SYSTEM) {
            BuybackProcessor.processBuyback(c);
            c.announce(MaplePacketCreator.enableActions());
        } else {
            if (!YamlConfig.config.server.USE_MTS) {
                c.announce(MaplePacketCreator.enableActions());
                return;
            }

            if(chr.getEventInstance() != null) {
                c.announce(MaplePacketCreator.serverNotice(5, "Entering Cash Shop or MTS are disabled when registered on an event."));
                c.announce(MaplePacketCreator.enableActions());
                return;
            }
            
            if(MapleMiniDungeonInfo.isDungeonMap(chr.getMapId())) {
                c.announce(MaplePacketCreator.serverNotice(5, "Changing channels or entering Cash Shop or MTS are disabled when inside a Mini-Dungeon."));
                c.announce(MaplePacketCreator.enableActions());
                return;
            }
            
            if (FieldLimit.CANNOTMIGRATE.check(chr.getMap().getFieldLimit())) {
                chr.dropMessage(1, "You can't do it here in this map.");
                c.announce(MaplePacketCreator.enableActions());
                return;
            }

            if (!chr.isAlive()) {
                c.announce(MaplePacketCreator.enableActions());
                return;
            }
            if (chr.getLevel() < 10) {
                c.announce(MaplePacketCreator.blockedMessage2(5));
                c.announce(MaplePacketCreator.enableActions());
                return;
            }

            chr.closePlayerInteractions();
            chr.closePartySearchInteractions();
            
            chr.unregisterChairBuff();
            Server.getInstance().getPlayerBuffStorage().addBuffsToStorage(chr.getId(), chr.getAllBuffs());
            Server.getInstance().getPlayerBuffStorage().addDiseasesToStorage(chr.getId(), chr.getAllDiseases());
            //chr.setAwayFromChannelWorld();
            chr.notifyMapTransferToPartner(-1);
            chr.removeIncomingInvites();
            chr.cancelAllBuffs(true);
            chr.cancelAllDebuffs();
            chr.cancelBuffExpireTask();
            chr.cancelDiseaseExpireTask();
            chr.cancelSkillCooldownTask();
            chr.cancelExpirationTask();

            chr.forfeitExpirableQuests();
            chr.cancelQuestExpirationTask();
            
            c.getChannelServer().removePlayer(chr);
            chr.getMap().removePlayer(c.getPlayer());
            HashMap<String, Integer> gotomaps = new HashMap<>(GameConstants.GOTO_AREAS);
            MapleMap target = c.getChannelServer().getMapFactory().getMap(gotomaps.get("fm"));
            MaplePortal targetPortal = target.getRandomPlayerSpawnpoint();
            chr.setMap(chr.getMap()); //save map
            chr.saveLocationOnWarp();
            chr.saveCharToDB();
            chr.changeMap(target,targetPortal);
        }
    }
}