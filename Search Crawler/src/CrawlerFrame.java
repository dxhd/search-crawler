import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class CrawlerFrame extends JFrame {

    public static void main(String[] args) {

        CrawlerFrame CF = new CrawlerFrame();
        CF.show();
    }

    private JTextField startUrlTextField;
    private JTextField maxLimitTextField;
    private JButton startButton;
    private JLabel crawledCountLabel;
    private boolean isCrawling;

    private CrawlManager crawlManager = CrawlManager.getInstance();

    //конструктор
    public CrawlerFrame(){
        setTitle("Search Crawler");
        this.setPreferredSize(new Dimension(240, 248));//Size(248, 200);
        this.setLocation(360, 240);
        this.setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);




        JPanel searchPanel = new JPanel();
        GridBagConstraints constraints;
        GridBagLayout layout = new GridBagLayout();
        searchPanel.setLayout(layout);


        //this.add(searchPanel);





        //установка startUrlLabel
        JLabel startUrlLabel = new JLabel("Start url");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
//        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(20, 20, 5, 20);
        layout.setConstraints(startUrlLabel, constraints);

//        startUrlLabel.setForeground(Color.lightGray);

        searchPanel.add(startUrlLabel);

        //установка текстового поля для начального url
        startUrlTextField = new JTextField();
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, 20, 0, 20);
        layout.setConstraints(startUrlTextField, constraints);

//        startUrlTextField.setBackground(Color.GRAY);
//        startUrlTextField.setForeground(Color.white);
//        startUrlTextField.setBorder(new LineBorder(Color.lightGray, 2, true));
        searchPanel.add(startUrlTextField);





        //установка maxLimitLabel
        JLabel maxLimitLabel = new JLabel("Limit url to download:");
        constraints = new GridBagConstraints();
//        constraints.anchor = GridBagConstraints.NORTH;
//        constraints.fill = GridBagConstraints.HORIZONTAL;
//        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 20, 10, 5);
        layout.setConstraints(maxLimitLabel, constraints);

//        maxLimitLabel.setForeground(Color.lightGray);

        searchPanel.add(maxLimitLabel);

        //установка текстового поля для ограничения количество просматриваемых страниц
        maxLimitTextField = new JTextField(4);
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(20, 0, 20, 20);
        layout.setConstraints(maxLimitTextField, constraints);

//        maxLimitTextField.setBackground(Color.GRAY);
//        maxLimitTextField.setForeground(Color.white);
//        maxLimitTextField.setBorder(new LineBorder(Color.lightGray, 2, true));



        searchPanel.add(maxLimitTextField);

        startButton = new JButton("Start crawling");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSearch();
            }
        });
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
//        constraints.gridwidth = GridBagConstraints.RELATIVE;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 20, 20, 20);
        layout.setConstraints(startButton, constraints);
        searchPanel.add(startButton);

//        searchPanel.setForeground(Color.lightGray);
//        searchPanel.setBackground(Color.darkGray);

        crawledCountLabel = new JLabel();
        crawledCountLabel.setEnabled(false);
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 20, 20, 20);
        layout.setConstraints(crawledCountLabel, constraints);
        searchPanel.add(crawledCountLabel);


        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(searchPanel, BorderLayout.NORTH);

        this.pack();

//        this.setBackground(Color.darkGray);
//        this.setForeground(Color.lightGray);


        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });
    }

    //действие при закрытии фрейма
    private void actionExit() {
        System.runFinalization();
        System.exit(0);
    }


    //действие по нажатию кнопки поиска
    private void actionSearch() {
        //если краулинг уже начат, то по нажатию на кнопку Стоп он остановится
        if (this.isCrawling) {
            Crawler.stop();
            isCrawling = false;
            return;
        }
        //список ошибок
        ArrayList<String> errorList = new ArrayList<String>();
        //начальный url
        String startUrl = startUrlTextField.getText().trim();
        //проверка наличия и валидности url
        if (startUrl.length() < 1) {
            errorList.add("Missing start url.");
        }
        else if (!URLWorker.verifyURL(startUrl)) {
            errorList.add("Invalid start url.");
        }
        //проверка наличия и валидности ограничения на количество url
        int maxUrls = 0;
        String limit = maxLimitTextField.getText().trim();
        if (limit.length() > 0) {
            try {
                maxUrls = Integer.parseInt(limit);
            } catch (NumberFormatException e) {
                errorList.add("Enter the number.");
            }
            if (maxUrls < 1) {
                errorList.add("Invalid limit value.");
            }
        }
        //вывод сообщений об ошибках на экран
        if (errorList.size() > 0) {
            StringBuffer message = new StringBuffer();
            for (int i = 0; i < errorList.size(); i++) {
                if (i + 1 == errorList.size()) {
                    message.append(errorList.get(i));
                    continue;
                }
                message.append(errorList.get(i) + "\n");
            }
            showError(message.toString());
            return;
        }
        //начать краулинг в новом потоке
        startCrawling(startUrl, maxUrls);
    }

    //метод, начинающий краулинг в новом потоке
    private void startCrawling (String startUrl, int maxUrlsLimit) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //меняем состояние элементов окна при начале краулинга
                setCursor(Cursor.WAIT_CURSOR);
                startUrlTextField.setEnabled(false);
                maxLimitTextField.setEnabled(false);
                startButton.setText("Stop");
                update(getGraphics());
                isCrawling = true;
                //добавление начального url в очередь на краулинг
                crawlManager.addUrlToQueue(startUrl);


                //очистка уже просмотренных веб-страниц, если краулинг запускается не в первый раз
                crawlManager.clearCrawled();
                //если введено число и это число больше нуля, то ограничить кол-во просматриваемых страниц
                if (maxUrlsLimit > 0)
                    crawlManager.crawler.crawl(maxUrlsLimit);
                else
                    crawlManager.crawler.crawl();
                //очистка очереди на краулинг после завершения
                crawlManager.clearQueue();
                //меняем состояние элементов окна при окончании краулинга
                isCrawling = false;
                startUrlTextField.setEnabled(true);
                maxLimitTextField.setEnabled(true);
                startButton.setText("Start crawling");
                /* ДОБАВЛЕНИЕ ИЗМЕНЕНИЯ ТЕКСТА ЛЭЙБЛА */
                crawledCountLabel.setText("Crawled urls: " + crawlManager.crawledUrlsCount());
                if (!crawledCountLabel.isEnabled()) {
                    crawledCountLabel.setEnabled(true);
                }
                setCursor(Cursor.getDefaultCursor());
            }
        });
        thread.start();
    }

    //вывод диалогового окна с ошибками на экран
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
