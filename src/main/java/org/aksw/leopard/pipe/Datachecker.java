package org.aksw.leopard.pipe;

import java.util.Map.Entry;

import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.io.taskreader.TaskTwoReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Checks if data in task 2 are in task 1 and so in TODO: Not finished.
 *
 * @author Ren&eacute; Speck <speck@informatik.uni-leipzig.de>
 *
 */
public class Datachecker {

  protected static final Logger LOG = LogManager.getLogger(Datachecker.class);

  public static void main(final String[] a) {
    new Datachecker().run();
  }

  public void run() {

    final TaskOneReader taskOneReader = TaskOneReader.getInstance();
    final TaskTwoReader taskTwoReader = TaskTwoReader.getInstance();

    LOG.info("Start...");
    for (final Entry<String, String> entry : taskTwoReader.uriToName.entrySet()) {

      // task 2
      final String uri = entry.getKey();
      final String name = entry.getValue();

      if (taskOneReader.uriToName.values().contains(name)) {

        final String uriName = taskOneReader.uriToName.get(uri);

        final String dom = taskTwoReader.uriToIsDomiciledIn.get(uri);

        if ((uriName != null) && uriName.equals(name)) {
          LOG.info("Same uri:" + uri);
          LOG.info(dom);
        }
      }
    }
  }
}
