package org.aksw.leopard.io;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkFinder {

  public static final Logger LOG = LogManager.getLogger(LinkFinder.class);

  public DocumentReader reader = new DocumentReader();

  protected final Queue<String> linksDone = new LinkedList<>();
  private final Queue<String> links = new LinkedList<>();

  private static final int MAX_DEPTH = 1;
  private int currentDepth = 0;
  private URL base = null;

  final UrlValidator urlValidator = new UrlValidator(new String[] {"http", "https"});
  List<String> notallowedExtensions = (Arrays.asList("ico", "jpg", "gif", "png", "pdf", "doc"));

  public static void main(final String[] a) {
    final LinkFinder lf = new LinkFinder("http://www.boxuk.com");
    lf.getPageLinks().forEach(LOG::info);
  }

  public LinkFinder() {

  }

  /**
   *
   * Constructor.
   *
   * @param url
   */
  public LinkFinder(final String url) {
    init(url);
  }

  protected void init(final String url) {
    clear();
    if (urlValidator.isValid(url)) {
      try {
        base = new URL(url);
        add(url);
        initLinks();
      } catch (final MalformedURLException e) {
        LOG.error(e.getLocalizedMessage() + ": " + url, e);
      }
    } else {
      LOG.warn("URL is not vaild.");
    }
  }

  public List<String> getPageLinks(final String url) {
    init(url);
    return getPageLinks();
  }

  public List<String> getPageLinks() {
    return new ArrayList<>(linksDone);
  }

  protected void initLinks() {
    while ((currentDepth < MAX_DEPTH) && !links.isEmpty()) {

      final String e = links.poll();
      try {
        final Document document = reader.documentReader(e);
        final Elements linksOnPage = document.select("a[href]");

        for (final Element page : linksOnPage) {

          String u = page.attr("abs:href").trim();
          if (!u.isEmpty()) {
            if (!u.startsWith("http")) {
              u = "http://".concat(u);
            }
            final URL urlObject = new URL(u);

            final boolean sameHost = urlObject.getHost().equals(base.getHost());
            if (sameHost && !linksDone.contains(u) && urlValidator.isValid(u)) {
              add(u);
            }
          }
        }
      } catch (final IOException ee) {
        LOG.error(ee.getLocalizedMessage(), ee);
      }
      currentDepth++;
    }
  }

  protected void add(String url) throws MalformedURLException {
    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    } else {
      final String p = new URL(url).getFile();
      if (p != null) {
        final int i = p.lastIndexOf(".");
        if (i > -1) {
          final String extension = p.substring(i + 1);
          if (notallowedExtensions.contains(extension.toLowerCase())) {
            return;
          }
        }
      }
    }
    if (!linksDone.contains(url)) {
      links.add(url);
      linksDone.add(url);
    }
  }

  public void clear() {
    base = null;
    currentDepth = 0;
    links.clear();
    linksDone.clear();
  }
}
