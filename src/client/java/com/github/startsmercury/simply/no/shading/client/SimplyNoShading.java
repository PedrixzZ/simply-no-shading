package com.github.startsmercury.simply.no.shading.client;

import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;

import org.slf4j.Logger;

import com.github.startsmercury.simply.no.shading.entrypoint.SimplyNoShadingClientEntrypoint;
import com.github.startsmercury.simply.no.shading.util.PrefixedLogger;
import com.github.startsmercury.simply.no.shading.util.storage.JsonPathStorage;
import com.github.startsmercury.simply.no.shading.util.storage.Storage;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

/**
 * The {@code SimplyNoShading} class models the Simply No Shading mod. It
 * contains the options, allows changing the options, and loading and saving of
 * the options. The model does not directly interact with the game, aside from
 * {@link #setOptions(Options)} which reloads the level when a change is
 * detected. Coupling this class with the base game is the responsibility of
 * {@link SimplyNoShadingClientEntrypoint} (and the mixins).
 *
 * @since 6.0.0
 */
public class SimplyNoShading {
	/**
	 * The first instance of the {@code SimplyNoShading} class.
	 */
	private static SimplyNoShading firstInstance;

	/**
	 * The category key for key mappings.
	 */
	public static final String KEY_CATEGORY = "key.categories.simply-no-shading";

	/**
	 * This mod's logger.
	 */
	public static final Logger LOGGER = PrefixedLogger.named("simply-no-shading", "[SimplyNoShading] ");

	/**
	 * Sets the first instance if there's not one set.
	 *
	 * @param instance the instance to try set as first
	 */
	private static void computeFirstInstanceIfAbsent(final SimplyNoShading instance) {
		if (firstInstance == null)
			firstInstance = instance;
	}

	/**
	 * Tries to get the options directory by using the {@linkplain FabricLoader
	 * fabric loader}. When called before the fabric loader is initialized, it'll
	 * instead return the current working directory.
	 *
	 * @return the options directory
	 */
	private static Path getConfigDirectory() {
		try {
			final var fabricLoader = FabricLoader.getInstance();
			final var configDirectory = fabricLoader.getConfigDir();

			return configDirectory;
		} catch (final RuntimeException re) { // FabricLoader is not yet loaded
			return Path.of("/");
		}
	}

	/**
	 * Returns the default options path.
	 *
	 * @return the default options path
	 * @see #getConfigDirectory()
	 */
	private static Path getDefaultOptionsPath() {
		final var configDirectory = SimplyNoShading.getConfigDirectory();
		final var optionsPath = configDirectory.resolve("simply-no-shading.json");

		return optionsPath;
	}

	/**
	 * Returns the first instance of the {@code SimplyNoShading} class.
	 *
	 * @return the first instance of the {@code SimplyNoShading} class
	 */
	public static SimplyNoShading getFirstInstance() {
		return firstInstance;
	}

	/**
	 * The options is responsible in storing the states that may modify the behavior
	 * of the mod.
	 */
	private Options options;

	/**
	 * The options storage dictates where the {@link #options} should be stored,
	 * most likely in a persistent file.
	 */
	private Storage<Options> optionsStorage;

	/**
	 * Creates a new {@code SimplyNoShading} instance.
	 */
	public SimplyNoShading() {
		this.options = Options.INTERNAL_SHADERS;
		this.optionsStorage = new JsonPathStorage<>(getDefaultOptionsPath(),
		        new GsonBuilder().setPrettyPrinting().create(),
		        Options.class);

		computeFirstInstanceIfAbsent(this);
	}

	/**
	 * Returns the options. It is responsible in storing the states that may modify
	 * the behavior of the mod
	 *
	 * @return the options
	 * @deprecated Use {@link #getOptions()} instead
	 */
	@Deprecated(forRemoval = true)
	public Options getConfig() {
		return getOptions();
	}

	/**
	 * Returns the config storage. It dictates where the {@link #getConfig() config}
	 * should be stored, most likely in a persistent file.
	 *
	 * @return the config storage
	 */
	@Deprecated(forRemoval = true)
	public Storage<Options> getConfigStorage() {
		return getOptionsStorage();
	}

	/**
	 * Returns the options. It is responsible in storing the states that may modify
	 * the behavior of the mod
	 *
	 * @return the options
	 */
	public Options getOptions() {
		return this.options;
	}

	/**
	 * Returns the options storage. It dictates where the {@link #getOptions()
	 * options} should be stored, most likely in a persistent file.
	 *
	 * @return the options storage
	 */
	public Storage<Options> getOptionsStorage() {
		return this.optionsStorage;
	}

	/**
	 * Loads the config from the {@link #getConfigStorage() config storage} logging
	 * any errors caught.
	 */
	@Deprecated(forRemoval = true)
	public void loadConfig() {
		loadOptions();
	}

	/**
	 * Loads the options from the {@link #getOptionsStorage() options storage}
	 * logging any errors caught.
	 */
	public void loadOptions() {
		try {
			setOptions(getOptionsStorage().load());
		} catch (final NoSuchFileException nsfe) {
			saveOptions();
		} catch (final Exception e) {
			LOGGER.warn("Unable to load options", e);
		}
	}

	/**
	 * Saves the config to the {@link #getConfigStorage() config storage} logging
	 * any errors caught.
	 */
	@Deprecated(forRemoval = true)
	public void saveConfig() {
		saveOptions();
	}

	/**
	 * Saves the options to the {@link #getOptionsStorage() options storage} logging
	 * any errors caught.
	 */
	public void saveOptions() {
		try {
			getOptionsStorage().save(getOptions());
		} catch (final Exception e) {
			LOGGER.warn("Unable to save options", e);
			e.printStackTrace();
		}
	}

	/**
	 * Sets a new options. It is responsible in storing the states that may modify
	 * the behavior of the mod
	 *
	 * @param options the new options
	 * @deprecated Use {@link #setOptions(Options)} instead
	 */
	@Deprecated(forRemoval = true)
	public void setConfig(final Options options) {
		setOptions(options);
	}

	/**
	 * Sets a new config storage. It dictates where the {@link #getConfig() config}
	 * should be stored, most likely in a persistent file.
	 *
	 * @param configStorage the new config storage
	 */
	@Deprecated(forRemoval = true)
	public void setConfigStorage(final Storage<Options> configStorage) {
		setOptionsStorage(configStorage);
	}

	/**
	 * Sets a new options. It is responsible in storing the states that may modify
	 * the behavior of the mod
	 *
	 * @param options the new options
	 */
	public void setOptions(final Options options) {
		Objects.requireNonNull(options, "Parameter options was null");

		if (this.options.equals(options))
			return;

		this.options = options;

		final var minecraft = Minecraft.getInstance();
		if (minecraft.levelRenderer != null)
			minecraft.levelRenderer.allChanged();
	}

	/**
	 * Sets a new options storage. It dictates where the {@link #getOptions()
	 * options} should be stored, most likely in a persistent file.
	 *
	 * @param optionsStorage the new options storage
	 */
	public void setOptionsStorage(final Storage<Options> optionsStorage) {
		Objects.requireNonNull(optionsStorage, "Parameter optionsStorage was null");

		this.optionsStorage = optionsStorage;
	}
}
