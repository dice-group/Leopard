package org.aksw.leopard.io.taskreader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskTwoReader extends ATaskReader {

  private static TaskTwoReader taskTwoReader = null;

  // given for all resources
  public Map<String, String> uriToName = new HashMap<>();
  public Map<String, String> uriToIsDomiciledIn = new HashMap<>();

  //
  public Map<String, String> statementToUri = new HashMap<>();

  // statements to be checked
  public Map<String, String> statementToHasLatestOrganizationFoundedDate = new HashMap<>();
  public Map<String, String> statementToHasHeadquartersPhoneNumber = new HashMap<>();
  public Map<String, String> statementToHasURL = new HashMap<>();

  /**
   * All statements connected with the uri.
   *
   * @param uri
   * @return statements
   */
  public Set<String> getStatements(final String uri) {
    final Set<String> data = statementToUri.entrySet().stream()//
        .filter(e -> e.getValue().equals(uri))//
        .map(e -> e.getKey())//
        .collect(Collectors.toSet());
    return data == null ? new HashSet<>() : data;
  }

  /**
   * Singleton.
   *
   * @return
   */
  public static TaskTwoReader getInstance() {
    if (taskTwoReader == null) {
      taskTwoReader = new TaskTwoReader(TaskFile.taskTwo);
    }
    return taskTwoReader;
  }

  /**
   *
   * Constructor.
   *
   * @param file
   */
  private TaskTwoReader(final String file) {
    final Set<Triple> triples = getTriples(file);
    setAllURI(triples);
    setAllStatements(triples);
  }

  public static void main(final String[] args) {
    final TaskTwoReader readTaskData = TaskTwoReader.getInstance();
    LOG.info("---uriToName ");
    readTaskData.uriToName.entrySet().stream().limit(10).forEach(LOG::info);

    LOG.info("---uriToIsDomiciledIn ");
    readTaskData.uriToIsDomiciledIn.entrySet().stream().limit(10).forEach(LOG::info);

    LOG.info("---statementToUri");
    readTaskData.statementToUri.entrySet().stream().limit(10).forEach(LOG::info);

    LOG.info("---statementToHasLatestOrganizationFoundedDate");
    readTaskData.statementToHasLatestOrganizationFoundedDate.entrySet().stream().limit(10)
        .forEach(LOG::info);

    LOG.info("---statementToHasHeadquartersPhoneNumber");
    readTaskData.statementToHasHeadquartersPhoneNumber.entrySet().stream().limit(10)
        .forEach(LOG::info);
    LOG.info("---statementToHasURL");
    readTaskData.statementToHasURL.entrySet().stream().limit(10).forEach(LOG::info);
  }

  public void setAllURI(final Set<Triple> triples) {
    final Iterator<Triple> iter = triples.iterator();
    while (iter.hasNext()) {
      final Triple triple = iter.next();
      final String s = triple.s;
      final String p = triple.p;
      final String o = triple.o;

      // add given resources
      if (s.startsWith(Voc.permid)) {
        if (p.startsWith(Voc.vcardOrganizationName)) {
          uriToName.put(s, cleanLiterals(o));
          iter.remove();
        } else if (p.startsWith(Voc.mdaasIsDomiciledIn)) {
          uriToIsDomiciledIn.put(s, cleanLiterals(o));
          iter.remove();
        } else {
          LOG.warn("Nothing found!");
        }
      }

      // add resource statements
      if (s.startsWith(Voc.aksw)) {
        if (p.equals(Voc.rdfSubject)) {
          statementToUri.put(s, o);
          iter.remove();
        }
      }
    }
  }

  public void setAllStatements(final Set<Triple> triples) {
    Iterator<Triple> iter = triples.iterator();
    while (iter.hasNext()) {
      final Triple triple = iter.next();
      final String s = triple.s;
      final String p = triple.p;
      final String o = triple.o;

      // add resource statements
      if (s.startsWith(Voc.aksw) && p.startsWith(Voc.rdfPredicate)) {
        if (o.equals(Voc.vcardHasURL)) {
          statementToHasURL.put(s, null);
          iter.remove();
        } else if (o.equals(Voc.permidHasHeadquartersPhoneNumber)) {
          statementToHasHeadquartersPhoneNumber.put(s, null);
          iter.remove();
        } else if (o.equals(Voc.permidHasLatestOrganizationFoundedDate)) {
          statementToHasLatestOrganizationFoundedDate.put(s, null);
          iter.remove();
        }
      }
    }
    iter = triples.iterator();
    while (iter.hasNext()) {
      final Triple triple = iter.next();
      final String s = triple.s;
      final String p = triple.p;
      final String o = triple.o;
      if (s.startsWith(Voc.aksw) && p.startsWith(Voc.rdfObject)) {
        if (statementToHasURL.containsKey(s)) {
          statementToHasURL.put(s, cleanURL(o));
          iter.remove();
        } else if (statementToHasHeadquartersPhoneNumber.containsKey(s)) {
          statementToHasHeadquartersPhoneNumber.put(s, cleanLiterals(o));
          iter.remove();
        } else if (statementToHasLatestOrganizationFoundedDate.containsKey(s)) {
          statementToHasLatestOrganizationFoundedDate.put(s, cleanLiterals(o));
          iter.remove();
        } else {
          LOG.info("Not found: " + triple);
        }
      }
    }
    LOG.info("triples size should be 0 now: " + triples.size());
    triples.forEach(LOG::info);
  }

  @Override
  protected void setAllowedPredicates() {
    // allowedPredicates.add(Voc.rdfType);
    allowedPredicates.add(Voc.rdfSubject);
    allowedPredicates.add(Voc.rdfPredicate);
    allowedPredicates.add(Voc.rdfObject);

    allowedPredicates.add(Voc.vcardOrganizationName);
    allowedPredicates.add(Voc.mdaasIsDomiciledIn);
  }
}
