package org.aksw.leopard.io.taskwriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.leopard.io.taskreader.Voc;
import org.aksw.leopard.io.taskstore.TaskOneStore;

/**
 * <code><pre>
  final Store store = new Store();
  store.deserialize();

  final TaskOneWriter tow = new TaskOneWriter(store);
  tow.write("task.ttl");
</pre></code>
 *
 * @author Ren&eacute; Speck <speck@informatik.uni-leipzig.de>
 *
 */
public class TaskOneWriter extends ATaskWriter {

  protected TaskOneStore store = null;

  /**
   *
   * Constructor.
   *
   * @param store
   */
  public TaskOneWriter(final TaskOneStore store) {
    this.store = store;
  }

  /**
   *
   * @param file
   */
  public void write(final String file) {
    final List<String> content = new ArrayList<>();

    final Set<String> keys = new HashSet<>();
    keys.addAll(store.idToDomiciledIn.keySet());
    keys.addAll(store.idToFoundedDate.keySet());
    keys.addAll(store.idToPhone.keySet());

    new StringBuffer();
    for (final String key : keys) {

      final String d = store.idToDomiciledIn.get(key);
      final String f = store.idToFoundedDate.get(key);
      final String p = store.idToPhone.get(key);

      if (d != null) {
        add(content, "<".concat(key).concat("> "), "<".concat(Voc.mdaasIsDomiciledIn).concat("> "),
            "\"".concat(d).concat("\"@en"));
      }

      if (f != null) {
        add(content, "<".concat(key).concat("> "),
            "<".concat(Voc.permidHasLatestOrganizationFoundedDate).concat("> "),
            "\"".concat(f).concat("\""));
      }

      if (p != null) {
        add(content, "<".concat(key).concat("> "),
            "<".concat(Voc.permidHasHeadquartersPhoneNumber).concat("> "),
            "\"".concat(p).concat("\""));

      }

    }
    if (!content.isEmpty()) {
      writeFile(file, content);
    }
  }

}
