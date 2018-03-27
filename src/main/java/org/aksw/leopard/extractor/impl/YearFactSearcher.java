package org.aksw.leopard.extractor.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.util.YearsParser;
import org.jsoup.nodes.Document;

public class YearFactSearcher extends AFactSearcher {
  protected TaskOneReader taskOneReader;

  public YearFactSearcher(final TaskOneReader taskOneReader) {

    this.taskOneReader = taskOneReader;
  }

  @Override
  public void execute(final String url, final String name) {
    final Map<String, Integer> pageToYear = new HashMap<>();

    // finds the smallest 4 digit in a specific range
    {
      for (final Entry<String, Document> doc : taskOneReader.getDocs(url, name).entrySet()) {
        final String text = doc.getValue().text();

        final Set<Integer> years = YearsParser.findYear(text, 1900, 2018);
        if (!years.isEmpty()) {
          final int year = years.stream().min(Integer::compare).get();
          pageToYear.put(doc.getKey(), year);
        }
      }
    }
    /**
     * <code>
    // try with frequency
    final Map<Integer, Integer> yearToFreq = new HashMap<>();
    for (final Entry<String, Integer> e : pageToYear.entrySet()) {
      yearToFreq.put(e.getValue(),
          yearToFreq.get(e.getValue()) == null ? 1 : yearToFreq.get(e.getValue()) + 1);
    }
    if (!yearToFreq.isEmpty()) {
      final Entry<Integer, Integer> max = yearToFreq.entrySet().stream()//
          .filter(e -> e.getValue() == Collections.max(yearToFreq.values()))
          .min(Entry.comparingByKey())//
          .get();
      foundedDate = String.valueOf(max.getKey());
      foundedDateScore = 0.01D;
    }
    
    </code>
     */
    {
      if (!pageToYear.values().isEmpty()) {
        final int year = pageToYear.values().stream().min(Integer::compare).get();
        foundedDate = String.valueOf(year);
        foundedDateScore = 0.001D;
      }
    }
  }
}
