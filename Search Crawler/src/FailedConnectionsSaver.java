import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public class FailedConnectionsSaver implements FileSaver {

    private PrintWriter out;
    private String filePath = System.getProperty("user.home") + "\\Desktop\\failed_connections.txt";
    private File file;
    FailedConnectionsSaver() {
        //создание файла с разрушенными соединениями
        file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            out = new PrintWriter(file);
        }
        catch (IOException e) {
//            System.out.println("ERROR: error with creation file for failed connections.");
        }
    }

    @Override
    public void save(Collection collection) {
        for (var item :
                collection) {
            out.println(item);
        }
        out.close();
    }

    @Override
    protected void finalize() {
        out.close();
    }

}
