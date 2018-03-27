package org.aksw.leopard.pipe;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.aksw.leopard.extractor.Extractor;
import org.aksw.leopard.extractor.IExtractor;
import org.aksw.leopard.extractor.impl.DBpediaSearcher;
import org.aksw.leopard.extractor.impl.DomainFactSearcher;
import org.aksw.leopard.extractor.impl.FoxSearcher;
import org.aksw.leopard.extractor.impl.SemanticTagFactSearcher;
import org.aksw.leopard.extractor.impl.WebsiteFactSearcher;
import org.aksw.leopard.io.ExtractedData;
import org.aksw.leopard.io.taskreader.TaskFile;
import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.io.taskreader.TaskTwoReader;
import org.aksw.leopard.io.taskstore.TaskOneStore;
import org.aksw.leopard.io.taskwriter.TaskOneWriter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Pipeline {

  public static final Logger LOG = LogManager.getLogger(Pipeline.class);

  final String fileIn = TaskFile.taskOne;
  final String fileOut = TaskFile.taskOneResults;

  final List<IExtractor> list = new ArrayList<>();

  final TaskOneReader taskOneReader = TaskOneReader.getInstance();
  final TaskTwoReader t2 = TaskTwoReader.getInstance();

  /**
   *
   * Constructor.
   *
   */
  public Pipeline() {

    IExtractor ifs;
    // ifs = new SemanticTagFactSearcherOld(taskOneReader);
    // list.add(ifs);

    ifs = new SemanticTagFactSearcher(taskOneReader);
    list.add(ifs);

    // ifs = new TaskTwo(taskOneReader, t2);
    // list.add(ifs);

    ifs = new DomainFactSearcher();
    list.add(ifs);

    ifs = new DBpediaSearcher(taskOneReader, t2);
    // list.add(ifs);

    ifs = new WebsiteFactSearcher(taskOneReader);
    list.add(ifs);

    ifs = new FoxSearcher();
    list.add(ifs);
  }

  public void run() throws MalformedURLException {
    LOG.info("run ...");

    /* Downloads websites */
    // new DownloadData().run();

    // check data

    // new Datachecker().run();

    // run extraction
    _run();

    LOG.info("done");
  }

  /**
   *
   * @throws MalformedURLException
   */
  public void _run() throws MalformedURLException {

    final TaskOneStore store = new TaskOneStore();

    for (final String uri : taskOneReader.uriToName.keySet()) {

      final String url = taskOneReader.uriToURL.get(uri);
      final String name = taskOneReader.uriToName.get(uri);

      final Extractor finder = new Extractor(list, url, name);

      // results (dom,found,phone) for each approach
      final Set<ExtractedData> results;
      results = finder.results;
      setStoreWithBestScores(uri, store, results);
    }
    store.store();

    final TaskOneWriter taskOneWriter = new TaskOneWriter(store);
    taskOneWriter.write(fileOut);
  }

  public void setStoreWithBestScores(final String uri, final TaskOneStore store,
      final Set<ExtractedData> results) {

    final ExtractedData best = ExtractedData.get("best", "", "", "", 0D, 0D, 0D);

    for (final ExtractedData extractedData : results) {

      if ((extractedData.domiciledIn != null)
          && (extractedData.domiciledInScore > best.domiciledInScore)) {
        best.domiciledIn = extractedData.domiciledIn;
        best.domiciledInScore = extractedData.domiciledInScore;
      }

      if ((extractedData.foundedDate != null)
          && (extractedData.foundedDateScore > best.foundedDateScore)) {
        best.foundedDate = extractedData.foundedDate;
        best.foundedDateScore = extractedData.foundedDateScore;
      }
      if ((extractedData.phone != null) && (extractedData.phoneScore > best.phoneScore)) {
        best.phone = extractedData.phone;
        best.phoneScore = extractedData.phoneScore;
      }
    }

    if (!best.domiciledIn.isEmpty()) {
      store.idToDomiciledIn.put(uri, best.domiciledIn);
    }
    if (!best.foundedDate.isEmpty()) {
      store.idToFoundedDate.put(uri, best.foundedDate);
    }
    if (!best.phone.isEmpty()) {
      store.idToPhone.put(uri, best.phone);
    }
  }
}
