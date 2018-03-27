package org.aksw.leopard.io.taskreader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.aksw.leopard.io.DocumentReader;
import org.aksw.leopard.pipe.DownloadData;
import org.jsoup.nodes.Document;

public class TaskOneReader extends ATaskReader {

  protected static TaskOneReader taskOneReader = null;

  public Map<String, String> uriToName = new HashMap<>();
  public Map<String, String> uriToURL = new HashMap<>();

  protected final DownloadData downloadData = new DownloadData();
  protected DocumentReader docReader = new DocumentReader();

  /**
   * Gets a singleton of the class.
   *
   * @return TaskOneReader
   */
  public static TaskOneReader getInstance() {
    if (taskOneReader == null) {
      taskOneReader = new TaskOneReader(TaskFile.taskOne);
    }
    return taskOneReader;
  }

  private TaskOneReader(final String file) {

    for (final Triple triple : getTriples(file)) {
      final String s = triple.s;
      final String p = triple.p;
      final String o = triple.o;

      if (p.equals(Voc.vcardOrganizationName)) {
        addData(uriToName, s, cleanLiterals(o));
      } else if (p.endsWith(Voc.vcardHasURL)) {
        addData(uriToURL, s, cleanURL(o));
      } else {
        LOG.warn("nothing found: " + triple);
      }
    }
  }

  public Set<String> getURIs() {
    final Set<String> uris = new HashSet<>();
    uris.addAll(uriToName.keySet());
    uris.addAll(uriToURL.keySet());
    return uris;
  }

  @Override
  protected void setAllowedPredicates() {
    allowedPredicates.add(Voc.vcardHasURL);
    allowedPredicates.add(Voc.vcardOrganizationName);
  }

  private boolean addData(final Map<String, String> map, final String uri, final String v) {
    if (map.get(uri) == null) {
      map.put(uri, v);
      return true;
    } else {
      LOG.warn("Already in the map: " + uri);
      return false;
    }
  }

  /**
   * All ids with the given url in task data.
   *
   * @param url
   * @param name
   * @return ids with the url
   */
  public List<String> getUris(final String url, final String name) {

    final List<String> uris = new ArrayList<>();

    for (final Entry<String, String> entry : uriToURL.entrySet()) {

      final String uri = entry.getKey();
      final String storedUrl = entry.getValue();
      final String storedName = uriToName.get(uri);

      if ((storedName != null) && (storedUrl != null) //
          && storedUrl.equals(url) && storedName.equals(name)//
      ) {
        uris.add(uri);
      }
    }
    return uris;
  }

  /**
   * All Documents with the url.
   *
   * @param url
   * @param name
   * @return
   */
  public Map<String, Document> getDocs(final String url, final String name) {

    Map<String, Document> docs = new HashMap<>();

    for (final String id : getUris(url, name)) {
      final String folder = downloadData.getFolder(id);
      final Map<String, Document> docsTmp = docReader.documentsReader(folder);
      if (docsTmp.size() > docs.size()) {
        docs = docsTmp;
      }
    }
    return docs;
  }
}
