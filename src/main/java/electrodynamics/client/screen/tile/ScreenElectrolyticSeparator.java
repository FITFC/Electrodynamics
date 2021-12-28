package electrodynamics.client.screen.tile;

import electrodynamics.common.inventory.container.tile.ContainerElectrolyticSeparator;
import electrodynamics.common.item.subtype.SubtypeItemUpgrade;
import electrodynamics.common.tile.TileElectrolyticSeparator;
import electrodynamics.prefab.inventory.container.slot.SlotRestricted;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.ScreenComponentElectricInfo;
import electrodynamics.prefab.screen.component.ScreenComponentFluid;
import electrodynamics.prefab.screen.component.ScreenComponentInfo;
import electrodynamics.prefab.screen.component.ScreenComponentProgress;
import electrodynamics.prefab.screen.component.ScreenComponentSlot;
import electrodynamics.prefab.screen.component.ScreenComponentSlot.EnumSlotType;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentProcessor;
import electrodynamics.prefab.tile.components.utils.AbstractFluidHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ScreenElectrolyticSeparator extends GenericScreen<ContainerElectrolyticSeparator> {

	public ScreenElectrolyticSeparator(ContainerElectrolyticSeparator container, Inventory inv, Component titleIn) {
		super(container, inv, titleIn);
		components.add(new ScreenComponentProgress(() -> {
			GenericTile furnace = container.getHostFromIntArray();
			if (furnace != null) {
				ComponentProcessor processor = furnace.getComponent(ComponentType.Processor);
				if (processor.operatingTicks > 0) {
					return Math.min(1.0, processor.operatingTicks / (processor.requiredTicks / 2.0));
				}
			}
			return 0;
		}, this, 38, 30));
		components.add(new ScreenComponentProgress(() -> {
			GenericTile furnace = container.getHostFromIntArray();
			if (furnace != null) {
				ComponentProcessor processor = furnace.getComponent(ComponentType.Processor);
				if (processor.operatingTicks > processor.requiredTicks / 2.0) {
					return Math.min(1.0, (processor.operatingTicks - processor.requiredTicks / 2.0) / (processor.requiredTicks / 2.0));
				}
			}
			return 0;
		}, this, 78, 30));
		components.add(new ScreenComponentFluid(() -> {
			TileElectrolyticSeparator boiler = container.getHostFromIntArray();
			if (boiler != null) {
				return ((AbstractFluidHandler<?>) boiler.getComponent(ComponentType.FluidHandler)).getInputTanks()[0];
			}
			return null;
		}, this, 21, 18));
		components.add(new ScreenComponentFluid(() -> {
			TileElectrolyticSeparator boiler = container.getHostFromIntArray();
			if (boiler != null) {
				return ((AbstractFluidHandler<?>) boiler.getComponent(ComponentType.FluidHandler)).getOutputTanks()[0];
			}
			return null;
		}, this, 62, 18));
		components.add(new ScreenComponentFluid(() -> {
			TileElectrolyticSeparator boiler = container.getHostFromIntArray();
			if (boiler != null) {
				return ((AbstractFluidHandler<?>) boiler.getComponent(ComponentType.FluidHandler)).getOutputTanks()[1];
			}
			return null;
		}, this, 102, 18));
		components.add(new ScreenComponentElectricInfo(this, -ScreenComponentInfo.SIZE + 1, 2).tag("chemicalmixer"));
	}
	
	@Override
	protected ScreenComponentSlot createScreenSlot(Slot slot) {
		return new ScreenComponentSlot(slot instanceof SlotRestricted res
				&& res.mayPlace(new ItemStack(electrodynamics.DeferredRegisters.SUBTYPEITEM_MAPPINGS.get(SubtypeItemUpgrade.basicspeed)))
						? EnumSlotType.SPEED
						: slot instanceof SlotRestricted ? EnumSlotType.LIQUID : EnumSlotType.NORMAL,
				this, slot.x - 1, slot.y - 1);
	}

}
