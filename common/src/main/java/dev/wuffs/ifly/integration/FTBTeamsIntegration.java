package dev.wuffs.ifly.integration;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.wuffs.ifly.network.records.AvailableTeam;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FTBTeamsIntegration implements TeamsInterface {

    public static final FTBTeamsIntegration INSTANCE = new FTBTeamsIntegration();
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
        List<AvailableTeam> teams = new ArrayList<>();
        for (Team team : FTBTeamsAPI.api().getManager().getTeams()) {
            teams.add(new AvailableTeam(team.getId(), team.getName(), team.getMembers().size()));
        }
        return teams;
    }
}
