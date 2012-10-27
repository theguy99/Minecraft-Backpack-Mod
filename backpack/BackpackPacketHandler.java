package backpack;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class BackpackPacketHandler implements IPacketHandler {
	/**
	 * Handles incoming packets.
	 * 
	 * @param manager
	 *            The network manager.
	 * @param packet
	 *            The incoming packet.
	 * @param player
	 *            The player who sends the packet.
	 */
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		if(packet.channel.equals("BackpackRename")) {
			handlePacket(packet, (EntityPlayer) player);
		}
	}

	/**
	 * Handles the packet if it was in channel "BackpackRename"
	 * 
	 * @param packet
	 *            The packet which was send.
	 * @param entityPlayer
	 *            The player who sends the packet.
	 */
	private void handlePacket(Packet250CustomPayload packet, EntityPlayer entityPlayer) {
		// converts the byte array to a string and trims it
		String name = new String(packet.data).trim();

		if(entityPlayer.getCurrentEquippedItem() != null) {
			ItemStack is = entityPlayer.getCurrentEquippedItem();
			BackpackInventory inv = new BackpackInventory(entityPlayer, is);
			// set new name
			inv.setInvName(name);
			// save the new data
			inv.saveInventory();
		}
	}
}
