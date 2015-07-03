package mercandalli.com.filespace.util;

import android.text.Html;
import android.text.Spanned;

import java.util.List;

/**
 * Created by Jonathan on 03/07/15.
 */
public class HtmlUtils {

    public static Spanned createListItem(List<StringPair> list) {
        String tmp = "<ul>";
        for(StringPair sp : list) {
            tmp += "<li><div><font color='#444'><b>"+sp.getName() + " : </b></font>"+sp.getValue()+"</div></li>";
        }
        return Html.fromHtml(tmp + "</ul>");
    }

}
