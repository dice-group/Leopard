package org.aksw.leopard.io.taskstore;

import org.aksw.leopard.util.Serialization;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TaskTwoStore {
  public static final Logger LOG = LogManager.getLogger(TaskOneStore.class);
  static {
    Serialization.setRootFolder("store");
  }
}
