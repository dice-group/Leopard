package org.aksw.leopard.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DocumentReader {

  public static final Logger LOG = LogManager.getLogger(DocumentReader.class);

  int timeout = 60 * 60 * 1000; // 1 min

  /**
   *
   * @param url
   * @return
   */
  public Document documentReader(final String url) {
    Document doc = null;
    try {
      final Connection con = Jsoup.connect(url);
      con.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
          + "(KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36")//
          .referrer(url)//
          .header("Accept", "text/html")//
          .header("Accept-Language", "en-US,en")//
          .followRedirects(true)//
          .ignoreContentType(true)//
          .ignoreHttpErrors(true).request().timeout(timeout);

      doc = con.execute().parse();
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
    return doc;
  }

  /**
   *
   * @param file
   * @return
   */
  public Document documentReader(final File file) {
    Document doc = null;
    try {
      doc = Jsoup.parse(file, StandardCharsets.UTF_8.toString());
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
    return doc;
  }

  /**
   * Gets all Docs in a folder.
   *
   * @param folder
   * @return filename to Document
   */
  public Map<String, Document> documentsReader(final String folder) {
    final Map<String, Document> docs = new HashMap<>();

    try (Stream<Path> stream = Files.list(Paths.get(folder))) {

      stream.filter(Files::isRegularFile)//
          .forEach(page -> {
            docs.put(page.toString(), documentReader(page.toFile()));
          });

    } catch (final IOException ee) {
      // LOG.error("Folder not exists: " + folder);
    }
    return docs;
  }
}
