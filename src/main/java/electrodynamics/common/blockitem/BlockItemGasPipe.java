package electrodynamics.common.blockitem;

import java.util.List;

import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.common.block.connect.BlockGasPipe;
import electrodynamics.prefab.utilities.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class BlockItemGasPipe extends BlockItem {

	private final BlockGasPipe pipe;
	
	public BlockItemGasPipe(BlockGasPipe pipe, Properties properties) {
		super(pipe, properties);
		this.pipe = pipe;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltips, TooltipFlag advanced) {
		super.appendHoverText(stack, level, tooltips, advanced);
		tooltips.add(TextUtils.tooltip("pipematerial", pipe.pipe.pipeMaterial.getTranslatedName()).withStyle(ChatFormatting.GRAY));
		//tooltips.add(TextUtils.tooltip("pipeinsulationmaterial", pipe.pipe.insulationMaterial.getTranslatedName()).withStyle(ChatFormatting.GRAY));
		tooltips.add(TextUtils.tooltip("pipemaximumpressure", ChatFormatter.getChatDisplayShort(pipe.pipe.pipeMaterial.maxPressure, DisplayUnit.PRESSURE_ATM)).withStyle(ChatFormatting.GRAY));
		//tooltips.add(TextUtils.tooltip("pipeheatloss", ChatFormatter.getChatDisplayShort(pipe.pipe.effectivePipeHeatLoss, DisplayUnit.TEMPERATURE_KELVIN)).withStyle(ChatFormatting.GRAY));
	}

}
