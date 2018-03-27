package org.aksw.leopard.extractor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aksw.leopard.io.DocumentReader;
import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.util.DomainCountry;
import org.aksw.leopard.util.Phone;
import org.aksw.leopard.util.Serialization;
import org.aksw.leopard.util.YearsParser;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

@SuppressWarnings("unchecked")
public class SemanticTagFactSearcher extends AFactSearcher {

  protected List<String> countries = new ArrayList<>();
  protected TaskOneReader taskOneReader;
  final protected DocumentReader docReader = new DocumentReader();
  final protected Phone phoneUtil = new Phone();

  final static protected String file = "rToL.map";
  final static protected Map<String, String> rToL;
  static {
    Serialization.setRootFolder("tmp_dbpedia");
    rToL = Serialization.deserialize(file, new HashMap<>().getClass());
    if (rToL == null) {
      throw new NullPointerException("Could not deserialize file: " + file);
    }
  }

  /**
   *
   * Constructor.
   *
   * @param taskOneReader
   * @param taskTwoReader
   */
  public SemanticTagFactSearcher(final TaskOneReader taskOneReader) {
    this.taskOneReader = taskOneReader;
  }

  @Override
  public void execute(final String url, final String name) {

    // addressLocality(url, name);

    extractDate(url, name);

    extractPhone(url, name);
  }

  /**
   *
   * @param url
   * @param name
   */
  protected void extractDate(final String url, final String name) {
    // get websites

    // all websites for the given url
    final Set<Integer> years = new HashSet<>();

    for (final Entry<String, Document> entry : taskOneReader.getDocs(url, name).entrySet()) {
      final Document pageDoc = entry.getValue();
      try {
        final String html = pageDoc.html();
        final String pattern = "founded";
        final int i = pageDoc.html().indexOf(pattern);
        if (i > -1) {

          final String sub = html.substring(i + pattern.length(), i + pattern.length() + 20);
          final int min = YearsParser.findYear(sub, 1300, 2018)//
              .stream().min(Integer::compare).get();
          years.add(min);
        }
      } catch (final Exception e) {
      }
    }
    if (!years.isEmpty()) {
      foundedDate = String.valueOf(years.stream().min(Integer::compare).get());
      foundedDateScore = 0.3421D;
    }
  }

  protected void extractPhone(final String url, final String name) {

    // get websites
    final Map<String, Document> docs = taskOneReader.getDocs(url, name);
    final List<String> phones = new ArrayList<>();

    // all pages
    for (final Entry<String, Document> entry : docs.entrySet()) {

      final Document pageDoc = entry.getValue();

      // 3 search for href="tel:
      final Elements linksOnPage = pageDoc.select("a[href*=tel:]");
      if (!linksOnPage.isEmpty()) {
        // take just the first one
        final String p = linksOnPage.first().attr("href").replaceAll("tel:", "");
        if (!p.isEmpty()) {
          phones.add(p);
        }
      }
      Elements phoneElement = null;
      // 2
      /*
       * <span property="telephone"><a href="tel:309-517-7511"
       * class="hidden-desktop">309-517-7511</a></span>
       */
      phoneElement = pageDoc.getElementsByAttributeValueMatching("property", "telephone");

      if (!phoneElement.isEmpty()) {
        final String n = phoneElement.text().trim();
        if (!phones.contains(n)) {
          phones.add(n);
        }
      }

      //
      try {

        phoneElement = pageDoc.getElementsByAttributeValueMatching("property", "og:phone_number");
        if (!phoneElement.isEmpty()) {
          final String n = phoneElement.attr("content").trim();
          if (!phones.contains(n)) {
            phones.add(n);
          }
        }
      } catch (final Exception e) {
        LOG.error(e.getLocalizedMessage(), e);
      }
    }

    final Set<String> p = new LinkedHashSet<>();
    p.addAll(phones);

    final List<String> c = new ArrayList<>();
    final List<String> pp = new ArrayList<>();

    for (final String phone : p) {

      PhoneNumberUtil.getInstance().getSupportedRegions().forEach(code -> {
        final List<PhoneNumber> list = phoneUtil.extractPhoneNumber(phone, code);
        if (!list.isEmpty()) {

          // TODO: take the one with a country
          pp.add(phoneUtil.internationalPhoneNumber(list.get(0)));
          c.add(DomainCountry.countryCodeToCountry.get(list.get(0).getCountryCode()));
        }
      });
    }

    if (!c.isEmpty()) {
      if (c.size() >= 1) {
        phone = pp.get(0); // todo?
        phoneScore = 0.4429D;

        domiciledIn = c.get(0);
        domiciledInScore = 0.8093D;
      } else {
      }
    }
  }

  /**
   * <code>
   protected void extractOld(final String url, final String name) {
     final Map<String, Document> docs = taskOneReader.getDocs(url, name);
     final List<String> phones = new ArrayList<>();
     for (final Entry<String, Document> entry : docs.entrySet()) {

       // final String pageName = entry.getKey();
       final Document pageDoc = entry.getValue();
       if (foundedDate == null) {
         try {
           final String html = pageDoc.html();
           final int i = pageDoc.html().indexOf("founded in");
           if (i > -1) {
             final String year =
                 html.substring(i + "founded in".length(), i + "founded in".length() + 5);
             Integer.valueOf(year);

             foundedDate = year;
             foundedDateScore = 1D;
           }
         } catch (final Exception e) {

         }
       }
       // open graph
       String n;
       Elements phoneElement;
       phoneElement = pageDoc.getElementsByAttributeValueMatching("property", "og:phone_number");
       if (!phoneElement.isEmpty()) {
         n = phoneElement.attr("content").trim();
         if (!phones.contains(n)) {
           phones.add(n);
         }
       }
       Elements countryElement;
       countryElement = pageDoc.getElementsByAttributeValueMatching("property", "og:country-name");
       if (!countryElement.isEmpty()) {
         n = countryElement.attr("content").trim();
         if (!countries.isEmpty()) {
           countries.add(n);
         }
       }

       // schema.org
       phoneElement = pageDoc.getElementsByAttributeValueMatching("property", "telephone");
       if (!phoneElement.isEmpty()) {
         n = phoneElement.text().trim();
         if (!phones.contains(n)) {
           phones.add(n);
         }
       }

       countryElement = pageDoc.getElementsByAttributeValueMatching("property", "addressLocality");
       if (!countryElement.isEmpty()) {
         n = countryElement.attr("content").trim();
         if (!countries.isEmpty()) {
           countries.add(n);
         }
       }
       // TODO:
       // <title>Moe's Bar & Grill | Pointe-Claire, QC | (514) 426-8247</title>

     }
     if (!phones.isEmpty() || !countries.isEmpty()) {
       LOG.info("url: " + url + " -> " + phones + "/" + countries + " " + docs.keySet());
     }

     final List<String> c = new ArrayList<>();
     final List<String> p = new ArrayList<>();
     for (final String phone : phones) {

       PhoneNumberUtil.getInstance().getSupportedRegions().forEach(code -> {
         final List<PhoneNumber> list = phoneUtil.extractPhoneNumber(phone, code);
         if (!list.isEmpty()) {
           p.add(phoneUtil.internationalPhoneNumber(list.get(0)));
           c.add(DomainCountry.countryCodeToCountry.get(list.get(0).getCountryCode()));
         }
       });
     }

     if (!c.isEmpty()) {
       if (c.size() >= 1) {
         phone = p.get(0);
         phoneScore = 0.5D;

         domiciledIn = c.get(0);
         domiciledInScore = 0.7D;
       } else {
       }
     }

     for (final String country : countries) {
       if (rToL.values().contains(country.trim())) {
         // domiciledIn = country.trim();
         // domiciledInScore = 0.5D;
       }
     }
   }

  </code>
   */

  @Override
  public void clear() {
    super.clear();
    countries = new ArrayList<>();
  }

  /**
   * !!!!!!!!!!!!!!Not working well. Just a few matches.!!!!!!!!!!!!!!!!!
   *
   * @param url
   * @param name
   */
  protected void addressLocality(final String url, final String name) {
    // get websites
    final Map<String, Document> docs = taskOneReader.getDocs(url, name);

    // all websites for the given url
    for (final Entry<String, Document> entry : docs.entrySet()) {
      final Document page = entry.getValue();

      final Elements country = page//
          .getElementsByAttributeValueMatching("property", "addressLocality");
      if (!country.isEmpty()) {
        LOG.info("found element: " + country);
        final String value = country.attr("content").trim();

        LOG.info("country: " + value + " " + url + " " + name);
      }
    }
  }
}
