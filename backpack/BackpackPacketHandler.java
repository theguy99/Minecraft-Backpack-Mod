package backpack;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class BackpackPacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player player) {
		if (packet.channel.equals("BackpackRename")) {
			handlePacket(packet, (EntityPlayerMP)player);
		}
	}
	
	private void handlePacket(Packet250CustomPayload packet, EntityPlayerMP entityPlayer) {
		String name = new String(packet.data).trim();
		
		if(entityPlayer.getCurrentEquippedItem() != null) {
			BackpackInventory inv = new BackpackInventory(entityPlayer, entityPlayer.getCurrentEquippedItem());
			if(!inv.hasInventory()) {
				inv.createInventory(name);
			} else {
				inv.loadInventory();
				inv.setInvName(name);
			}
			inv.saveInventory();
		}
	}

}
