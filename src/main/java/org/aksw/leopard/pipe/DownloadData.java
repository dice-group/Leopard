package org.aksw.leopard.pipe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.aksw.leopard.io.LinkFinder;
import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.io.taskreader.TaskTwoReader;
import org.aksw.leopard.io.taskreader.Voc;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * It gets all urls to download for task data. Then download websites.
 *
 * @author Ren&eacute; Speck <speck@informatik.uni-leipzig.de>
 *
 */
public class DownloadData {

  public String getFolder(final String id) {
    return "tmp/".concat(id.replaceAll(Voc.permid, ""));
  }

  protected static final Logger LOG = LogManager.getLogger(DownloadData.class);

  public static void main(final String[] a) {
    new DownloadData().run();
  }

  public void run() {

    final TaskOneReader readTaskOne = TaskOneReader.getInstance();
    final TaskTwoReader readTaskTwo = TaskTwoReader.getInstance();

    final Set<String> done = ConcurrentHashMap.newKeySet();

    final int threads = 64;

    final ExecutorService executorService = Executors.newFixedThreadPool(threads);
    final CompletionService<Boolean> completionService =
        new ExecutorCompletionService<>(executorService);

    final List<Future<Boolean>> list = new ArrayList<>();
    int i = 0;
    for (final Entry<String, String> entry : readTaskTwo.statementToHasURL.entrySet()) {
      final Future<Boolean> futureResult = completionService.submit(() -> {
        final String id = readTaskTwo.statementToUri.get(entry.getKey());
        final String url = entry.getValue();
        return addLinks(done, id, url);
      });
      i++;
      list.add(futureResult);
    }

    for (final Entry<String, String> entry : readTaskOne.uriToURL.entrySet()) {
      final Future<Boolean> futureResult = completionService.submit(() -> {
        final String id = entry.getKey();
        final String url = entry.getValue();
        return addLinks(done, id, url);
      });
      i++;
      list.add(futureResult);
    }
    executorService.shutdown();

    for (int iii = 0; iii < i; ++iii) {
      Future<Boolean> future = null;
      try {
        future = completionService.poll(3 * 60, TimeUnit.SECONDS);
      } catch (final InterruptedException e) {
        LOG.error(e.getLocalizedMessage(), e);
      }
      if (future == null) {
        list.get(iii).cancel(true);
        LOG.warn("Timeout ...");
      }
    }

    LOG.info("Done");
  }

  protected boolean addLinks(final Set<String> done, final String id, final String url) {
    if (!done.contains(url)) {

      final String folder = getFolder(id);
      int c = 0;
      final int max = 20;
      final LinkFinder links = new LinkFinder();
      List<String> list = null;
      list = links.getPageLinks(url);

      for (final String link : list) {
        if (c < max) {
          c++;
          final String file = folder + "/" + c + ".html";
          final String content;
          content = links.reader.documentReader(link).html();
          try {
            Paths.get(file).toFile().getParentFile().mkdirs();
            Files.write(Paths.get(file), content.getBytes());
          } catch (final IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return false;
          }
        }
      }
      return done.add(url);
    }
    return false;
  }
}
