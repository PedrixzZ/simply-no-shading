package com.github.startsmercury.simply.no.shading.entrypoint;

import static net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding;

import com.github.startsmercury.simply.no.shading.client.Options;
import com.github.startsmercury.simply.no.shading.client.SimplyNoShading;
import com.github.startsmercury.simply.no.shading.client.gui.screens.OptionsScreen;
import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;

/**
 * The {@code SimplyNoShadingClientEntrypoint} class is an implementation of
 * {@link ClientModInitializer} and is an entrypoint defined with the
 * {@code client} key in the {@code fabric.mod.json}. This allows Simply No
 * Shading to be initialized and configured for the minecraft client.
 */
public class SimplyNoShadingClientEntrypoint implements ClientModInitializer {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onInitializeClient() {
		final var simplyNoShading = new SimplyNoShading();

		simplyNoShading.loadOptions();

		setupKeyMappings(simplyNoShading);
		setupShutdownHook(simplyNoShading::saveOptions);
	}

	/**
	 * Registers key mappings and key event listeners.
	 *
	 * @param simplyNoShading the simply no shading instance
	 */
	protected void setupKeyMappings(final SimplyNoShading simplyNoShading) {
		final var openOptionsScreen = new KeyMapping("key.simply-no-shading.openOptionsScreen",
		        InputConstants.UNKNOWN.getValue(),
		        SimplyNoShading.KEY_CATEGORY);
		final var reloadOptions = new KeyMapping("key.simply-no-shading.reloadOptions",
		        InputConstants.UNKNOWN.getValue(),
		        SimplyNoShading.KEY_CATEGORY);
		final var toggleBlockShading = new KeyMapping("key.simply-no-shading.toggleBlockShading",
		        InputConstants.UNKNOWN.getValue(),
		        SimplyNoShading.KEY_CATEGORY);
		final var toggleCloudShading = new KeyMapping("key.simply-no-shading.toggleCloudShading",
		        InputConstants.UNKNOWN.getValue(),
		        SimplyNoShading.KEY_CATEGORY);

		registerKeyBinding(openOptionsScreen);
		registerKeyBinding(reloadOptions);
		registerKeyBinding(toggleBlockShading);
		registerKeyBinding(toggleCloudShading);

		ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
			if (reloadOptions.consumeClick()) {
				while (reloadOptions.consumeClick()) {}

				simplyNoShading.loadOptions();
			}

			if (openOptionsScreen.consumeClick()) {
				while (openOptionsScreen.consumeClick()) {}

				minecraft.setScreen(new OptionsScreen(null));
				return;
			}

			final var builder = Options.builder(simplyNoShading.getOptions());

			while (toggleBlockShading.consumeClick())
				builder.toggleBlockShading();
			while (toggleCloudShading.consumeClick())
				builder.toggleCloudShading();

			simplyNoShading.setOptions(builder.build());
		});
	}

	/**
	 * Registers a shutdown thread with the name 'Simply No Shading Shutdown Thread'
	 *
	 * @param shutdownAction the shutdown action to run
	 */
	protected void setupShutdownHook(final Runnable shutdownAction) {
		final var shutdownThread = new Thread(shutdownAction);
		shutdownThread.setName("Simply No Shading Shutdown Thread");
		Runtime.getRuntime().addShutdownHook(shutdownThread);
	}
}
