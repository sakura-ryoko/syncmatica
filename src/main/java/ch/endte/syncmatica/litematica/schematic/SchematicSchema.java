package ch.endte.syncmatica.litematica.schematic;

import javax.annotation.Nonnull;

/**
 * Cloned from Litematica 1.21.5 -- Sakura
 */
public record SchematicSchema(int litematicVersion, int minecraftDataVersion)
{
    @Override
    public @Nonnull String toString()
    {
        return "V" + this.litematicVersion() + " / DataVersion " + this.minecraftDataVersion();
    }
}
