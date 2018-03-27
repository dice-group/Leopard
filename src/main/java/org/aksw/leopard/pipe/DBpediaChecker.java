package org.aksw.leopard.pipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.aksw.leopard.dbpedia.Surfaceforms;
import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.io.taskreader.TaskTwoReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DBpediaChecker {
  protected static final Logger LOG = LogManager.getLogger(Datachecker.class);

  protected Surfaceforms sf;

  public static void main(final String[] a) {
    new DBpediaChecker().run();
  }

  public DBpediaChecker() {

  }

  public void run() {

    final TaskOneReader readTaskOne = TaskOneReader.getInstance();
    final TaskTwoReader readTaskTwo = TaskTwoReader.getInstance();

    LOG.info("Start...");

    // find min and max sf length
    final List<String> list = new ArrayList<>(readTaskTwo.uriToName.values());
    list.addAll(readTaskOne.uriToName.values());
    final List<Integer> l =
        new ArrayList<>(list.stream().map(v -> v.length()).collect(Collectors.toSet()));
    Collections.sort(l);
    final int max = l.get(l.size() - 1);
    final int min = l.get(0);

    // load sfs
    sf = new Surfaceforms("data/dbpedia/en_surface_forms.tsv", min, max);

    LOG.info("==Task 1=================================");
    // check task1
    for (final Entry<String, String> entry : readTaskOne.uriToName.entrySet()) {
      check(entry);
    }

    LOG.info("==Task 2=================================");
    // check taks2
    for (final Entry<String, String> entry : readTaskTwo.uriToName.entrySet()) {
      check(entry);
    }
    LOG.info("========================================");

  }

  protected void check(final Entry<String, String> entry) {
    final Set<String> dbpediaUris = sf.getURIs(entry.getValue().trim());
    if ((dbpediaUris != null) && (dbpediaUris.size() > 0)) {
      LOG.info(entry.getValue() + " / " + dbpediaUris);
    }
  }
}
