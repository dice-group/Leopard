package org.aksw.leopard.extractor.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aksw.leopard.io.DocumentReader;
import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.util.DomainCountry;
import org.aksw.leopard.util.Phone;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 *
 * @author Ren&eacute; Speck <speck@informatik.uni-leipzig.de>
 *
 */
public class WebsiteFactSearcher extends AFactSearcher {

  protected DocumentReader docReader = new DocumentReader();
  protected Phone phoneUtil = new Phone();

  protected TaskOneReader taskOneReader;

  /**
   *
   * Constructor.
   *
   * @param taskOneReader
   */
  public WebsiteFactSearcher(final TaskOneReader taskOneReader) {
    this.taskOneReader = taskOneReader;
  }

  @Override
  public void clear() {
    super.clear();
  }

  @Override
  public void execute(final String url, final String name) {

    final Map<String, Document> docs = taskOneReader.getDocs(url, name);

    final Map<String, List<String>> phoneSet = new HashMap<>();

    for (final Entry<String, Document> entry : docs.entrySet()) {
      final String pageName = entry.getKey();
      final Document pageDoc = entry.getValue();

      final List<String> tmp = phone(pageDoc, url);
      if (!tmp.isEmpty()) {
        phoneSet.put(pageName, tmp);
      }
      // LOG.info(tmp);
    }
    // all pages done

    // find number with highest frequency
    final Map<String, Integer> map = new HashMap<>();
    // page to phone numbers
    for (final Entry<String, List<String>> entry : phoneSet.entrySet()) {
      final Set<String> phones = new HashSet<>();
      phones.addAll(entry.getValue());
      for (final String phone : phones) {
        map.put(phone, map.get(phone) == null ? 0 : map.get(phone) + 1);
      }
    }
    if (!map.isEmpty()) {
      phone = Collections.max(map.entrySet(), Map.Entry.comparingByValue()).getKey();
      phoneScore = 0.5;
      final String code = getCode(url);
      final List<PhoneNumber> phones = phoneUtil.extractPhoneNumber(phone, code);

      if (phones.size() > 0) {
        final String country =
            DomainCountry.countryCodeToCountry.get(phones.get(0).getCountryCode());
        if (country != null) {
          domiciledIn = country;
          domiciledInScore = 0.8;
        } else {
          // domiciledIn = "United States";
        }
      }
    }
  }

  private String getCode(final String url) {
    String tld = DomainCountry.getTLD(url);
    // TODO:
    if (tld.isEmpty() || tld.equals(".com")) {
      tld = ".us";

    }
    final String code = DomainCountry.domainToISOCode.get(tld);
    return code;

  }

  // TODO: there are com websites in UK and DE!
  protected List<String> phone(final Document doc, final String url) {
    final List<String> v = new ArrayList<>();

    final String text = doc.text();
    final String code = getCode(url);
    final List<PhoneNumber> phones = phoneUtil.extractPhoneNumber(text, code);
    if (phones.size() > 0) {
      v.add(phoneUtil.internationalPhoneNumber(phones.get(0)));
    }
    return v;
  }

  protected List<String> _domiciledIn(final Document doc) {

    // String prefix = "http://www.linkedin.com";
    // final String s = "https://www.linkedin.com/company/metropool-inc-/";

    final String content = doc.toString();

    // "itemprop=\"addressCountry\"";
    // doc.select("#mp-itn b a");
    final String tag = "country-name";

    final Elements elements = doc.select(tag);
    final Iterator<Element> iter = elements.iterator();

    while (iter.hasNext()) {
      final Element e = iter.next();
      LOG.debug(e.data());
    }

    final int index = content.indexOf(tag);
    if (index > 0) {
      LOG.info(content.substring(index,
          (index + 50) > content.length() ? content.length() : index + 50));
    }
    return new ArrayList<>();
  }
}
