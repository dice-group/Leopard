package org.aksw.leopard.extractor.impl;

import org.aksw.leopard.util.DomainCountry;

/**
 *
 * @author Ren&eacute; Speck <speck@informatik.uni-leipzig.de>
 *
 */
public class DomainFactSearcher extends AFactSearcher {

  public static final String defaultDomiciledIn = "United States";

  /**
   * Finds top level domain in url and get the counrty code for it. Sets defaultCountry for .com.
   */
  @Override
  public void execute(final String url, final String name) {

    final String tld = DomainCountry.getTLD(url);
    final String country = DomainCountry.domainToCountry.get(tld);
    if (country != null) {
      domiciledIn = country;
      domiciledInScore = 0.5D;
    } else {
      domiciledIn = defaultDomiciledIn;
      domiciledInScore = 0.1;
    }

    String str = name;
    str = str.replaceAll("[^0-9]+", " ").trim();
    if ((str.length() == 4) && !str.contains(" ")) {
      final int j = Integer.valueOf(str);
      if ((j < 2018) && (j > 1300)) {
        foundedDate = str;
        foundedDateScore = 0.9;
      }
    }

    /**
     * <code>
     // TODO:
     str = name;
     final List<String> countries = new ArrayList<>();
     countries.add("UK");
     countries.add("US");
     countries.add("FR");
     // countries.add("Inc");
     countries.add("GmbH");
     countries.add("AG");
     countries.add("Deutschland");
     countries.add("United States");
     countries.add("England");
     countries.add("Canada");
     for (final String c : countries) {
       if (str.matches(".*\\b" + c + "\\b.*")) {
         LOG.info(c + ": " + str + " " + url);
       }
     }
     </code>
     */
  }
}
