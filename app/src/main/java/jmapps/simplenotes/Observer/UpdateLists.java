package jmapps.simplenotes.Observer;

import java.util.Observable;

public class UpdateLists extends Observable {

    private boolean updateAdapterLists;
    private static UpdateLists INSTANCE;

    public static UpdateLists getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UpdateLists();
        }
        return INSTANCE;
    }

    public boolean isUpdateAdapterLists() {
        return updateAdapterLists;
    }

    public void setUpdateAdapterLists(boolean updateAdapterLists) {
        this.updateAdapterLists = updateAdapterLists;
        setChanged();
    }
}
