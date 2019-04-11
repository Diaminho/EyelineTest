import bot.TelegramBot;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Main {
    public static void main(String[] args) {
        //настройки прокси
        System.getProperties().put( "proxySet", "true" );
        System.getProperties().put( "socksProxyHost", "149.56.27.45");
        System.getProperties().put( "socksProxyPort", "1080");

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        TelegramBot telegramBot=new TelegramBot();
        try {
            telegramBotsApi.registerBot(telegramBot);
            System.out.println(telegramBot.getBotUsername());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}