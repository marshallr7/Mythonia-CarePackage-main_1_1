package net.mythonia.carepackage.entity;

import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.MStore;
import net.mythonia.carepackage.CarePackagePlugin;

public class ConfColl extends Coll<Conf> {

    private static final ConfColl i = new ConfColl();

    private ConfColl() {
        super("carepackage_conf", Conf.class, MStore.getDb(), CarePackagePlugin.get());
    }

    public static ConfColl get() {
        return i;
    }

    @Override
    public void onTick() {
        super.onTick();
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        if (!active) {
            return;
        }
        Conf.i = this.get("carepackage_conf", true);
    }

}