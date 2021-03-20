package electrodynamics.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;

import electrodynamics.DeferredRegisters;
import electrodynamics.api.tile.components.ComponentType;
import electrodynamics.api.tile.components.type.ComponentFluidHandler;
import electrodynamics.api.utilities.UtilitiesRendering;
import electrodynamics.client.ClientRegister;
import electrodynamics.common.tile.TileCombustionChamber;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class RenderCombustionChamber extends TileEntityRenderer<TileCombustionChamber> {

    public RenderCombustionChamber(TileEntityRendererDispatcher rendererDispatcherIn) {
	super(rendererDispatcherIn);
    }

    @Override
    @Deprecated
    public void render(TileCombustionChamber tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
	    int combinedLightIn, int combinedOverlayIn) {
	matrixStackIn.push();
	float prog = tileEntityIn.<ComponentFluidHandler>getComponent(ComponentType.FluidHandler).getStackFromFluid(DeferredRegisters.fluidEthanol)
		.getAmount() / (float) TileCombustionChamber.TANK_CAPACITY;
	matrixStackIn.translate(0, 1.0 / 16.0 - 2.5 / 16.0 * (1 - prog), 0);
	IBakedModel ibakedmodel = Minecraft.getInstance().getModelManager().getModel(ClientRegister.MODEL_COMBUSTIONCHAMBERETHANOL);
	UtilitiesRendering.prepareRotationalTileModel(tileEntityIn, matrixStackIn);
	if (prog > 0) {
	    matrixStackIn.scale(1, prog / 16.0f * 12f, 1);
	    UtilitiesRendering.renderModel(ibakedmodel, tileEntityIn, RenderType.getSolid(), matrixStackIn, bufferIn, combinedLightIn,
		    combinedOverlayIn);
	}
	matrixStackIn.pop();
    }

}
