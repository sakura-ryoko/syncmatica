package ch.endte.syncmatica.material;

import ch.endte.syncmatica.data.ServerPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class SyncmaticaMaterialList {
    private ArrayList<SyncmaticaMaterialEntry> list;
    private ServerPosition deliveryPoint;

    public SyncmaticaMaterialEntry getUnclaimedEntry() {
        final Optional<SyncmaticaMaterialEntry> unclaimed = list.parallelStream().filter(SyncmaticaMaterialEntry.UNFINISHED).filter(SyncmaticaMaterialEntry.UNCLAIMED).findFirst();
        if (unclaimed.isPresent()) {
            return unclaimed.get();
        }
        return null;
    }

    public Collection<DeliveryPosition> getDeliveryPosition(final SyncmaticaMaterialEntry entry) {
        if (!list.contains(entry)) {
            throw new IllegalArgumentException();
        }
        final DeliveryPosition delivery = new DeliveryPosition(deliveryPoint.getBlockPosition(), deliveryPoint.getDimensionId(), entry.getAmountMissing());
        final ArrayList<DeliveryPosition> deliveryList = new ArrayList<>();
        deliveryList.add(delivery);
        return deliveryList;
    }
}
