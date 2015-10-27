package mercandalli.com.filespace.net.response;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import mercandalli.com.filespace.model.file.FileSpaceModel;

/**
 * Created by Jonathan on 23/10/2015.
 */
public class GetFileSpaceResponse {

    @SerializedName("type")
    private String mType;

    @SerializedName("article_title_1")
    private String mDateCreation;

    @SerializedName("article_title_1")
    private String mArticleTitle1;

    @SerializedName("article_content_1")
    private String mArticleContent1;

    public FileSpaceModel createModel() {

        Date dateCreation = null;
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            dateCreation = dateFormatGmt.parse(mDateCreation);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new FileSpaceModel.FileSpaceModelBuilder()
                .type(mType)
                .dateCreation(dateCreation)
                .articleTitle1(mArticleTitle1)
                .articleContent1(mArticleContent1)
                .build();
    }
}
