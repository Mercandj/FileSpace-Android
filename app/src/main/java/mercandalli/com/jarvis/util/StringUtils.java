package mercandalli.com.jarvis.util;

import java.util.StringTokenizer;

/**
 * Created by Jonathan on 17/05/2015.
 */
public class StringUtils {

    public static String[] getWords(String sentence) {
        StringTokenizer stToken = new StringTokenizer(sentence, " ");
        int nbToken = stToken.countTokens();
        String[] messageTab = new String[nbToken];
        int token = 0;
        while (stToken.hasMoreTokens()) {
            messageTab[token] = stToken.nextToken();
            token++;
        }
        return messageTab;
    }

}
