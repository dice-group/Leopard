package org.aksw.leopard.extractor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aksw.leopard.io.DocumentReader;
import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.util.Phone;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SemanticTagFactSearcherOld extends AFactSearcher {

  protected DocumentReader docReader = new DocumentReader();

  protected TaskOneReader taskOneReader;

  List<String> phones = new ArrayList<>();
  List<String> countries = new ArrayList<>();

  Phone phone = new Phone();

  public SemanticTagFactSearcherOld(final TaskOneReader taskOneReader) {
    this.taskOneReader = taskOneReader;
  }

  protected void extract(final String url, final String name) {
    final Map<String, Document> docs = taskOneReader.getDocs(url, name);

    for (final Entry<String, Document> entry : docs.entrySet()) {
      // final String pageName = entry.getKey();
      final Document pageDoc = entry.getValue();

      // open graph
      Elements phone;
      phone = pageDoc.getElementsByAttributeValueMatching("property", "og:phone_number");
      if (!phone.isEmpty()) {
        final String n = phone.attr("content").trim();
        if (!phones.contains(n)) {
          phones.add(n);
        }
      }

      Elements country;
      country = pageDoc.getElementsByAttributeValueMatching("property", "og:country-name");
      if (!country.isEmpty()) {
        final String n = country.attr("content").trim();
        if (!countries.isEmpty()) {
          countries.add(n);
        }
      }

      // schema.org
      phone = pageDoc.getElementsByAttributeValueMatching("property", "telephone");
      if (!phone.isEmpty()) {
        final String n = phone.text().trim();
        if (!phones.contains(n)) {
          phones.add(n);
        }
      }

      country = pageDoc.getElementsByAttributeValueMatching("property", "addressLocality");
      if (!country.isEmpty()) {
        final String n = country.attr("content").trim();
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
  }

  @Override
  public void execute(final String url, final String name) {
    extract(url, name);

  }

  @Override
  public void clear() {
    super.clear();

    phones = new ArrayList<>();
    countries = new ArrayList<>();
  }
}
