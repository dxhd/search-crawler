import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Indexator {

    WebPage indexate(String link) throws IOException {
        URL url = new URL(link);
        Document webpage = Jsoup.connect(link).get();;
        String html = webpage.getElementsByTag("html").html();
        StringBuffer code = new StringBuffer(html);

        Elements hTags = webpage.body().getElementsByTag("h1");
        hTags.addAll(webpage.body().getElementsByTag("h2"));
        hTags.addAll(webpage.body().getElementsByTag("h3"));

        ArrayList<String> headers = new ArrayList<>();
        for (Element header :
                hTags) {

            headers.add(header.text());
        }
        return new WebPage(link, code, headers);
    }

    WebPage indexate(Document webpage) throws IOException {
        String link = webpage.baseUri();
        URL url = new URL(link);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

//        String line = null;
//        while ((line = reader.readLine()) != null) {
//            code.append(line);
//        }

        String html = webpage.getElementsByTag("html").html();

        StringBuffer code = new StringBuffer(html);

        Elements hTags = webpage.body().getElementsByTag("h1");
        hTags.addAll(webpage.body().getElementsByTag("h2"));
        hTags.addAll(webpage.body().getElementsByTag("h3"));

        ArrayList<String> headers = new ArrayList<>();
        for (Element header :
                hTags) {
            https://metanit.com/
            headers.add(header.text());
        }
        return new WebPage(link, code, headers);
    }

}
