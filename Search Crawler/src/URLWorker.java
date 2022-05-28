import java.net.MalformedURLException;
import java.net.URL;

public class URLWorker {
    //получение базового домена из url
    public static String getBaseDomain(String url) {
        if (url.substring(8).contains("/")) {
            url = url.substring(0, url.indexOf("/", 8));
        }
        return url;
    }

    //построение абсолютного url-адреса из относительного
    public static String buildURL(String baseDomain, String url) {
        if (!url.contains(baseDomain)) {
            while (url.startsWith("/")) {
                url = url.substring(1);
            }
            if (verifyURL(url)) {
                return url;
            }
            return baseDomain + "/" + url;
        }
        else return url;
    }

    //проверка url
    public static boolean verifyURL (String url) {
        if (url.contains("#") || url.contains("javascript:") || url.contains("index.html")
                || url.contains("tel:") || url.contains("mailto:")) {
            return false;
        }
        try {
            URL verifiedUrl = new URL(url);
        }
        catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
