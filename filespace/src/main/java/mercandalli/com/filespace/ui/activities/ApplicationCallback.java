package mercandalli.com.filespace.ui.activities;

import mercandalli.com.filespace.models.ModelFile;

/**
 * Created by Jonathan on 21/10/2015.
 */
public interface ApplicationCallback extends ConfigCallback {
    boolean isLogged();

    void invalidateMenu();

    void updateAdapters();

    ModelFile createImageFile();
}
