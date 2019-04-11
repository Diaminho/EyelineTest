package bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import parser.RssParser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, реализующий бота для получения постов из RSS источников в телеграм
 */
public class TelegramBot extends TelegramLongPollingBot {
    private static final String botToken="TOKEN";
    private static final String botUserName="BotName";
    RssParser rssParser;
    private ReplyKeyboardMarkup replyKeyboardMarkup;
    LocalDateTime time;

    private String url="https://lenta.ru/rss/news";
    //протестированные источники
    //"https://lenta.ru/rss/news"  "https://evo-lutio.livejournal.com/data/rss" "https://aleksei-turchin.livejournal.com/data/rss"

    public TelegramBot() {
        this.rssParser = new RssParser();
        rssParser.readRss(url);
        rssParser.setFirstTitle();
        time=LocalDateTime.now();
    }

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
        replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("Сначала"));

        // Вторая строка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавление кнопки во вторую строку клавиатуры
        keyboardSecondRow.add(new KeyboardButton("Далее"));

        // Добавляем все строки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    /**
     * Метод обрабатывает команды
     * @param update
     */
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()) {
            Message message=update.getMessage();
            String textMessage=message.getText();
            //по умолчанию считаем, что в посте нет изображений
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText("0");
            sendMessage.disableWebPagePreview();
            sendMessage.enableMarkdown(true);
            sendMessage.setParseMode("HTML");
            setButtons(sendMessage);

            if (textMessage.compareTo("Сначала")==0 || textMessage.compareTo("Сначала*")==0){
                doStart(sendMessage);
            }
            else if (textMessage.compareTo("Далее")==0){
                goNext(sendMessage);
            }
        } else  if(update.hasCallbackQuery()) {
            update.getCallbackQuery();
        }
    }

    /**
     * Функция для чтения первого поста в ленте
     * @param sendMessage
     */
    private void doStart(SendMessage sendMessage){
        System.out.println("Begin");
        //устанавливаем номер текущего поста в 0
        rssParser.setCountRss(0);
        if (sendMessage.getText().compareTo("Сначала*")==0) {
            sendMessage.setText(rssParser.readRss(url));
        }
        else {
            sendMessage.setText(rssParser.readRss());
        }
        sendInfo(sendMessage);
    }

    /**
     * Функция для чтения следующего поста в ленте
     * @param sendMessage
     */
    private void goNext(SendMessage sendMessage){
        System.out.println("Next");
        String oldFirstPost=rssParser.getFirstTitle();
        //проверка, прошло ли 10 минут
        if (LocalDateTime.now().isAfter(time.plusMinutes(10))) {
            rssParser.readRss(url);
            rssParser.setFirstTitle();
            //ставим текущее время в качестве времени последнего обновления
            time=LocalDateTime.now();
            //проверяем, изменился ли заголовок первого поста изменился
            if (oldFirstPost.compareTo(rssParser.getFirstTitle()) != 0) {
                replyKeyboardMarkup.getKeyboard().get(0).set(0, "Сначала*");
            }
        }
        sendMessage.setText(rssParser.readRss());
        sendInfo(sendMessage);
    }

    /**
     * Функция для оправки сообшения или фото, в зависимости от наличия фотографии в посте
     * @param sendMessage
     */
    private void sendInfo(SendMessage sendMessage){
        String photo=rssParser.getCurrentPhoto();
        try {
            //если фотографии не в посте, то оправляем как текстовое сообщение
            if (photo.compareTo("")==0) {
                execute(sendMessage);
            } else {
                SendPhoto message=new SendPhoto();
                message.setPhoto(photo);
                message.setCaption(sendMessage.getText().substring(0,200));
                message.setChatId(sendMessage.getChatId());
                message.setReplyMarkup(replyKeyboardMarkup);
                execute(message);
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}