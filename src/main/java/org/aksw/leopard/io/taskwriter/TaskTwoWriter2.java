package org.aksw.leopard.io.taskwriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.leopard.io.taskreader.TaskFile;
import org.aksw.leopard.io.taskreader.TaskTwoReader;
import org.aksw.leopard.io.taskreader.Voc;
import org.aksw.leopard.io.taskstore.TaskTwoStore;
import org.aksw.leopard.util.DomainCountry;
import org.aksw.leopard.util.Phone;

// TODO:
public class TaskTwoWriter2 extends ATaskWriter {

  protected TaskTwoStore store = null;

  final String fileIn = TaskFile.taskTwo;
  final String fileOut = TaskFile.taskTwoResults;

  Phone phone = new Phone();

  @Deprecated
  public TaskTwoWriter2() {}

  public TaskTwoWriter2(final TaskTwoStore store) {
    this.store = store;
  }

  public void makeAllTrue() {
    final TaskTwoReader reader = TaskTwoReader.getInstance();

    final Set<String> statements = new HashSet<>();

    statements.addAll(reader.statementToHasHeadquartersPhoneNumber.keySet());
    statements.addAll(reader.statementToHasLatestOrganizationFoundedDate.keySet());
    statements.addAll(reader.statementToHasURL.keySet());

    LOG.info("total statements " + statements.size());

    LOG.info("statementToHasHeadquartersPhoneNumber "
        + reader.statementToHasHeadquartersPhoneNumber.size());
    LOG.info("statementToHasLatestOrganizationFoundedDate "
        + reader.statementToHasLatestOrganizationFoundedDate.size());
    LOG.info("statementToHasURL " + reader.statementToHasURL.keySet().size());

    final List<String> content = new ArrayList<>();
    final StringBuffer tmp = new StringBuffer();
    final Map<String, Set<String>> uritos = new HashMap<>();
    for (final String statement : statements) {
      {
        final String uri = reader.statementToUri.get(statement);
        if (uritos.get(uri) == null) {
          uritos.put(uri, new HashSet<>());
        }
        uritos.get(uri).add(statement);
      }

      boolean valid = false;
      final String phoneNumber = reader.statementToHasHeadquartersPhoneNumber.get(statement);
      if (phoneNumber != null) {

        // get given domiciledIn
        final String uri = reader.statementToUri.get(statement);
        final String domiciledIn = reader.uriToIsDomiciledIn.get(uri);
        if (domiciledIn != null) {
          // LOG.info(phoneNumber + " " + domiciledIn);
          // phoneNumber = phoneNumber.replaceAll("^0*", "");
          // LOG.info(phoneNumber + " " + domiciledIn);
          final String domain = DomainCountry.countryToDomain.get(domiciledIn);
          String iso = DomainCountry.domainToISOCode.get(domain);
          if (iso == null) {
            iso = "US";
            LOG.info("Not found: " + domiciledIn.toUpperCase());
          } else {
          }
          valid = (phone.checkPhone(phoneNumber, iso));

          /**
           * <code>
          final List<PhoneNumber> phones = phone.extractPhoneNumber(phoneNumber, iso);
          if (phones.size() > 0) {
            final PhoneNumber number = phones.get(0);
            final String country = DomainCountry.countryCodeToCountry.get(number.getCountryCode());

            if (country.equals(domiciledIn)) {
              valid = true;
            }
          } </code>
           */
        }
        tmp.append("<").append(statement).append("> ")//
            .append("<" + Voc.akswHasTruthValue + ">")//
            .append(" \"").append(valid ? "1.0" : "0.0").append("\"^^<").append(Voc.xmlDouble)
            .append("> .");
        content.add(tmp.toString());
        tmp.delete(0, tmp.length());
      } else {
        // NO PHONE
        tmp.append("<").append(statement).append("> ")//
            .append("<" + Voc.akswHasTruthValue + ">")//
            .append(" \"").append("0.0").append("\"^^<").append(Voc.xmlDouble).append("> .");
        content.add(tmp.toString());
        tmp.delete(0, tmp.length());
      }

    } // for
    uritos.entrySet().stream().filter(p -> p.getValue().size() > 1).forEach(LOG::info);
    writeFile(fileOut, content);
  }

  public static void main(final String[] args) {
    final TaskTwoWriter2 ttw = new TaskTwoWriter2();
    ttw.makeAllTrue();
  }
}
