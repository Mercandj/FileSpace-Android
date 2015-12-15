package com.mercandalli.android.apps.files.main;

import com.mercandalli.android.apps.files.file.FileModel;

/**
 * Created by Jonathan on 21/10/2015.
 */
public interface ApplicationCallback extends Config.ConfigCallback {
    boolean isLogged();

    void invalidateMenu();

    FileModel createImageFile();

    void refreshData();

    void updateAdapters();

}
