package net.mythonia.carepackage;

import com.massivecraft.massivecore.Identified;
import com.massivecraft.massivecore.util.PermissionUtil;
import org.bukkit.permissions.Permissible;

public enum Perm implements Identified {

    BASECOMMAND,
    COORDINATES,
    GIVE,
    RELOAD;

    private final String id;

    Perm() {
        this.id = PermissionUtil.createPermissionId(CarePackagePlugin.get(), this);
    }

    @Override
    public String getId() {
        return this.id;
    }

    public boolean has(Permissible permissible, boolean verboose) {
        return PermissionUtil.hasPermission(permissible, this, verboose);
    }

    public boolean has(Permissible permissible) {
        return PermissionUtil.hasPermission(permissible, this);
    }

}