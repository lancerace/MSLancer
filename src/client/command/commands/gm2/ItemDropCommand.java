/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2019 RonanLana

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

/*
   @Author: Arthur L - Refactored command content into modules
*/
package client.command.commands.gm2;

import client.command.Command;
import client.MapleClient;
import client.MapleCharacter;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import config.YamlConfig;
import constants.inventory.ItemConstants;
import net.server.Server;
import server.MapleItemInformationProvider;

public class ItemDropCommand extends Command {
    {
        setDescription("");
    }

    @Override
    public void execute(MapleClient c, String[] params) {
        MapleCharacter player = c.getPlayer();

        if (params.length < 1) {
            player.yellowMessage("Syntax: !drop <itemid> <quantity>");
            return;
        }

        int itemId = Integer.parseInt(params[0]);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        if (ii.getName(itemId) == null) {
            player.yellowMessage("Item id '" + params[0] + "' does not exist.");
            return;
        }

        short quantity = 1;
        if (params.length >= 2)
            quantity = Short.parseShort(params[1]);

        if (YamlConfig.config.server.BLOCK_GENERATE_CASH_ITEM && ii.isCash(itemId)) {
            player.yellowMessage("You cannot create a cash item with this command.");
            return;
        }

        if (ItemConstants.isExpCoupon(itemId)) {
            Item toDropItem = new Item(itemId, (short) 0, quantity, -1);
            switch (itemId) {
            case 5211048:
            case 5360042: {
                // 4 Hour 2X coupons, the period is 1, but we don't want them to last a day.
                toDropItem.setExpiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 4));
            }
                break;
            case 5211060:
                toDropItem.setExpiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 2));
                break;
            case 5211047:// 3 Hour 2X coupons
            case 5360014:
                toDropItem.setExpiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 3));// 3 Hour 2X coupons
                break;
            default:
                toDropItem.setExpiration(Server.getInstance().getCurrentTime() + (1000 * 60 * 60 * 24));
            }

            toDropItem.setOwner("");
            if (player.gmLevel() < 3) {
                short f = toDropItem.getFlag();
                f |= ItemConstants.ACCOUNT_SHARING;
                f |= ItemConstants.UNTRADEABLE;
                f |= ItemConstants.SANDBOX;
                toDropItem.setFlag(f);
                toDropItem.setOwner("TRIAL-MODE");
            }
            c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDropItem, c.getPlayer().getPosition(),
                    true, true);
            return;
        }

        if (ItemConstants.isPet(itemId)) {
            if (params.length >= 2) { // thanks to istreety & TacoBell
                quantity = 1;
                long days = Math.max(1, Integer.parseInt(params[1]));
                long expiration = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000);
                int petid = MaplePet.createPet(itemId);

                Item toDrop = new Item(itemId, (short) 0, quantity, petid);
                toDrop.setExpiration(expiration);

                toDrop.setOwner("");
                if (player.gmLevel() < 3) {
                    short f = toDrop.getFlag();
                    f |= ItemConstants.ACCOUNT_SHARING;
                    f |= ItemConstants.UNTRADEABLE;
                    f |= ItemConstants.SANDBOX;

                    toDrop.setFlag(f);
                    toDrop.setOwner("TRIAL-MODE");
                }

                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(),
                        true, true);

                return;
            } else {
                player.yellowMessage("Pet Syntax: !drop <itemid> <expiration>");
                return;
            }
        }

        Item toDrop;
        if (ItemConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
            toDrop = ii.getEquipById(itemId);
        } else {
            toDrop = new Item(itemId, (short) 0, quantity);
        }

        toDrop.setOwner(player.getName());
        if (player.gmLevel() < 3) {
            short f = toDrop.getFlag();
            f |= ItemConstants.ACCOUNT_SHARING;
            f |= ItemConstants.UNTRADEABLE;
            f |= ItemConstants.SANDBOX;

            toDrop.setFlag(f);
            toDrop.setOwner("TRIAL-MODE");
        }

        c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true,
                true);
    }
}
