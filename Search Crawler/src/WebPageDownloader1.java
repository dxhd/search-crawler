import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

class WebPageDownloader {

    private static WebPageDownloader INSTANCE;
    WebPageDownloader() { };
    public static WebPageDownloader getInstance() {
        if (INSTANCE == null) {
            synchronized (WebPageDownloader.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WebPageDownloader();
                }
            }
        }
        return INSTANCE;
    }

    public StringBuffer downloadCode(URL url) {
        StringBuffer code = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                code.append(line).append("\n");
            }
            reader.close();
        } catch (IOException ex) {
            System.out.println("Ошибка загрузки сайта.");
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                System.out.println("Ошибка закрытия буффера.");
            }
        }
        return code;
    }


}
