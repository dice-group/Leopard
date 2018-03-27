package org.aksw.leopard.extractor.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.leopard.io.taskreader.TaskOneReader;
import org.aksw.leopard.io.taskreader.TaskTwoReader;
import org.aksw.leopard.io.taskreader.Voc;
import org.aksw.leopard.util.DomainCountry;
import org.aksw.leopard.util.Serialization;

public class FoxSearcher extends AFactSearcher {

  Map<String, String> idToPlace;
  final TaskOneReader taskOneReader = TaskOneReader.getInstance();
  final TaskTwoReader taskTwoReader = TaskTwoReader.getInstance();

  public int counter = 0;

  /**
   *
   * Constructor.
   *
   * @param taskOneReader
   * @param taskTwoReader
   */
  public FoxSearcher() {

    Serialization.setRootFolder("tmp_dbpedia");
    idToPlace = Serialization.deserialize("fox.map", new HashMap<>().getClass());
  }

  /**
   *
   */
  @Override
  public void execute(final String url, final String name) {
    final List<String> ids = taskOneReader.getUris(url, name);

    final Set<String> places = new HashSet<>();
    for (final String id : ids) {
      final String label = idToPlace.get(id.replaceAll(Voc.permid, ""));
      if (label != null) {
        places.add(label);
      }
    }

    if (!places.isEmpty()) {
      for (final String place : places) {
        if (DomainCountry.countryToDomain.keySet().contains(place)) {
          domiciledIn = place;
          domiciledInScore = 0.6831D;
          counter++;
          break;
        }
      }
    }
  }
}
