package com.mercandalli.android.filespace.main;

import com.mercandalli.android.filespace.file.FileModel;

/**
 * Created by Jonathan on 21/10/2015.
 */
public interface ApplicationCallback extends ConfigCallback {
    boolean isLogged();

    void invalidateMenu();

    FileModel createImageFile();

    void refreshData();

    void updateAdapters();

}
