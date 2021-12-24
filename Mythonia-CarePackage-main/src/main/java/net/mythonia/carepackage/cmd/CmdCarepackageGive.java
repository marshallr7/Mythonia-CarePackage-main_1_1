package net.mythonia.carepackage.cmd;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.command.type.sender.TypePlayer;
import com.massivecraft.massivecore.util.Txt;
import net.mythonia.carepackage.CarePackagePlugin;
import net.mythonia.carepackage.Perm;
import net.mythonia.carepackage.entity.Conf;
import org.bukkit.entity.Player;

public class CmdCarepackageGive extends CommandCarepackage {

    public CmdCarepackageGive() {
        this.addParameter(TypePlayer.get(), "player");
        this.addParameter(TypeInteger.get(), "amount", "1");
        this.addRequirements(RequirementHasPerm.get(Perm.GIVE));
    }

    @Override
    public void perform() throws MassiveException {
        Player player = this.readArg();
        int amount = this.readArg(1);

        player.getInventory().addItem(CarePackagePlugin.get().getCarepackageItem(amount))
                .forEach((integer, itemStack) -> player.getLocation().getWorld().dropItemNaturally(player.getLocation(), itemStack));

        if (!Conf.get().msgReceivedCarePackage.isEmpty()) {
            player.sendMessage(Txt.colorize(Conf.get().msgReceivedCarePackage
                    .replace("{amount}", CarePackagePlugin.get().getNumberFormat().format(amount))
            ));
        }

        if (!Conf.get().msgGivenCarePackage.isEmpty()) {
            player.sendMessage(Txt.colorize(Conf.get().msgGivenCarePackage
                    .replace("{player}", player.getName())
                    .replace("{amount}", CarePackagePlugin.get().getNumberFormat().format(amount))
            ));
        }
    }

}