package mercandalli.com.filespace.models.better;

import android.text.Spanned;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mercandalli.com.filespace.utils.FileUtils;
import mercandalli.com.filespace.utils.HtmlUtils;
import mercandalli.com.filespace.utils.StringPair;
import mercandalli.com.filespace.utils.TimeUtils;

/**
 * Created by Jonathan on 24/10/2015.
 */
public class FileModelUtils {

    public static Spanned toSpanned(final FileModel fileModel) {
        final FileTypeModel type = fileModel.getType();
        final boolean isDirectory = fileModel.isDirectory();
        final long size = fileModel.getSize();
        final boolean isPublic = fileModel.isPublic();
        final Date dateCreation = fileModel.getDateCreation();

        List<StringPair> spl = new ArrayList<>();
        spl.add(new StringPair("Name", fileModel.getName()));
        if (!fileModel.isDirectory()) {
            spl.add(new StringPair("Extension", type.toString()));
        }
        spl.add(new StringPair("Type", type.getTitle()));
        if (!isDirectory || size != 0) {
            spl.add(new StringPair("Size", FileUtils.humanReadableByteCount(size)));
        }
        if (dateCreation != null) {
            if (fileModel.isOnline()) {
                spl.add(new StringPair("Upload date", TimeUtils.getDate(dateCreation)));
            } else {
                spl.add(new StringPair("Last modification date", TimeUtils.getDate(dateCreation)));
            }
        }
        if (fileModel.isOnline())
            spl.add(new StringPair("Visibility", isPublic ? "Public" : "Private"));
        return HtmlUtils.createListItem(spl);
    }

}
