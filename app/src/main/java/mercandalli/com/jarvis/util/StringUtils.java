package mercandalli.com.jarvis.util;

import java.text.Normalizer;
import java.util.StringTokenizer;

/**
 * Created by Jonathan on 17/05/2015.
 */
public class StringUtils {

    public static String[] getWords(String sentence) {
        if(sentence == null)
            return null;
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

    public static String nomalizeString(String message) {
        if(message == null)
            return null;
        return remplaceAccents(message.toLowerCase());
    }

    public static String remplaceAccents(String message) {
        if(message == null)
            return null;
        message = Normalizer.normalize(message, Normalizer.Form.NFD);
        message = message.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return message;
    }

}
