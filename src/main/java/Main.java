import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

public class Main {
    public static void main(String[] args) {
        System.getProperties().put( "proxySet", "true" );
        System.getProperties().put( "socksProxyHost", "149.56.27.45");
        System.getProperties().put( "socksProxyPort", "1080");

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        //System.out.println(111);
        try {
            TelegramBot telegramBot=new TelegramBot();
            telegramBotsApi.registerBot(telegramBot);
            System.out.println(telegramBot.getBotUsername());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
