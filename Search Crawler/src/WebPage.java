import java.util.ArrayList;

public class WebPage {
    //url-адрес
    private String url;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    //код веб-страницы
    private StringBuffer code = new StringBuffer();
    public StringBuffer getCode() {
        return code;
    }

    //заголовки на веб-странице
    private ArrayList<String> headers;
    public ArrayList<String> getHeaders() {
        return headers;
    }
    public void setHeaders(ArrayList<String> headers) {
        this.headers = headers;
    }

    //конструктор
    public WebPage(String url, StringBuffer code, ArrayList<String> headers) {
        this.url = url;
        this.headers = headers;
        this.code = code;
    }
}
