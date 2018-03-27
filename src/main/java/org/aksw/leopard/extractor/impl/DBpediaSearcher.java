package org.aksw.leopard.extractor.impl;

import java.util.HashMap;
import java.util.Map;

import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.io.taskreader.TaskTwoReader;
import org.aksw.leopard.util.Serialization;

public class DBpediaSearcher extends AFactSearcher {
  Map<String, String> nameToHomepage, nameToLocation, nameToYear;
  protected TaskOneReader taskOneReader;
  protected TaskTwoReader taskTwoReader;

  @SuppressWarnings("unchecked")
  public DBpediaSearcher(final TaskOneReader taskOneReader, final TaskTwoReader taskTwoReader) {
    this.taskOneReader = taskOneReader;
    // this.taskTwoReader = taskTwoReader;

    Serialization.setRootFolder("tmp_dbpedia");
    String file;

    file = "nameToHomepage.map";
    nameToHomepage = Serialization.deserialize(file, new HashMap<>().getClass());

    file = "nameToLocation.map";
    nameToLocation = Serialization.deserialize(file, new HashMap<>().getClass());

    file = "nameToYear.map";
    nameToYear = Serialization.deserialize(file, new HashMap<>().getClass());

    if (nameToYear == null) {
      nameToYear = new HashMap<>();
    }

    if (nameToLocation == null) {
      nameToLocation = new HashMap<>();
    }

    if (nameToHomepage == null) {
      nameToHomepage = new HashMap<>();
    }
  }

  @Override
  public void execute(final String url, final String name) {

    if (name != null) {
      final String loc = nameToLocation.get(name.trim());
      if (loc != null) {
        domiciledIn = loc;
        domiciledInScore = 0.8868;
      }

      final String year = nameToYear.get(name.trim());
      if (year != null) {
        // foundedDate = year;
        // foundedDateScore = 0.1667;
      }
    }
  }
}
