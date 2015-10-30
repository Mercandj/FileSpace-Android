package mercandalli.com.filespace.ui.activitiy;

import mercandalli.com.filespace.model.file.FileModel;

/**
 * Created by Jonathan on 21/10/2015.
 */
public interface ApplicationCallback extends ConfigCallback {
    boolean isLogged();

    void invalidateMenu();

    FileModel createImageFile();

    void refreshAdapters();

    void updateAdapters();

}
