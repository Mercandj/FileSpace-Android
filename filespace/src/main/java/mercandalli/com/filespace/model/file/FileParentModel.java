package mercandalli.com.filespace.model.file;

import java.io.File;

/**
 * Created by Jonathan on 27/10/2015.
 */
public class FileParentModel {

    private int mId;
    private File mFile;

    public FileParentModel(final int id) {
        mId = id;
    }

    public FileParentModel(final File file) {
        mFile = file;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File mFile) {
        this.mFile = mFile;
    }

    public boolean isOnline() {
        return mFile == null;
    }
}
