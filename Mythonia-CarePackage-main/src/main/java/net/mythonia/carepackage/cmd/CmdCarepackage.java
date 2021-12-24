package net.mythonia.carepackage.cmd;

import net.mythonia.carepackage.entity.Conf;

import java.util.List;

public class CmdCarepackage extends CommandCarepackage {

    private static final CmdCarepackage i = new CmdCarepackage();

    public CmdCarepackageCoordinates cmdCarepackageCoordinates = new CmdCarepackageCoordinates();
    public CmdCarepackageGive cmdCarepackageGive = new CmdCarepackageGive();
    public CmdCarepackageReload cmdCarepackageReload = new CmdCarepackageReload();
//    public CmdSpawnCarepackage cmdSpawnCarepackage = new CmdSpawnCarepackage();

    public static CmdCarepackage get() {
        return i;
    }

    @Override
    public List<String> getAliases() {
        return Conf.get().cmdAliases;
    }

}