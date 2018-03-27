package org.aksw.leopard;

import java.util.HashSet;

import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.io.taskreader.TaskTwoReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Statistic {

  protected static final Logger LOG = LogManager.getLogger(Statistic.class);

  public static void main(final String[] a) {

    final TaskOneReader taskOneReader = TaskOneReader.getInstance();
    final TaskTwoReader taskTwoReader = TaskTwoReader.getInstance();

    {
      LOG.info("Task1");
      LOG.info("uris : " + taskOneReader.uriToName.keySet().size());
      final HashSet<String> names = new HashSet<>();

      names.addAll(taskOneReader.uriToName.values());
      LOG.info("names : " + names.size());

      final HashSet<String> urls = new HashSet<>();
      urls.addAll(taskOneReader.uriToURL.values());

      LOG.info("urls : " + urls.size());

    }
    {

      LOG.info("Task2");

      LOG.info("uris : " + taskTwoReader.uriToName.keySet().size());
      final HashSet<String> names = new HashSet<>();
      names.addAll(taskTwoReader.uriToName.values());
      LOG.info("names : " + names.size());

      final HashSet<String> statements = new HashSet<>();
      statements.addAll(taskTwoReader.statementToHasHeadquartersPhoneNumber.keySet());
      statements.addAll(taskTwoReader.statementToHasLatestOrganizationFoundedDate.keySet());
      statements.addAll(taskTwoReader.statementToHasURL.keySet());
      statements.addAll(taskTwoReader.statementToUri.keySet());

      LOG.info("statements :" + statements.size());
    }
  }
}
