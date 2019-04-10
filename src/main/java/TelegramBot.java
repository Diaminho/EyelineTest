import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import parser.RssParser;

import java.util.ArrayList;
import java.util.List;


public class TelegramBot extends TelegramLongPollingBot {
    private static final String botToken="888333065:AAGaAeJc5v9VgETNAGW1ZFf7ALjjQLRzCIo";
    private static final String botUserName="EyeLineTestBot";
    RssParser rssParser;

    public TelegramBot() {
        this.rssParser = new RssParser();
    }

    /**
     * Метод для приема сообщений.
     * @param update Содержит сообщение от пользователя.
     */
    /*
    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        System.out.println(message);
        sendMsg(update.getMessage().getChatId().toString(), message);
    }*/

    /**
     * Метод возвращает имя бота, указанное при регистрации.
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return botUserName;
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Метод создает кнопки "Далее" и "Сначала"
     * @param sendMessage
     */
    public synchronized void setButtons(SendMessage sendMessage) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("Сначала"));

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add(new KeyboardButton("Далее"));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }


    /**
     * Метод обрабатывает команды
     * @param update
     */
    @Override
    public void onUpdateReceived(Update update) {

        //https://lenta.ru/rss/news

        if(update.hasMessage()) {
            Message message=update.getMessage();
            String textMessage=message.getText();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.enableMarkdown(true);
            sendMessage.setText("0");
            setButtons(sendMessage);

            if (textMessage.compareTo("Сначала")==0){
                doStart(sendMessage);
            }
            else if (textMessage.compareTo("Далее")==0){
                goNext(sendMessage);
            }

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        } else  if(update.hasCallbackQuery()) {
            update.getCallbackQuery();
        }
    }



    /**
     * ....
     */
    private void doStart(SendMessage sendMessage){
        System.out.println("Start");
        rssParser.setCountRss(0);
        sendMessage.setText(rssParser.ReadRss("https://lenta.ru/rss/news"));
    }

    private void goNext(SendMessage sendMessage){
        System.out.println("Next");
        sendMessage.setText(rssParser.ReadRss("https://lenta.ru/rss/news"));
    }

}
