package org.aksw.leopard.extractor.impl;

public class GoogleFactSearcher extends AFactSearcher {

  @Override
  public void execute(final String url, final String name) {
    if (phone == null) {
      LOG.warn("NOT IMPLEMENTED YET");
      phone = "";
      foundedDate = "";
      domiciledIn = "";
    }
  }
}
