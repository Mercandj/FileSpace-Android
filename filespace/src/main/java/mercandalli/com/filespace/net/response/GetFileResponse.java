package mercandalli.com.filespace.net.response;

import com.google.gson.annotations.SerializedName;

import mercandalli.com.filespace.models.better.FileModel;
import mercandalli.com.filespace.models.better.FileTypeModel;

/**
 * Created by Jonathan on 23/10/2015.
 */
public class GetFileResponse {
    @SerializedName("id")
    private int mId;

    @SerializedName("id_user")
    private int mIdUser;

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

    public FileModel createFileModel() {
        return new FileModel.FileModelBuilder()
                .id(mId)
                .idUser(mIdUser)
                .name(mName)
                .url(mUrl)
                .size(mSize)
                .isPublic(mPublic == 1)
                .type(new FileTypeModel(mType))
                .isDirectory(mDirectory == 1)
                .build();
    }
}
