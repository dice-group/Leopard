package org.aksw.leopard.extractor.impl;

import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.io.taskreader.TaskTwoReader;

public class TaskTwo extends AFactSearcher {
  protected TaskOneReader taskOneReader;
  protected TaskTwoReader taskTwoReader;

  public TaskTwo(final TaskOneReader taskOneReader, final TaskTwoReader taskTwoReader) {
    this.taskOneReader = taskOneReader;
    this.taskTwoReader = taskTwoReader;
  }

  @Override
  public void execute(final String url, final String name) {
    if (!taskOneReader.uriToName.values().contains(name)) {
      return;
    }

    for (final String uri : taskOneReader.getUris(url, name)) {

      // domiciledIn
      if (domiciledIn == null) {
        final String dom = taskTwoReader.uriToIsDomiciledIn.get(uri);
        if (dom != null) {
          domiciledIn = dom;
          domiciledInScore = 1D;
        }
      }

      for (final String statement : taskTwoReader.getStatements(uri)) {
        String data = null;

        if (phone == null) {
          data = taskTwoReader.statementToHasHeadquartersPhoneNumber.get(statement);
          if ((data != null) && !data.trim().isEmpty()) {
            phone = data;
            phoneScore = 0.25D;
          }
        }

        if (foundedDate == null) {
          data = taskTwoReader.statementToHasLatestOrganizationFoundedDate.get(statement);
          if ((data != null) && !data.trim().isEmpty()) {
            foundedDate = data;
            foundedDateScore = 0.4211D;
          }
        }
      }
    } // end for
  }
}
