package electrodynamics.prefab.tile.components.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import electrodynamics.common.recipe.ElectrodynamicsRecipe;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.utilities.UtilitiesTiles;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * Contains a single fluid tank capable of holding any fluid, but 
 * can only hold one fluid at a time; specialized case of FluidHandler
 * for fluid storage tiles
 * @author skip999
 */
public class ComponentUniversalFluidHandler extends ComponentFluidHandler{

	@Nullable
	private FluidTank fluidTank;
	private int capacity = 0;
	
	private List<Fluid> validFluids;
	
	public ComponentUniversalFluidHandler(GenericTile source) {
		super(source);
	}
	
	@Override
	public void saveToNBT(CompoundNBT nbt) {
		CompoundNBT tag = new CompoundNBT();
		tag.putString("FluidName", fluidTank.getFluid().getRawFluid().getRegistryName().toString());
		tag.putInt("Amount", fluidTank.getFluid().getAmount());

		if (fluidTank.getFluid().getTag() != null) {
			tag.put("Tag", fluidTank.getFluid().getTag());
		}
		tag.putInt("cap", fluidTank.getCapacity());
		
		nbt.put("fluidtank", tag);
	}
	
	@Override
	public void loadFromNBT(BlockState state, CompoundNBT nbt) {
		CompoundNBT compound = nbt.getCompound("cap");
		FluidStack stack = FluidStack.loadFluidStackFromNBT(compound);
		FluidTank tank = new FluidTank(this.capacity,test -> test.getFluid() == stack.getFluid());
		tank.setFluid(stack);
		this.fluidTank = tank;
	}
	
	@Override
	public ComponentType getType() {
		return ComponentType.UniversalFluidHandler;
	}
	
	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		return fluidTank.getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		return fluidTank.getCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return getValidInputFluids().contains(stack.getFluid()) && fluidTank.isFluidValid(stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		Direction relative = UtilitiesTiles.getRelativeSide(getHolder().hasComponent(ComponentType.Direction)
				? getHolder().<ComponentDirection>getComponent(ComponentType.Direction).getDirection()
				: Direction.UP, lastDirection);
		boolean canFill = inputDirections.contains(lastDirection)
				|| getHolder().hasComponent(ComponentType.Direction) && relativeInputDirections.contains(relative);
		return canFill && getValidInputFluids().contains(resource.getFluid()) ? fluidTank.fill(resource, action) : 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		Direction relative = UtilitiesTiles.getRelativeSide(getHolder().hasComponent(ComponentType.Direction)
				? getHolder().<ComponentDirection>getComponent(ComponentType.Direction).getDirection()
				: Direction.UP, lastDirection);
		boolean canDrain = outputDirections.contains(lastDirection)
				|| getHolder().hasComponent(ComponentType.Direction) && relativeOutputDirections.contains(relative);
		return canDrain ? fluidTank.drain(resource, action) : FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return fluidTank.drain(maxDrain, action);
	}
	
	@Override
	public ComponentUniversalFluidHandler addFluidTank(Fluid fluid, int capacity, boolean isInput) {
		fluidTank = new FluidTank(capacity, test -> test.getFluid() == fluid);
		this.capacity = capacity;
		return this;
	}
	
	@Override
	public FluidStack getFluidInTank(int tank, boolean isInput) {
		return fluidTank.getFluid();
	}
	
	@Override
	public Collection<FluidTank> getInputFluidTanks() {
		Collection<FluidTank> tank = new ArrayList<>();
		tank.add(fluidTank);
		return tank;
	}
	
	@Override
	public Collection<FluidTank> getOutputFluidTanks() {
		return getInputFluidTanks();
	}
	
	@Override
	public int getInputTanks() {
		return getTanks();
	}
	
	@Override
	public int getOutputTanks() {
		return getInputTanks();
	}
	
	
	@Override
	public List<Fluid> getValidInputFluids() {
		return validFluids;
	}
	
	@Override
	public List<Fluid> getValidOutputFluids() {
		return getValidInputFluids();
	}
	
	@Override
	public FluidTank getTankFromFluid(Fluid fluid, boolean isInput) {
		return fluidTank.getFluid().getFluid().isEquivalentTo(fluid.getFluid()) ? fluidTank : new FluidTank(0);
	}
	
	@Override
	public FluidStack getStackFromFluid(Fluid fluid, boolean isInput) {
		return getTankFromFluid(fluid, isInput).getFluid();
	}
	
	@Override
	public void addFluidToTank(FluidStack fluid, boolean isInput) {
		if(isFluidValid(0, fluid)) {
			fluidTank.fill(fluid, FluidAction.EXECUTE);
		}
	}
	
	@Override
	public void drainFluidFromTank(FluidStack fluid, boolean isInput) {
		fluidTank.drain(fluid, FluidAction.EXECUTE);
	}
	
	@Override
	public ComponentUniversalFluidHandler setValidFluids(List<Fluid> fluids) {
		this.validFluids = fluids;
		return this;
	}
	
	@Override
	public <T extends ElectrodynamicsRecipe> ComponentUniversalFluidHandler setAddFluidsValues(Class<T> recipeClass,
			IRecipeType<?> recipeType, int capacity, boolean hasInput, boolean hasOutput) {
		return this;
	}
	
	@Override
	//not needed
	public ComponentUniversalFluidHandler setFluidInTank(FluidStack stack, int tank, boolean isInput) {
		fluidTank.setFluid(stack);
		return this;
	}
	
	@Override
	//not needed
	public void addFluids() {}

}
