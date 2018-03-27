package org.aksw.leopard.google;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.aksw.leopard.io.DocumentReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Phone {

  public static final Logger LOG = LogManager.getLogger(Phone.class);
  public DocumentReader webreader = new DocumentReader();

  public String getGoogleSearchURL(String query) {
    String url = "https://www.google.com/search?hl=en&q=%s";
    try {
      query = URLEncoder.encode(query, Charset.forName("UTF-8").toString());
    } catch (final UnsupportedEncodingException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
    url = String.format(url, query);
    return url;
  }

  public static void main(final String[] args) {

    final Phone phone = new Phone();
    final String url = phone.getGoogleSearchURL("Arends Inspection");

    final Document doc = phone.webreader.documentReader(url);
    final String tag = "#rhs_block";
    // "itemprop=\"addressCountry\"";
    // doc.select("#mp-itn b a");
    final Elements elements = doc.select(tag);
    final Iterator<Element> iter = elements.iterator();

    // LOG.info(elements.toString());

    final String content = "";
    while (iter.hasNext() && content.isEmpty()) {
      final Element e = iter.next();
      final String p = e.toString();
      if (p.indexOf("Phone") > -1) {
        // content = p;
        break;
      } else {
        iter.remove();
      }
    }

    final int index = elements.toString().indexOf("Phone");
    if (index > -1) {
      LOG.info(elements.toString().substring(index, index + 170));
    }

  }

}
