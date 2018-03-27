package org.aksw.leopard.io.taskstore;

import java.io.NotSerializableException;
import java.util.HashMap;
import java.util.Map;

import org.aksw.leopard.util.Serialization;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TaskOneStore {
  public static final Logger LOG = LogManager.getLogger(TaskOneStore.class);
  static {
    Serialization.setRootFolder("store");
  }

  public static String phoneFile = ("phone.bin");
  public static String foundedDateFile = ("foundedDate.bin");
  public static String domiciledInFile = ("domiciledIn.bin");

  public Map<String, String> idToPhone;
  public Map<String, String> idToFoundedDate;
  public Map<String, String> idToDomiciledIn;

  // public static void main(final String[] args) {
  // final Store strore = new Store();
  // LOG.info(strore.idToPhone.size());
  // strore.idToPhone.put(String.valueOf(new Random().nextInt()), "");
  // strore.store();
  // }

  public TaskOneStore() {

    // read data if any
    deserialize();

    // files not exist ye
    if (idToPhone == null) {
      idToPhone = new HashMap<>();
    }
    if (idToFoundedDate == null) {
      idToFoundedDate = new HashMap<>();
    }
    if (idToDomiciledIn == null) {
      idToDomiciledIn = new HashMap<>();
    }
  }

  /**
   * Stores the data
   */
  public void store() {
    try {
      Serialization.serialize(phoneFile, idToPhone, true);
      Serialization.serialize(foundedDateFile, idToFoundedDate, true);
      Serialization.serialize(domiciledInFile, idToDomiciledIn, true);
    } catch (final NotSerializableException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  public void deserialize() {
    idToPhone = Serialization.deserialize(phoneFile, new HashMap<String, String>().getClass());
    idToFoundedDate =
        Serialization.deserialize(foundedDateFile, new HashMap<String, String>().getClass());
    idToDomiciledIn =
        Serialization.deserialize(domiciledInFile, new HashMap<String, String>().getClass());
  }
}
