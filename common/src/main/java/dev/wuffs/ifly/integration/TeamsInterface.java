package dev.wuffs.ifly.integration;

import dev.wuffs.ifly.network.records.AvailableTeam;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public interface TeamsInterface {

    boolean isInTeam(Player player, UUID teamUUID);
    UUID getPlayerTeam(Player player);

    List<AvailableTeam> getAvailableTeams();
}
