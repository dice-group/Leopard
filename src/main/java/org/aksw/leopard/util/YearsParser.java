package org.aksw.leopard.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class YearsParser {

  public static final Logger LOG = LogManager.getLogger(YearsParser.class);

  /**
   * Finds numbers in a specific range in data, e.g. 1850 to 2017
   *
   * @param data
   * @return numbers
   */
  public static Set<Integer> findYear(final String data, final int min, final int max) {
    final Set<Integer> years = new HashSet<>();
    final Pattern namePtrn = Pattern.compile("\\b[1-2][0-9]{3}\\b");
    final Matcher nameMtchr = namePtrn.matcher(data);
    while (nameMtchr.find()) {
      final String find = nameMtchr.group(0);
      try {
        final int j = Integer.valueOf(find);
        if ((j < max) && (j > min)) {
          years.add(j);
        }
      } catch (final Exception e) {
        LOG.error(e.getLocalizedMessage(), e);
      }
    }
    return years;
  }
}
