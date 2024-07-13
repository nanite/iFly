package dev.wuffs.ifly.integration;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamRank;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import dev.wuffs.ifly.AscensionShard;
import dev.wuffs.ifly.network.records.AvailableTeam;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FTBTeamsIntegration implements TeamsInterface {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(AscensionShard.MOD_ID, "ftb_teams");

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public boolean isInTeam(Player player, UUID teamUUID) {
        return FTBTeamsAPI.api().getManager().getTeamByID(teamUUID)
                .map(t -> t.getMembers().contains(player.getUUID()))
                .orElse(false);
    }

    @Override
    public UUID getPlayerTeam(Player player) {
        return null;
    }

    @Override
    public List<AvailableTeam> getAvailableTeams() {
        List<AvailableTeam> teams = new ArrayList<>();
        for (Team team : FTBTeamsAPI.api().getManager().getTeams()) {
            teams.add(new AvailableTeam(team.getId(), team.getName(), team.getMembers().size()));
        }
        return teams;
    }

    @Override
    public boolean isManager(Player player, UUID teamId) {
        return FTBTeamsAPI.api().getManager().getTeamByID(teamId)
                // Check if the player is in the team
                .filter(t -> t.getMembers().contains(player.getUUID()))
                // Check if the player is at least an officer
                .map(t -> t.getRankForPlayer(player.getUUID()).isAtLeast(TeamRank.OFFICER))
                .orElse(false);
    }
}
