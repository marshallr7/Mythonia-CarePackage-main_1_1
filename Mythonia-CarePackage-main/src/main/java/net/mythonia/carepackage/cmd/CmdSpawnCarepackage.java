package net.mythonia.carepackage.cmd;

import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.util.Txt;
import net.mythonia.carepackage.Perm;
import net.mythonia.carepackage.entity.Carepackages;
import org.bukkit.entity.Player;

public class CmdSpawnCarepackage extends CommandCarepackage {

    public CmdSpawnCarepackage() {
        this.addRequirements(RequirementHasPerm.get(Perm.RELOAD));
    }

    @Override
    public void perform() {
        sender.sendMessage("Test");
        Carepackages carepackages = new Carepackages();
        Player player = (Player) sender;
        carepackages.spawnCarepackage(player.getLocation(), player.getUniqueId());
        sender.sendMessage(Txt.colorize("&aCarepackage Spawned."));
    }
}
