package parser;

import com.sun.syndication.feed.rss.Enclosure;
import com.sun.syndication.feed.rss.Image;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static j2html.TagCreator.*;

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
        SyndEnclosureImpl enclosure=((SyndEnclosureImpl)entryList.get(countRss).getEnclosures().get(0));
        if(enclosure.getType().compareTo("image/jpeg")==0){
            //Image image=new Image(enclosure.getUrl());
        }


        SyndEntryImpl entry=entryList.get(countRss);

        Map<String, String> newsInfo=new HashMap<String, String>();
        newsInfo.put("Link", entry.getLink());
        newsInfo.put("Title",entry.getTitle());
        newsInfo.put("Image", enclosure.getUrl());
        newsInfo.put("Body", entry.getDescription().getValue());
        if (countRss!=entryList.size()) {
            countRss++;
        }

        return createHTML(newsInfo);
    }


    private String createHTML(Map<String, String> newsInfo){
        String s=b(newsInfo.get("Title")).render()+
                i(newsInfo.get("Body")).render()+
                a().withHref(newsInfo.get("Image")).withText("\n").render()+
                a().withHref(newsInfo.get("Link")).withText(newsInfo.get("Link")).render();
        System.out.println(s);
        return s;
    }

}
