package dev.wuffs.ifly.integration;

import dev.wuffs.ifly.AscensionShard;
import dev.wuffs.ifly.network.records.AvailableTeam;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public class CadmusIntegration implements TeamsInterface {
    public static final ResourceLocation ID = new ResourceLocation(AscensionShard.MOD_ID, "cadmus");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public boolean isInTeam(Player player, UUID teamUUID) {
        return false;
    }

    @Override
    public UUID getPlayerTeam(Player player) {
        return null;
    }

    @Override
    public List<AvailableTeam> getAvailableTeams() {
        return null;
    }
}
