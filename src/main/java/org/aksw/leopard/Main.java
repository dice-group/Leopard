package org.aksw.leopard;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.aksw.leopard.io.taskstore.TaskOneStore;
import org.aksw.leopard.io.taskwriter.TaskOneWriter;
import org.aksw.leopard.pipe.Pipeline;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Leopard <br>
 * speck@informatik.uni-leipzig.de
 *
 * @author Ren&eacute; Speck <speck@informatik.uni-leipzig.de>
 *
 */
public class Main {
  public static final Logger LOG = LogManager.getLogger(Main.class);
  public static final String defaultDomiciledIn = "Germany";

  public static void main(final String[] args) throws IOException {

    final List<String> lines = Files.readAllLines(Paths.get("bla/test.txt"));

    LOG.info(lines);

    final TaskOneStore store = new TaskOneStore();
    for (String l : lines) {
      l = "http://permid.org/".concat(l);
      store.idToDomiciledIn.put(l, defaultDomiciledIn);
      store.idToFoundedDate.put(l, "1955");
      store.idToPhone.put(l, "00018002446227");

    }
    final TaskOneWriter t = new TaskOneWriter(store);
    t.write("bla/test.txt.nt");
  }

  public static void main2(final String[] args) {

    final Pipeline pipe = new Pipeline();
    try {
      pipe.run();
    } catch (final MalformedURLException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }
}
