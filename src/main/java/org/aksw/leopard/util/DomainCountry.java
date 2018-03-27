package org.aksw.leopard.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DomainCountry {
  public static final Logger LOG = LogManager.getLogger(DomainCountry.class);

  public static final String file = "data/CountryDomain.txt";
  public static final String file2 = "data/countrycode.txt";

  public static Map<String, String> domainToCountry = new HashMap<>();
  public static Map<String, String> domainToISOCode = new HashMap<>();
  public static Map<String, String> countryToDomain = new HashMap<>();
  public static Map<Integer, String> countryCodeToCountry = new HashMap<>();

  static {
    try {
      for (final String line : Files.readAllLines(Paths.get(file))) {
        if (!line.trim().isEmpty()) {
          final String[] split = line.split("\t");
          domainToCountry.put(split[3], split[0]);
          countryToDomain.put(split[0], split[3]);
          domainToISOCode.put(split[3], split[1]);
        }
      }
    } catch (final Exception e) {
      LOG.error(e.getLocalizedMessage(), e);
    }

    try {
      for (final String line : Files.readAllLines(Paths.get(file2))) {
        if (!line.trim().isEmpty()) {
          final String[] split = line.split("\t");
          try {
            countryCodeToCountry.put(Integer.valueOf(split[1]), split[0]);
          } catch (final Exception e) {
          }
        }
      }
    } catch (final Exception e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }

  public static String getTLD(final String url) {
    try {
      final URL urlObj = new URL(url);
      final String[] domainNameParts = urlObj.getHost().split("\\.");
      return ".".concat(domainNameParts[domainNameParts.length - 1]);
    } catch (final MalformedURLException e) {
      LOG.error("Not found url: " + url);
    }
    return "";
  }
}
