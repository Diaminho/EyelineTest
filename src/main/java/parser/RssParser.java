package parser;

import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, который парсит RSS из URL источника
 */
public class RssParser {
    //номер текущей новости
    private int countRss;
    //список всех доступных новостей
    private List<SyndEntryImpl> entryList;
    //заголовок первого поста из списка
    private String firstTitle;
    //фотография текущего поста
    private String currentPhoto;

    public String getFirstTitle() {
        return firstTitle;
    }

    public void setFirstTitle() {
        firstTitle=entryList.get(0).getTitle();
    }

    public int getCountRss() {
        return countRss;
    }

    public void setCountRss(int countRss) {
        this.countRss = countRss;
    }

    public String getCurrentPhoto() {
        return currentPhoto;
    }

    public void setCurrentPhoto(String currentPhoto) {
        this.currentPhoto = currentPhoto;
    }

    /**
     * Метод парсит RSS из URL
     * @param url
     * @return Возвращает заголовок новости и ссылку на нее (пока что)
     */
    public String readRss(String url){
        URL feedSource = null;
        try {
            feedSource = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;
        try {
            feed = input.build(new XmlReader(feedSource));
        } catch (FeedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        entryList=new ArrayList<>(feed.getEntries());
        return readRss();
    }

    /**
     * Метод читает пост из списка доступных постов и преобразует его в строку
     * @return
     */
    public String readRss(){
        List enclosureList=entryList.get(countRss).getEnclosures();
        SyndEnclosureImpl enclosure=null;
        if (enclosureList.size()!=0) {
            enclosure =(SyndEnclosureImpl) enclosureList.get(0);
        }

        String imgUrl=null;
        if(enclosure!=null && enclosure.getType().compareTo("image/jpeg")==0){
            imgUrl=enclosure.getUrl();
        }

        SyndEntryImpl entry=entryList.get(countRss);

        if (countRss<entryList.size()) {
            countRss++;
        }

        String content=Jsoup.parse(entry.getDescription().getValue()).text();
        content= (content.length()>500)? content.substring(0,500): content;

        String title=entry.getTitle();
        imgUrl= (imgUrl==null) ? getImgUrl(entry.getDescription().getValue()): imgUrl;
        String link=entry.getLink();

        //строка для постов без изображений
        String res="<b>"+title+"</b> \n"+content+"\n"+link;

        if (imgUrl!=null){
            currentPhoto=new String(imgUrl);
            content= (content.length()>200) ? content.substring(0,200): content;
            res=link+"\n"+title+"\n\n"+content;
            res= (res.length()>200) ? res.substring(0,200) : res;
        } else {
            currentPhoto = "";
        }
        return res;
    }

    /**
     * Функция получает URL первой картинки из HTML-строки
     * @param content
     * @return Возвращает ссылку на изображение или null
     */
    private String getImgUrl(String content) {
        Elements imgs = Jsoup.parse(content).getElementsByTag("img");
        if (imgs.size()>0){
            return imgs.get(0).absUrl("src");
        }
        else {
            return null;
        }
    }
}