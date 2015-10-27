package mercandalli.com.filespace.model.file;

import java.io.File;

/**
 * Created by Jonathan on 27/10/2015.
 */
public class FileParentModel {

    private int mId;
    private boolean mIsMine;
    private File mFile;

    public FileParentModel(final int id, final boolean isMine) {
        mId = id;
        mIsMine = isMine;
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

    public boolean isMine() {
        return mIsMine;
    }

    public void setIsMine(boolean isMine) {
        this.mIsMine = isMine;
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
