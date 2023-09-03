package electrodynamics.client.screen.tile;

import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.common.inventory.container.tile.ContainerWindmill;
import electrodynamics.common.tile.generators.TileWindmill;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.types.ScreenComponentMultiLabel;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import electrodynamics.prefab.screen.component.utils.AbstractScreenComponentInfo;
import electrodynamics.prefab.utilities.ElectroTextUtils;
import electrodynamics.prefab.utilities.object.TransferPack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenWindmill extends GenericScreen<ContainerWindmill> {

	public ScreenWindmill(ContainerWindmill container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);
		addComponent(new ScreenComponentElectricInfo(-AbstractScreenComponentInfo.SIZE + 1, 2));
		addComponent(new ScreenComponentMultiLabel(0, 0, graphics -> {
			TileWindmill windmill = menu.getHostFromIntArray();
			if (windmill == null) {
				return;
			}
			TransferPack transfer = windmill.getProduced();
			graphics.drawString(font, ElectroTextUtils.gui("machine.current", ChatFormatter.getChatDisplayShort(transfer.getAmps(), DisplayUnit.AMPERE)), inventoryLabelX + 60, inventoryLabelY - 48, 4210752, false);
			graphics.drawString(font, ElectroTextUtils.gui("machine.output", ChatFormatter.getChatDisplayShort(transfer.getWatts(), DisplayUnit.WATT)), inventoryLabelX + 60, inventoryLabelY - 35, 4210752, false);
			graphics.drawString(font, ElectroTextUtils.gui("machine.voltage", ChatFormatter.getChatDisplayShort(transfer.getVoltage(), DisplayUnit.VOLTAGE)), inventoryLabelX + 60, inventoryLabelY - 22, 4210752, false);
		}));
	}

}