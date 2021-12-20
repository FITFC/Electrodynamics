package electrodynamics.compatibility.jei.recipecategories.modfurnace;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;

import electrodynamics.compatibility.jei.utils.gui.ScreenObjectWrapper;
import electrodynamics.compatibility.jei.utils.gui.arrows.animated.ArrowAnimatedWrapper;
import electrodynamics.compatibility.jei.utils.gui.backgroud.BackgroundWrapper;
import electrodynamics.compatibility.jei.utils.gui.item.GenericItemSlotWrapper;
import electrodynamics.compatibility.jei.utils.label.GenericLabelWrapper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public abstract class ModFurnaceRecipeCategory implements IRecipeCategory<SmeltingRecipe> {

	private GenericLabelWrapper[] LABELS;

	private LoadingCache<Integer, List<IDrawableAnimated>> ANIMATED_ARROWS;
	private LoadingCache<Integer, List<IDrawableStatic>> STATIC_ARROWS;
	private LoadingCache<Integer, List<IDrawableStatic>> INPUT_SLOTS;
	private LoadingCache<Integer, List<IDrawableStatic>> OUTPUT_SLOTS;

	private GenericItemSlotWrapper[] inSlots = new GenericItemSlotWrapper[0];
	private GenericItemSlotWrapper[] outSlots = new GenericItemSlotWrapper[0];
	private ArrowAnimatedWrapper[] animArrows = new ArrowAnimatedWrapper[0];
	private ScreenObjectWrapper[] staticArrows = new ScreenObjectWrapper[0];

	private int ANIMATION_LENGTH;

	private String RECIPE_GROUP;
	private String MOD_ID;

	private IDrawable BACKGROUND;
	private IDrawable ICON;

	public ModFurnaceRecipeCategory(IGuiHelper guiHelper, String modID, String recipeGroup, ItemStack inputMachine, BackgroundWrapper wrapper,
			int animTime) {

		ANIMATION_LENGTH = animTime;

		RECIPE_GROUP = recipeGroup;
		MOD_ID = modID;

		ICON = guiHelper.createDrawableIngredient(inputMachine);
		BACKGROUND = guiHelper.createDrawable(new ResourceLocation(modID, wrapper.getTexture()), wrapper.getTextX(), wrapper.getTextY(),
				wrapper.getLength(), wrapper.getWidth());
	}

	@Override
	public Class<? extends SmeltingRecipe> getRecipeClass() {
		return SmeltingRecipe.class;
	}

	@Override
	public Component getTitle() {
		return new TranslatableComponent("gui.jei.category." + RECIPE_GROUP);
	}

	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}

	@Override
	public IDrawable getIcon() {
		return ICON;
	}

	@Override
	public void setIngredients(SmeltingRecipe recipe, IIngredients ingredients) {
		ingredients.setInputLists(VanillaTypes.ITEM, getItemInputs(recipe));
		ingredients.setOutputs(VanillaTypes.ITEM, getItemOutputs(recipe));
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, SmeltingRecipe recipe, IIngredients ingredients) {

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		GenericItemSlotWrapper wrapper;
		for (int i = 0; i < inSlots.length; i++) {
			wrapper = inSlots[i];
			guiItemStacks.init(i, true, wrapper.itemXStart(), wrapper.itemYStart());
		}

		int offset = inSlots.length;

		for (int i = 0; i < outSlots.length; i++) {
			wrapper = outSlots[i];
			guiItemStacks.init(i + offset, false, wrapper.itemXStart(), wrapper.itemYStart());
		}

		guiItemStacks.set(ingredients);
	}

	@Override
	public void draw(SmeltingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
		List<IDrawableStatic> inputSlots = INPUT_SLOTS.getUnchecked(ANIMATION_LENGTH);
		IDrawableStatic image;
		ScreenObjectWrapper wrapper;
		for (int i = 0; i < inputSlots.size(); i++) {
			image = inputSlots.get(i);
			wrapper = inSlots[i];
			image.draw(matrixStack, wrapper.getXPos(), wrapper.getYPos());
		}

		List<IDrawableStatic> outputSlots = OUTPUT_SLOTS.getUnchecked(ANIMATION_LENGTH);
		for (int i = 0; i < outputSlots.size(); i++) {
			image = outputSlots.get(i);
			wrapper = outSlots[i];
			image.draw(matrixStack, wrapper.getXPos(), wrapper.getYPos());
		}

		List<IDrawableStatic> staticArrows = STATIC_ARROWS.getUnchecked(ANIMATION_LENGTH);
		for (int i = 0; i < staticArrows.size(); i++) {
			image = staticArrows.get(i);
			wrapper = this.staticArrows[i];
			image.draw(matrixStack, wrapper.getXPos(), wrapper.getYPos());
		}
		List<IDrawableAnimated> arrows = ANIMATED_ARROWS.getUnchecked(ANIMATION_LENGTH);
		IDrawableAnimated arrow;
		for (int i = 0; i < arrows.size(); i++) {
			arrow = arrows.get(i);
			wrapper = animArrows[i];
			arrow.draw(matrixStack, wrapper.getXPos(), wrapper.getYPos());
		}

		addDescriptions(matrixStack);
	}

	// in case we decide to do something wacky with a furnace
	public abstract List<List<ItemStack>> getItemInputs(SmeltingRecipe recipe);

	public abstract List<ItemStack> getItemOutputs(SmeltingRecipe recipe);

	public void addDescriptions(PoseStack stack) {
		Font fontRenderer = Minecraft.getInstance().font;
		TranslatableComponent text;
		for (GenericLabelWrapper wrap : LABELS) {
			text = new TranslatableComponent("gui.jei.category." + RECIPE_GROUP + ".info." + wrap.getName(), ANIMATION_LENGTH / 20);
			fontRenderer.draw(stack, text, wrap.getXPos(), wrap.getYPos(), wrap.getColor());
		}
	}

	public void setInputSlots(IGuiHelper guiHelper, GenericItemSlotWrapper... inputSlots) {
		inSlots = inputSlots;
		INPUT_SLOTS = CacheBuilder.newBuilder().maximumSize(inputSlots.length).build(new CacheLoader<Integer, List<IDrawableStatic>>() {
			@Override
			public List<IDrawableStatic> load(Integer time) {
				List<IDrawableStatic> slots = new ArrayList<>();
				for (ScreenObjectWrapper slot : inputSlots) {
					slots.add(guiHelper.drawableBuilder(new ResourceLocation(MOD_ID, slot.getTexture()), slot.getTextX(), slot.getTextY(),
							slot.getLength(), slot.getWidth()).build());
				}
				return slots;
			}
		});
	}

	public void setOutputSlots(IGuiHelper guiHelper, GenericItemSlotWrapper... outputSlots) {
		outSlots = outputSlots;
		OUTPUT_SLOTS = CacheBuilder.newBuilder().maximumSize(outputSlots.length).build(new CacheLoader<Integer, List<IDrawableStatic>>() {
			@Override
			public List<IDrawableStatic> load(Integer time) {
				List<IDrawableStatic> slots = new ArrayList<>();
				for (ScreenObjectWrapper slot : outputSlots) {
					slots.add(guiHelper.drawableBuilder(new ResourceLocation(MOD_ID, slot.getTexture()), slot.getTextX(), slot.getTextY(),
							slot.getLength(), slot.getWidth()).build());
				}
				return slots;
			}
		});
	}

	public void setStaticArrows(IGuiHelper guiHelper, ScreenObjectWrapper... arrows) {
		if (staticArrows.length == 0) {
			staticArrows = arrows;
		} else {
			staticArrows = ArrayUtils.addAll(staticArrows, arrows);
		}
		STATIC_ARROWS = CacheBuilder.newBuilder().maximumSize(staticArrows.length).build(new CacheLoader<Integer, List<IDrawableStatic>>() {
			@Override
			public List<IDrawableStatic> load(Integer time) {
				List<IDrawableStatic> arrows = new ArrayList<>();
				for (ScreenObjectWrapper arrow : staticArrows) {
					arrows.add(guiHelper.drawableBuilder(new ResourceLocation(MOD_ID, arrow.getTexture()), arrow.getTextX(), arrow.getTextY(),
							arrow.getLength(), arrow.getWidth()).build());
				}
				return arrows;
			}
		});
	}

	public void setAnimatedArrows(IGuiHelper guiHelper, ArrowAnimatedWrapper... arrows) {
		ScreenObjectWrapper[] temp = new ScreenObjectWrapper[arrows.length];
		for (int i = 0; i < arrows.length; i++) {
			temp[i] = arrows[i].getStaticArrow();
		}
		setStaticArrows(guiHelper, temp);
		animArrows = arrows;
		ANIMATED_ARROWS = CacheBuilder.newBuilder().maximumSize(animArrows.length).build(new CacheLoader<Integer, List<IDrawableAnimated>>() {
			@Override
			public List<IDrawableAnimated> load(Integer time) {
				List<IDrawableAnimated> arrows = new ArrayList<>();
				for (ArrowAnimatedWrapper arrow : animArrows) {
					arrows.add(guiHelper.drawableBuilder(new ResourceLocation(MOD_ID, arrow.getTexture()), arrow.getTextX(), arrow.getTextY(),
							arrow.getLength(), arrow.getWidth()).buildAnimated(time, arrow.getStartDirection(), false));
				}

				return arrows;
			}
		});
	}

	public void setLabels(GenericLabelWrapper... labels) {
		LABELS = labels;
	}

}
