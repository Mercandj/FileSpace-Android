package com.mercandalli.android.apps.files.common.util;

import android.text.Html;
import android.text.Spanned;

import java.util.List;

/**
 * Created by Jonathan on 03/07/15.
 */
public class HtmlUtils {

    public static Spanned createListItem(final List<StringPair> list) {
        final StringBuilder tmp = new StringBuilder(400);
        tmp.append("<ul>");
        for (final StringPair sp : list) {
            tmp.append("<li><div><font color='#444'><b>")
                    .append(sp.getName())
                    .append(" : </b></font>")
                    .append(sp.getValue())
                    .append("</div></li>");
        }
        return Html.fromHtml(tmp.append("</ul>").toString());
    }

}
