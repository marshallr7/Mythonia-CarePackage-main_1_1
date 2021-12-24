package net.mythonia.carepackage.entity;

import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.MStore;
import net.mythonia.carepackage.CarePackagePlugin;

public class CarepackagesColl extends Coll<Carepackages> {

    private static final CarepackagesColl i = new CarepackagesColl();

    private CarepackagesColl() {
        super("carepackage_data", Carepackages.class, MStore.getDb(), CarePackagePlugin.get());
    }

    public static CarepackagesColl get() {
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
        Carepackages.i = this.get("carepackage_data", true);
    }

}