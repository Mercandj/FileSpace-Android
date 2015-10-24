package mercandalli.com.filespace.net.response;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import mercandalli.com.filespace.model.file.FileModel;
import mercandalli.com.filespace.model.file.FileTypeModel;

/**
 * Created by Jonathan on 23/10/2015.
 */
public class GetFileResponse {
    @SerializedName("id")
    private int mId;

    @SerializedName("id_user")
    private int mIdUser;

    @SerializedName("id_file_parent")
    private int mIdFileParent;

    @SerializedName("name")
    private String mName;

    @SerializedName("url")
    private String mUrl;

    @SerializedName("size")
    private long mSize;

    @SerializedName("public")
    private long mPublic;

    @SerializedName("type")
    private String mType;

    @SerializedName("directory")
    private long mDirectory;

    @SerializedName("date_creation")
    private String mDateCreation;

    public FileModel createFileModel() {

        Date dateCreation = null;
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            dateCreation = dateFormatGmt.parse(mDateCreation);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new FileModel.FileModelBuilder()
                .id(mId)
                .idUser(mIdUser)
                .idFileParent(mIdFileParent)
                .name(mName)
                .url(mUrl)
                .size(mSize)
                .isPublic(mPublic == 1)
                .type(new FileTypeModel(mType))
                .isDirectory(mDirectory == 1)
                .dateCreation(dateCreation)
                .build();
    }
}
