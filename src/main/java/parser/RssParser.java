package parser;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, который парсит RSS из URL источника
 */
public class RssParser {
    /**
     * номер текущей новости
     */
    private int countRss;
    /**
     * список всех доступных новостей
     */
    private List<SyndEntryImpl> entryList;

    public int getCountRss() {
        return countRss;
    }

    public void setCountRss(int countRss) {
        this.countRss = countRss;
    }

    /**
     * Метод парсит RSS из URL
     * @param url
     * @return Возвращает заголовок новости и ссылку на нее (пока что)
     */
    public String ReadRss(String url){
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
        System.out.println(feed);
        entryList=new ArrayList<>(feed.getEntries());
        String title=entryList.get(countRss).getTitle()+"\n"+entryList.get(countRss).getLink();
        feed.getEntries();
        if (countRss!=entryList.size()) {
            countRss++;
        }
        return title;
    }


}
