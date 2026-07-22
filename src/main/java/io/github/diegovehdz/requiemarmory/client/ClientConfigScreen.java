package io.github.diegovehdz.requiemarmory.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Hooks the mod up to NeoForge's built-in configuration GUI, reachable from the Mods list. Using the
 * built-in screen keeps the config dependency-free — no Cloth Config or similar required.
 *
 * <p>Client-only, and deliberately its own class: the mod constructor calls it behind a {@code Dist}
 * check, so {@link ConfigurationScreen} is never loaded on a dedicated server.</p>
 */
public final class ClientConfigScreen {
    private ClientConfigScreen() {}

    public static void register(ModContainer container) {
        // Typed local, not an inline lambda: registerExtensionPoint's (Class, T) and (Class, Supplier<T>)
        // overloads are both applicable to a bare lambda and the call is ambiguous without it.
        IConfigScreenFactory factory = ConfigurationScreen::new;
        container.registerExtensionPoint(IConfigScreenFactory.class, factory);
    }
}
