package net.mythonia.carepackage.cmd;

import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.util.Txt;
import net.mythonia.carepackage.CarePackagePlugin;
import net.mythonia.carepackage.Perm;

public class CmdCarepackageReload extends CommandCarepackage {

    public CmdCarepackageReload() {
        this.addRequirements(RequirementHasPerm.get(Perm.RELOAD));
    }

    @Override
    public void perform() {
        CarePackagePlugin.get().refreshPrizePool();
        sender.sendMessage(Txt.colorize("&aReloaded prize pool."));
    }

}