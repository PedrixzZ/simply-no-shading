package com.github.startsmercury.simply.no.shading.client;

/**
 * The {@code Options} class is extendable but immutable collection of data that
 * plays a role in the bahavior of Simply No Shading, primarily in toggling
 * shading.
 *
 * @since 6.0.0
 */
@Deprecated(forRemoval = true)
public class Config extends Options {
	/**
	 * Creates a new config with all the fields set.
	 *
	 * @param blockShadingEnabled controls block shading, excluding block entities
	 * @param cloudShadingEnabled controls cloud shading
	 */
	public Config(final boolean blockShadingEnabled, final boolean cloudShadingEnabled) {
		super(blockShadingEnabled, cloudShadingEnabled);
	}
}
