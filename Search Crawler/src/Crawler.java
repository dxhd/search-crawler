import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

public class Crawler {

    private static boolean crawling;

    private Indexator indexator;
    private CrawlManager crawlManager;
    private FailedConnectionsSaver failedConnectionsSaver;
    private MySQLWorker dbWorker;

    void init() {
        indexator = new Indexator();
        crawlManager = CrawlManager.getInstance();
        failedConnectionsSaver = new FailedConnectionsSaver();
        dbWorker = new MySQLWorker();
    }

    //непостредственно краулинг
    public void crawl() {
        init();
        crawling = true; //поднятие флага
        while (crawling && !crawlManager.isQueueEmpty()) {
            String url = crawlManager.pollUrlFromQueue(); //берётся url из очереди
            try {
                Document document = Jsoup.connect(url).get(); //подключение к серверу
                //System.out.println("Log: Подключение к " + url + " выполнено успешно.");
                if (!crawlManager.isUrlDisallowed(url) && !crawlManager.isUrlCrawled(url)) {
                    String baseDomain = URLWorker.getBaseDomain(url);

                    WebPage webpage = indexator.indexate(document); //индексирование страницы
                    if (dbWorker.saveToDB(webpage)) { //загрузка страницы в бд
                        crawlManager.addToCrawledUrls(url);
                    }
                    Elements links = document.body().getElementsByTag("a"); //парсинг всех ссылок
                    for (Element link :
                            links) {
                        String href = link.attr("href");
                        href = URLWorker.buildURL(baseDomain, href);
                        if (crawlManager.isUrlCrawled(href)) {
                            continue;
                        }
                        if (URLWorker.verifyURL(href)) {
                            crawlManager.addUrlToQueue(href);
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                //System.out.println("Invalid url: " + url);
            } catch (IOException e) {
                //System.out.println("Connection to " + url + " failed.");
                crawlManager.failedUrls.add(url);
            }
        }
        failedConnectionsSaver.save(crawlManager.failedUrls);
        dbWorker.closeAllConnections();
    }

    //непостредственно краулинг
    public void crawl(int maxUrlsLimit) {
        init();
        crawling = true; //поднятие флага
        while (crawling && !crawlManager.isQueueEmpty() && crawlManager.crawledUrlsCount() < maxUrlsLimit) {
            String url = crawlManager.pollUrlFromQueue(); //берётся url из очереди
            try {
                Document document = Jsoup.connect(url).get(); //подключение к серверу
//                System.out.println("Log: Подключение к " + url + " выполнено успешно.");
                if (!crawlManager.isUrlDisallowed(url) && !crawlManager.isUrlCrawled(url)) {
                    String baseDomain = URLWorker.getBaseDomain(url);

                    WebPage webpage = indexator.indexate(document); //индексирование страницы
                    if (dbWorker.saveToDB(webpage)) { //загрузка страницы в бд
                        crawlManager.addToCrawledUrls(url);
                    }
                    Elements links = document.body().getElementsByTag("a"); //парсинг всех ссылок
                    for (Element link :
                            links) {
                        String href = link.attr("href");
                        href = URLWorker.buildURL(baseDomain, href);
                        if (crawlManager.isUrlCrawled(href)) {
                            continue;
                        }
                        if (URLWorker.verifyURL(href)) {
                            crawlManager.addUrlToQueue(href);
                            //System.out.println("Log: " + href + " added to urlsToCrawl.");
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
//                System.out.println("Invalid url: " + url);
            } catch (IOException e) {
//                System.out.println("Connection to " + url + " failed.");
                crawlManager.failedUrls.add(url);
                //fileSaver.save(url);
            }
        }
        failedConnectionsSaver.save(crawlManager.failedUrls);
        dbWorker.closeAllConnections();
    }

    public static void stop() {
        crawling = false;
    }
}








