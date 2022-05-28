import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class CrawlManager {

    //очередь ссылок на краулинг
    private Queue<String> urlsToCrawl = null;
    //пустая ли очередь
    boolean isQueueEmpty(){
        return (urlsToCrawl.size() == 0);
    }
    //кол-во ссылок в очереди
    int queueSize() {
        return urlsToCrawl.size();
    }
    //добавление ссылки в очередь
    void addUrlToQueue(String url) {
        urlsToCrawl.add(url);
    }
    //удаление ссылки из очереди
    String pollUrlFromQueue() {
        return urlsToCrawl.poll();
    }
    //очистка очереди
    void clearQueue() {
        urlsToCrawl.clear();
    }
    //содержится ли ссылка в очереди
    boolean queueContainsUrl(String url) {
        return urlsToCrawl.contains(url);
    }

    //множество ссылок на уже просмотренные страницы
    private LinkedHashSet<String> crawledUrls = null;
    //была ли уже просмотрена страница
    boolean isUrlCrawled(String url) {
        return crawledUrls.contains(url);
    }
    //добавить страницу в просмотренные
    void addToCrawledUrls(String url) {
        crawledUrls.add(url);
    }
    //количество просмотренных страниц
    int crawledUrlsCount() {
        return crawledUrls.size();
    }
    //очистить список просмотренных веб-страниц
    void clearCrawled() {
        crawledUrls.clear();
    }

    //список разрушенных соединений
    ArrayList<String> failedUrls;
    //множество ссылок на веб-страницы, которые не нужно просматривать
    private HashMap<String, ArrayList<String>> disallowedUrls = null;
    private HashMap allowedUrls = null;
    //чтение файла robots.txt
    void readRobotsTxt(String url) {
        String host = URLWorker.getBaseDomain(url); //получаем главный домен страницы
        //если robots.txt этого домена уже был прочтён, то выполнение метода заканчивается
        if (disallowedUrls.containsKey(host) || allowedUrls.containsKey(host)) {
            return;
        }
        try {
            URL robotFileUrl = new URL(host + "/robots.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(robotFileUrl.openStream()));
            //создаем массивы для разрешенных и неразрешенных адресов
            ArrayList<String> allowed = new ArrayList<String>();
            ArrayList<String> disallowed = new ArrayList<String>();
            //построчно считываем файл
            String line;
            while ((line = reader.readLine()) != null) {
                //форматируем строку и добавляем в соответсвующий массив
                if (line.startsWith("Disallow:")) {
                    String disallowedUrl = line.substring("Disallow:".length());
                    int commentIndex = disallowedUrl.indexOf("#");
                    if (commentIndex != -1) {
                        disallowedUrl = disallowedUrl.substring(0, commentIndex);
                    }
                    disallowedUrl = disallowedUrl.trim();
                    disallowedUrl = URLWorker.buildURL(host, disallowedUrl);
                    disallowed.add(disallowedUrl);
                }
                if (line.startsWith("Allow:")) {
                    String allowedUrl = line.substring("Allow:".length());
                    int commentIndex = allowedUrl.indexOf("#");
                    if (commentIndex != -1) {
                        allowedUrl = allowedUrl.substring(0, commentIndex);
                    }
                    allowedUrl = allowedUrl.trim();
                    allowedUrl = URLWorker.buildURL(host, allowedUrl);
                    allowed.add(allowedUrl);
                }
            }
            //добавляем главный домен и списки адресов в соответсвующие множества
            allowedUrls.put(host, allowed);
            disallowedUrls.put(host, disallowed);
        } catch (MalformedURLException e) {
            System.out.println("ERROR: Invalid url to read robot.txt.");
        } catch (IOException e) {
            System.out.println("ERROR: IOException with reading robot.txt.");
        }
    }
    //добавлена ли страница в множество тех, которые не нужно просматривать
    boolean isUrlDisallowed(String url) {
        String host = URLWorker.getBaseDomain(url);
        if (disallowedUrls.containsKey(host)) {
            return disallowedUrls.get(host).contains(url);
        }
        return false;
    }
    Crawler crawler = null; //краулеры
    //конструктор
    private CrawlManager() {

        urlsToCrawl = new ConcurrentLinkedQueue<String>();
        crawledUrls = new LinkedHashSet<String>();
        disallowedUrls = new HashMap();
        allowedUrls = new HashMap();
        failedUrls = new ArrayList<String>();
        crawler = new Crawler();

    }
    //реализация синглтона-классхолдера
    private static class CrawlManagerHolder {
        public static final CrawlManager HOLDER_INSTANCE = new CrawlManager();
    }
    public static CrawlManager getInstance() {
        return CrawlManagerHolder.HOLDER_INSTANCE;
    }
}

