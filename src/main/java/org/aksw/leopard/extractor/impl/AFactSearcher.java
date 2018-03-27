package org.aksw.leopard.extractor.impl;

import org.aksw.leopard.extractor.IExtractor;
import org.aksw.leopard.io.ExtractedData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Add data to the members to extend this class
 *
 * @author Ren&eacute; Speck <speck@informatik.uni-leipzig.de>
 *
 */
public abstract class AFactSearcher implements IExtractor {

  public static final Logger LOG = LogManager.getLogger(AFactSearcher.class);

  public String domiciledIn, foundedDate, phone;
  public double domiciledInScore, foundedDateScore, phoneScore;

  public AFactSearcher() {
    clear();
  }

  @Override
  public ExtractedData results() {
    return ExtractedData.get(//
        this.getClass().getSimpleName(), //
        domiciledIn, foundedDate, phone, //
        domiciledInScore, foundedDateScore, phoneScore//
    );
  }

  @Override
  public void clear() {
    domiciledIn = null;
    foundedDate = null;
    phone = null;

    domiciledInScore = 0D;
    foundedDateScore = 0D;
    phoneScore = 0D;
  }
}
