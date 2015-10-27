package mercandalli.com.filespace.local;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mercandalli.com.filespace.config.Constants;
import mercandalli.com.filespace.model.file.FileModel;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class FileLocalApi {

    public List<FileModel> getFiles(final File directoryFile, final String search, final int sortMode) {
        List<File> fs = Arrays.asList((search == null) ? directoryFile.listFiles() : directoryFile.listFiles(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().contains(search.toLowerCase());
                    }
                }
        ));

        if (sortMode == Constants.SORT_ABC) {
            Collections.sort(fs, new Comparator<File>() {
                @Override
                public int compare(final File f1, final File f2) {
                    return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
                }
            });
        } else if (sortMode == Constants.SORT_SIZE) {
            Collections.sort(fs, new Comparator<File>() {
                @Override
                public int compare(final File f1, final File f2) {
                    return (new Long(f2.length())).compareTo(f1.length());
                }
            });
        } else {
            final Map<File, Long> staticLastModifiedTimes = new HashMap<>();
            for (File f : fs) {
                staticLastModifiedTimes.put(f, f.lastModified());
            }
            Collections.sort(fs, new Comparator<File>() {
                @Override
                public int compare(final File f1, final File f2) {
                    return staticLastModifiedTimes.get(f2).compareTo(staticLastModifiedTimes.get(f1));
                }
            });
        }

        List<FileModel> mFilesList = new ArrayList<>();
        for (File file : fs) {
            FileModel.FileModelBuilder mileModelBuilder = new FileModel.FileModelBuilder();
            mFilesList.add(
                    mileModelBuilder.file(file)
                            .build());
        }

        return mFilesList;
    }

}
