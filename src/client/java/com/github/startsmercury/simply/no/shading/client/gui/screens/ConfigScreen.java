package com.github.startsmercury.simply.no.shading.client.gui.screens;

import com.github.startsmercury.simply.no.shading.client.Options;

import dev.lambdaurora.spruceui.screen.SpruceScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * The {@code ConfigScreen} class is an implementation of {@link SpruceScreen}
 * that functions as the config screen for Simply No Shading.
 * <p>
 * Like any other screens for minecraft, it can be displayed by using
 * {@link Minecraft#setScreen(Screen)}.
 *
 * @since 6.0.0
 */
@Deprecated(forRemoval = true)
public class ConfigScreen extends OptionsScreen {
	/**
	 * Creates a new screen with a set parent, {@linkplain #DEFAULT_TITLE default
	 * title}, and default config builder.
	 *
	 * @param parent the parent screen
	 */
	public ConfigScreen(final Screen parent) {
		super(parent);
	}

	/**
	 * Creates a new screen with a set parent, title, and default config builder.
	 *
	 * @param parent the parent screen
	 * @param title  the screen title
	 */
	protected ConfigScreen(final Screen parent, final Component title) {
		super(parent, title);
	}

	/**
	 * Creates a new screen with a set parent, title, and config builder.
	 *
	 * @param parent        the parent screen
	 * @param title         the screen title
	 * @param configBuilder the config builder
	 */
	protected ConfigScreen(final Screen parent, final Component title, final Options.Builder configBuilder) {
		super(parent, title, configBuilder);
	}
}
