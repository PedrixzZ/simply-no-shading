package com.github.startsmercury.simply.no.shading.client.gui.screens;

import static com.github.startsmercury.simply.no.shading.client.SimplyNoShading.LOGGER;
import static dev.lambdaurora.spruceui.background.EmptyBackground.EMPTY_BACKGROUND;
import static dev.lambdaurora.spruceui.util.ColorUtil.WHITE;
import static dev.lambdaurora.spruceui.util.ColorUtil.packARGBColor;
import static dev.lambdaurora.spruceui.util.RenderUtil.renderBackgroundTexture;
import static net.minecraft.network.chat.CommonComponents.GUI_DONE;

import com.github.startsmercury.simply.no.shading.client.Options;
import com.github.startsmercury.simply.no.shading.client.SimplyNoShading;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.option.SpruceBooleanOption;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceOptionListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.network.chat.Component;

/**
 * The {@code OptionsScreen} class is an implementation of {@link SpruceScreen}
 * that functions as the options screen for Simply No Shading.
 * <p>
 * Like any other screens for minecraft, it can be displayed by using
 * {@link Minecraft#setScreen(Screen)}.
 *
 * @since 6.1.0
 */
public class OptionsScreen extends SpruceScreen {
	/**
	 * The height of the button panel of this screen.
	 */
	private static final int BUTTON_PANEL_HEIGHT = 35;

	/**
	 * The default title for Simply No Shading's options screen.
	 */
	public static final Component DEFAULT_TITLE = Component.translatable("options.simply-no-shading");

	/**
	 * Fully opaque components render completely.
	 */
	private static final float OPAQUE = 1.0F;

	/**
	 * The color used over panorama and level renders.
	 */
	private static final int RENDER_GRADIENT_COLOR = packARGBColor(20, 20, 20, 79);

	/**
	 * The height of the title panel of this screen.
	 */
	private static final int TITLE_PANEL_HEIGHT = 34;

	/**
	 * The options builder to build immutable options objects.
	 */
	protected Options.Builder optionsBuilder;

	/**
	 * The options widget that contains the options for the user to interact.
	 */
	protected SpruceOptionListWidget optionsWidget;

	/**
	 * The panorama renderer used when there is no level to render.
	 */
	protected final PanoramaRenderer panoramaRenderer;

	/**
	 * The parent screen who'll regain display once this screen is done for :).
	 */
	protected final Screen parent;

	/**
	 * Creates a new screen with a set parent, {@linkplain #DEFAULT_TITLE default
	 * title}, and default options builder.
	 *
	 * @param parent the parent screen
	 */
	public OptionsScreen(final Screen parent) {
		this(parent, DEFAULT_TITLE);
	}

	/**
	 * Creates a new screen with a set parent, title, and default options builder.
	 *
	 * @param parent the parent screen
	 * @param title  the screen title
	 */
	protected OptionsScreen(final Screen parent, final Component title) {
		this(parent, title, Options.builder(SimplyNoShading.getFirstInstance().getOptions()));
	}

	/**
	 * Creates a new screen with a set parent, title, and options builder.
	 *
	 * @param parent         the parent screen
	 * @param title          the screen title
	 * @param optionsBuilder the options builder
	 */
	protected OptionsScreen(final Screen parent, final Component title, final Options.Builder optionsBuilder) {
		super(title);

		this.panoramaRenderer = new PanoramaRenderer(TitleScreen.CUBE_MAP);
		this.parent = parent;
		this.optionsBuilder = optionsBuilder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void init() {
		super.init();

		final SpruceBooleanOption blockShadingEnabledOption;
		final SpruceBooleanOption cloudShadingEnabledOption;
		final SpruceButtonWidget doneButton;

		this.optionsWidget = new SpruceOptionListWidget(Position.of(0, TITLE_PANEL_HEIGHT),
		        this.width,
		        this.height - TITLE_PANEL_HEIGHT - BUTTON_PANEL_HEIGHT);
		blockShadingEnabledOption = new SpruceBooleanOption("options.simply-no-shading.blockShadingEnabled",
		        this.optionsBuilder::isBlockShadingEnabled,
		        this.optionsBuilder::setBlockShadingEnabled,
		        Component.translatable("options.simply-no-shading.blockShadingEnabled.tooltip"));
		cloudShadingEnabledOption = new SpruceBooleanOption("options.simply-no-shading.cloudShadingEnabled",
		        this.optionsBuilder::isCloudShadingEnabled,
		        this.optionsBuilder::setCloudShadingEnabled,
		        Component.translatable("options.simply-no-shading.cloudShadingEnabled.tooltip"));
		{
			final var buttonWidth = 200;
			final var buttonHeight = 20;
			final var buttonPosition = Position.of((this.width - buttonWidth) / 2, this.height - buttonHeight - 7);
			doneButton = new SpruceButtonWidget(buttonPosition,
			        buttonWidth,
			        buttonHeight,
			        GUI_DONE,
			        button -> onClose());
		}

		this.optionsWidget.addOptionEntry(blockShadingEnabledOption, cloudShadingEnabledOption);
		this.optionsWidget.setBackground(EMPTY_BACKGROUND);

		addRenderableWidget(this.optionsWidget);
		addRenderableWidget(doneButton);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removed() {
		if (this.optionsBuilder != null)
			SimplyNoShading.getFirstInstance().setOptions(this.optionsBuilder.build());
		else
			LOGGER.warn(this + " tried to save changes from a null options builder");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(final PoseStack poseStack, final int mouseX, final int mouseY, final float delta) {
		if (this.minecraft.level == null) {
			this.panoramaRenderer.render(delta, OPAQUE);
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
		}
		this.fillGradient(poseStack, 0, 0, this.width, this.height, RENDER_GRADIENT_COLOR, RENDER_GRADIENT_COLOR);

		renderBackgroundTexture(0, 0, this.width, TITLE_PANEL_HEIGHT, 0);
		renderBackgroundTexture(0, this.height - BUTTON_PANEL_HEIGHT, this.width, BUTTON_PANEL_HEIGHT, 0);

		super.render(poseStack, mouseX, mouseY, delta);

		final var titlePosX = this.width / 2;
		final var titlePosY = 14;
		drawCenteredString(poseStack, this.font, this.title, titlePosX, titlePosY, WHITE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renderBackground(final PoseStack poseStack, final int z) {
		// Override fogging that would have been applied to the panels' background
	}
}
