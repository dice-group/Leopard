package org.aksw.leopard.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.aksw.leopard.Main;
import org.aksw.leopard.io.DocumentReader;
import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.io.taskreader.TaskTwoReader;
import org.aksw.leopard.io.taskreader.Voc;
import org.aksw.leopard.pipe.DownloadData;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

/**
 * 1. prints the IDs for which we do not have a website downloaded <br>
 * 2.
 *
 * @author Ren&eacute; Speck <speck@informatik.uni-leipzig.de>
 *
 */
public class Test {

  public static final Logger LOG = LogManager.getLogger(Main.class);

  final static String file = "urlsNotFound.nt";

  public static void main(final String[] args) {

    // 1
    // Test.notFound();

    // 2
    Test.downloadNotFound();
  }

  protected static InputStream connect(final String url) {

    new StringBuffer();
    HttpURLConnection conn = null;
    InputStream is = null;
    try {

      final URL obj = new URL(url);
      conn = (HttpURLConnection) obj.openConnection();
      conn.setReadTimeout(5000);
      conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
      conn.addRequestProperty("User-Agent", "Mozilla");
      conn.addRequestProperty("Referer", "google.com");

      System.out.println("Request URL ... " + url);

      boolean redirect = false;

      // normally, 3xx is redirect
      final int status = conn.getResponseCode();
      if (status != HttpURLConnection.HTTP_OK) {
        if ((status == HttpURLConnection.HTTP_MOVED_TEMP)
            || (status == HttpURLConnection.HTTP_MOVED_PERM)
            || (status == HttpURLConnection.HTTP_SEE_OTHER)) {
          redirect = true;
        }
      }

      System.out.println("Response Code ... " + status);

      if (redirect) {

        // get redirect url from "location" header field
        final String newUrl = conn.getHeaderField("Location");

        // get the cookie if need, for login
        final String cookies = conn.getHeaderField("Set-Cookie");

        // open the new connnection again
        conn = (HttpURLConnection) new URL(newUrl).openConnection();
        conn.setRequestProperty("Cookie", cookies);
        conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
        conn.addRequestProperty("User-Agent", "Mozilla");
        conn.addRequestProperty("Referer", "google.com");

        System.out.println("Redirect to URL : " + newUrl);

      }

      is = conn.getInputStream();

    } catch (final Exception e) {
      e.printStackTrace();
    }

    return is;
  }

  public static void downloadNotFound() {

    List<String> lines = null;
    try {
      lines = Files.readAllLines(Paths.get(file));
    } catch (final IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    if (lines != null) {
      lines.parallelStream().forEach(line -> {
        final String split[] = line.split(" ");
        if ((split.length >= 3) && split[1].contains(Voc.vcardHasURL)) {

          final String uri = split[0]//
              .replaceAll("<", "").replaceAll(">", "").replaceAll(Voc.permid, "");
          final String o = split[2]//
              .replaceAll("<", "").replaceAll(">", "");

          LOG.info(uri);
          LOG.info(o);

          try {

            final InputStream is = connect(o);
            if ((is != null) && (is.available() > 0)) {
              final String out = uri.concat("/1.html");
              final Path p = Paths.get(out);
              try {
                p.toFile().getParentFile().mkdirs();
              } catch (final Exception e) {

              }
              Files.copy(is, p, StandardCopyOption.REPLACE_EXISTING);
            } else {
              LOG.info("null or not available");
            }
            // is.close();
            /**
             * <code>

             website = new URL(o);
             try (InputStream in = website.openStream()) {

               final String out = uri.concat("/1.html");
               final Path p = Paths.get(out);
               try {
                 p.toFile().getParentFile().mkdirs();
               } catch (final Exception e) {

               }

               Files.copy(in, p, StandardCopyOption.REPLACE_EXISTING);
             }
             </code>
             */
          } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }

        }
      });

    }

  }

  /**
   * prints the IDs for which we do not have a website downloaded
   *
   */
  public static void notFound() {

    // vars
    final DownloadData dd = new DownloadData();
    int found = 0;
    int notfound = 0;

    final TaskOneReader readTaskOne = TaskOneReader.getInstance();
    final TaskTwoReader readTaskTwo = TaskTwoReader.getInstance();

    final DocumentReader reader = new DocumentReader();

    // data not downloaded yet
    final List<String> notFound = new ArrayList<>();

    final Set<String> ids = new HashSet<>();
    ids.addAll(readTaskOne.uriToName.keySet());
    ids.addAll(readTaskOne.uriToURL.keySet());

    // task 1
    LOG.info("Task 1 ");
    for (final String id : ids) {
      final String folder = dd.getFolder(id);
      final Map<String, Document> docs = reader.documentsReader(folder);
      if (docs.isEmpty()) {
        notfound++;

        add(notFound, Voc.permid.concat(folder), Voc.vcardHasURL, readTaskOne.uriToURL.get(id));
        add(notFound, Voc.permid.concat(folder), Voc.vcardOrganizationName,
            readTaskOne.uriToName.get(id));

      } else {
        found++;
      }
    }

    // task 2
    LOG.info("Task 2 ");
    ids.clear();
    ids.addAll(readTaskTwo.uriToName.keySet());
    ids.addAll(readTaskTwo.uriToIsDomiciledIn.keySet());

    for (final String id : ids) {
      final String folder = dd.getFolder(id);
      final Map<String, Document> docs = reader.documentsReader(folder);
      if (docs.isEmpty()) {
        notfound++;

        // all statements for the given id
        final Set<String> statements = readTaskTwo.statementToUri.entrySet().stream()//
            .filter(e -> e.getValue().equals(id)).map(Map.Entry::getKey)//
            .collect(Collectors.toSet());

        for (final String statement : statements) {
          final String url = readTaskTwo.statementToHasURL.get(statement);
          if (url != null) {
            add(notFound, Voc.permid.concat(folder), Voc.vcardHasURL, url);
          }
        }
      } else {
        found++;
      }
    }

    LOG.info(found + "/" + notfound);

    LOG.info("Write file: " + file);
    writeFile(file, notFound);
  }

  protected static void add(final List<String> content, final String s, final String p,
      final String o) {
    content.add(new StringBuffer()//
        .append("<").append(s).append("> ")//
        .append("<").append(p).append("> ")//
        .append("<").append(o).append("> ")//
        .append(" .").toString());
  }

  protected static void writeFile(final String file, final List<String> lines) {
    try {
      Paths.get(file).toFile().getParentFile().mkdirs();
    } catch (final Exception e) {

    }

    try {
      Files.write(Paths.get(file), lines, StandardCharsets.UTF_8);
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }
}
