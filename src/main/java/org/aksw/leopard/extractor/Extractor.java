package org.aksw.leopard.extractor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.leopard.io.ExtractedData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author Ren&eacute; Speck <speck@informatik.uni-leipzig.de>
 *
 */
public class Extractor {

  public static final Logger LOG = LogManager.getLogger(Extractor.class);

  /**
   * Results
   */
  public Set<ExtractedData> results = new HashSet<>();

  /**
   *
   * Constructor executes, stores results and cleans all extractor impl.
   *
   * @param extractors
   * @param url
   * @param name
   */
  public Extractor(final List<IExtractor> extractors, final String url, final String name) {
    for (final IExtractor extractor : extractors) {
      execute(extractor, url, name);
    }
  }

  /**
   * Executes, stores results and cleans extractor impl.
   *
   * @param extractor
   * @param url
   * @param name
   */
  protected void execute(final IExtractor extractor, final String url, final String name) {
    extractor.execute(url, name);
    results.add(extractor.results());
    extractor.clear();
  }
}
