package org.aksw.leopard.extractor;

import org.aksw.leopard.io.ExtractedData;

public interface IExtractor {

  /**
   * Execute extractor.
   *
   * @param url
   * @param name
   */
  public void execute(final String url, final String name);

  public ExtractedData results();

  public void clear();
}
