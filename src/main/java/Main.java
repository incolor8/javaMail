import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {
    private  final  static  String  PROPS_FILE =
            "src/main/resources/email.properties.txt"; // сервер, порт, тема сообщения
    private  final  static  String  MAILS_FILE =
            "src/main/resources/emails.txt"; // файл с адресами-получателями
    
    private static List<Integer> fieldLog = new ArrayList<>();
    
    static Logger LOGGER;
    static {
        try(FileInputStream ins = new FileInputStream(
                "src/main/resources/log/log.config")){ // путь к файлу log.properties
            // сам лог сохраняется в корень проекта
            LogManager.getLogManager().readConfiguration(ins);
            LOGGER = Logger.getLogger(Main.class.getName());
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            InputStream input = new FileInputStream(PROPS_FILE);
            Document htmlFile = Jsoup.parse(new File(
                    "src/main/resources/mailForm.html"), "UTF-8");
            
                /**         Файл для тела письма html вида.
                 *          Если отправка обычного текста без форматирования,
                 *          то можно использовать email.properties.txt
                 * */
                
            Reader reader = new InputStreamReader(input);
            Properties pr = new Properties();
            pr.load(reader);
            
            //из файла email.properties
            SendMail.SMTP_SERVER    = pr.getProperty ("server" );
            SendMail.SMTP_Port      = pr.getProperty ("port"   );
            SendMail.FILE_PATH      = PROPS_FILE;
    
    
            List empty = fieldLog;
            List<String> logList = empty;
            String bodyHtml = htmlFile.outerHtml();

            InputStream inputMails = new FileInputStream(MAILS_FILE);
            Reader readMails = new InputStreamReader(inputMails);
            pr.load(readMails);
            ArrayList<String> mailList = new ArrayList<>();

            for (int index = 1; index < 4; index++) {
                String date = new SimpleDateFormat("dd.MM.yy H:m:s").format(new Date());
                String emailTo = pr.getProperty("to" + index);
                if (emailTo == null) {
                    continue;
                }
                mailList.add(emailTo);
                String mail = mailList.get(index - 1);
                SendMail se = new SendMail(mail, htmlFile.title() + " Уникальный код: " + new GregorianCalendar().getTime().getTime());
    
                LOGGER.log(Level.INFO, "Отправка сообщения на " + mail);
                se.sendMessage(bodyHtml + "\nСообщение отправлено в "  + date
                        + " на " + mail + ". Рассылка производится автоматически");
                logList.add(mail);
                
                TimeUnit.SECONDS.sleep(3); // sleep 3s.
                // с учетом sleep рассылка на 1000 писем ~ 50 минут.
            }
            
            LOGGER.info("Количество отправленных писем: " + logList.size());
            
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING,"Ошибка отправки письма!" , e);
            System.exit(1);
        }
    }
}
