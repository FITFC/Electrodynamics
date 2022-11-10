package electrodynamics.prefab.tile.components.generic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import electrodynamics.common.recipe.recipeutils.AbstractFluidRecipe;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.Component;
import electrodynamics.prefab.tile.components.ComponentType;
import electrodynamics.prefab.tile.components.type.ComponentDirection;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.utilities.BlockEntityUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public abstract class AbstractFluidHandler<A extends Component> implements Component, IFluidHandler {

	private GenericTile holder;

	public HashSet<Direction> relativeOutputDirections = new HashSet<>();
	public HashSet<Direction> relativeInputDirections = new HashSet<>();
	public HashSet<Direction> outputDirections = new HashSet<>();
	public HashSet<Direction> inputDirections = new HashSet<>();

	protected RecipeType<AbstractFluidRecipe> recipeType;
	protected int tankCapacity;
	protected boolean hasInput;
	protected boolean hasOutput;

	protected TagKey<Fluid>[] inKeys;
	protected TagKey<Fluid>[] outKeys;
	protected int inTankCount;
	protected int outTankCount;
	protected int inCapacity;
	protected int outCapacity;

	protected AbstractFluidHandler(GenericTile tile) {
		holder(tile);
		if (holder.hasComponent(ComponentType.PacketHandler)) {
			ComponentPacketHandler handler = holder.getComponent(ComponentType.PacketHandler);
			handler.guiPacketWriter(this::writeGuiPacket);
			handler.guiPacketReader(this::readGuiPacket);
		}
	}

	@Override
	public void holder(GenericTile tile) {
		this.holder = tile;
	}

	public GenericTile getHolder() {
		return holder;
	}

	@Override
	public ComponentType getType() {
		return ComponentType.FluidHandler;
	}

	private void writeGuiPacket(CompoundTag nbt) {
		saveToNBT(nbt);
	}

	private void readGuiPacket(CompoundTag nbt) {
		loadFromNBT(nbt);
	}

	public AbstractFluidHandler<A> universalInput() {
		for (Direction dir : Direction.values()) {
			input(dir);
		}
		return this;
	}

	public AbstractFluidHandler<A> input(Direction dir) {
		inputDirections.add(dir);
		return this;
	}

	public AbstractFluidHandler<A> output(Direction dir) {
		outputDirections.add(dir);
		return this;
	}

	public AbstractFluidHandler<A> relativeInput(Direction... dir) {
		relativeInputDirections.addAll(Arrays.asList(dir));
		return this;
	}

	public AbstractFluidHandler<A> relativeOutput(Direction... dir) {
		relativeOutputDirections.addAll(Arrays.asList(dir));
		return this;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, Direction side) {
		if (capability != ForgeCapabilities.ITEM_HANDLER) {
			return false;
		}
		if (side == null || inputDirections.contains(side) || outputDirections.contains(side)) {
			return true;
		}
		Direction dir = getHolder().hasComponent(ComponentType.Direction) ? getHolder().<ComponentDirection>getComponent(ComponentType.Direction).getDirection() : null;
		if (dir != null) {
			return relativeInputDirections.contains(BlockEntityUtils.getRelativeSide(dir, side)) || relativeOutputDirections.contains(BlockEntityUtils.getRelativeSide(dir, side));
		}
		return false;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		return hasCapability(capability, facing) ? (LazyOptional<T>) LazyOptional.of(() -> this) : LazyOptional.empty();
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return resource != null && getValidInputFluids().contains(resource.getFluid()) ? getTankFromFluid(resource.getFluid(), true).fill(resource, action) : 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return resource != null ? getTankFromFluid(resource.getFluid(), false).drain(resource, action) : FluidStack.EMPTY;
	}

	protected abstract void addFluidTank(Fluid fluid, boolean isInput);

	protected abstract void setFluidInTank(FluidStack stack, int tank, boolean isInput);

	public abstract AbstractFluidHandler<A> setManualFluids(int tankCount, boolean isInput, int capacity, Fluid... fluids);

	public abstract AbstractFluidHandler<A> setInputTags(int count, int capacity, TagKey<Fluid>... tags);

	public abstract AbstractFluidHandler<A> setOutputTags(int count, int capacity, TagKey<Fluid>... tags);

	public abstract FluidStack getFluidInTank(int tank, boolean isInput);

	public abstract FluidTank getTankFromFluid(Fluid fluid, boolean isInput);

	public abstract FluidTank[] getInputTanks();

	public abstract FluidTank[] getOutputTanks();

	public abstract List<Fluid> getValidInputFluids();

	public abstract List<Fluid> getValidOutputFluids();

	protected abstract void addValidFluid(Fluid fluid, boolean isInput);

	public abstract int getInputTankCount();

	public abstract int getOutputTankCount();

	public abstract void addFluidToTank(FluidStack fluid, boolean isInput);

	public abstract void drainFluidFromTank(FluidStack fluid, boolean isInput);

	public AbstractFluidHandler<A> setAddFluidsValues(RecipeType<? extends AbstractFluidRecipe> recipeType, int capacity, boolean hasInput, boolean hasOutput) {
		this.hasInput = hasInput;
		this.hasOutput = hasOutput;
		this.recipeType = (RecipeType<AbstractFluidRecipe>) recipeType;
		this.tankCapacity = capacity;
		return this;
	}

	public abstract void addFluids();

	public static Direction[] getDirectionalArray(HashSet<Direction> directionSet) {
		Direction[] directions = new Direction[directionSet.size()];
		List<Direction> list = directionSet.stream().toList();
		for (int i = 0; i < directions.length; i++) {
			directions[i] = list.get(i);
		}
		return directions;
	}

}
