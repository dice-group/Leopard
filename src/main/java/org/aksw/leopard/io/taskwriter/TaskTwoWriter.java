package org.aksw.leopard.io.taskwriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.aksw.leopard.io.DocumentReader;
import org.aksw.leopard.io.taskreader.TaskFile;
import org.aksw.leopard.io.taskreader.TaskTwoReader;
import org.aksw.leopard.io.taskreader.Voc;
import org.aksw.leopard.io.taskstore.TaskTwoStore;
import org.aksw.leopard.util.DomainCountry;
import org.aksw.leopard.util.Phone;
import org.jsoup.nodes.Document;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

// TODO:
public class TaskTwoWriter extends ATaskWriter {

  protected TaskTwoStore store = null;

  final String fileIn = TaskFile.taskTwo;
  final String fileOut = TaskFile.taskTwoResults;

  Phone phone = new Phone();

  public TaskTwoWriter() {}

  public TaskTwoWriter(final TaskTwoStore store) {
    this.store = store;
  }

  public void makeAllTrue() {
    final TaskTwoReader reader = TaskTwoReader.getInstance();

    final Set<String> statements = new HashSet<>();
    statements.addAll(reader.statementToHasHeadquartersPhoneNumber.keySet());
    statements.addAll(reader.statementToHasLatestOrganizationFoundedDate.keySet());
    statements.addAll(reader.statementToHasURL.keySet());

    final List<String> content = new ArrayList<>();
    final StringBuffer tmp = new StringBuffer();

    for (final String statement : statements) {

      final String uri = reader.statementToUri.get(statement);
      if (uri != null) {
        final String domiciledIn = reader.uriToIsDomiciledIn.get(uri);
        final String name = reader.uriToName.get(uri);

        final String fodler = "tmp/" + uri.replaceAll("http://permid.org/", "");

        final Map<String, Document> docsTmp = docReader.documentsReader(fodler);

        String web = null;
        String phoneNumber = null;
        String date = null;

        final Set<String> stats;
        stats = reader.statementToUri.entrySet().stream()//
            .filter(e -> e.getValue().equals(uri))//
            .map(e -> e.getKey())//
            .collect(Collectors.toSet());

        String state = "";
        if (reader.statementToHasHeadquartersPhoneNumber.get(statement) != null) {
          state = "p";
        }
        if (reader.statementToHasLatestOrganizationFoundedDate.get(statement) != null) {
          state = "y";
        }
        if (reader.statementToHasURL.get(statement) != null) {
          state = "w";
        }

        for (final String s : stats) {

          String tmp2 = null;
          tmp2 = reader.statementToHasHeadquartersPhoneNumber.get(s);
          if (tmp2 != null) {
            phoneNumber = tmp2;
          }

          tmp2 = reader.statementToHasURL.get(s);
          if (tmp2 != null) {
            web = tmp2;
          }

          tmp2 = reader.statementToHasLatestOrganizationFoundedDate.get(s);
          if (tmp2 != null) {
            date = tmp2;
          }
        }

        LOG.info("===");
        LOG.info(uri);
        LOG.info(domiciledIn);
        LOG.info(web);
        LOG.info(phoneNumber);

        boolean valid = false;
        if (state.equals("p")) {
          valid = phone(date, name, domiciledIn, phoneNumber, web, docsTmp);
        } else if (state.equals("w")) {
          valid = website(date, name, domiciledIn, phoneNumber, web, docsTmp);
        } else if (state.equals("y")) {
          valid = year(date, name, domiciledIn, phoneNumber, web, docsTmp);
        }

        tmp.append("<").append(statement).append("> ")//
            .append("<" + Voc.akswHasTruthValue + ">")//
            .append(" \"").append(valid ? "1.0" : "0.0").append("\"^^<").append(Voc.xmlDouble)
            .append("> .");
        content.add(tmp.toString());
        tmp.delete(0, tmp.length());

      } // for
    }

    writeFile(fileOut, content);

  }

  protected DocumentReader docReader = new DocumentReader();

  public boolean website(final String date, final String name, final String domiciledIn,
      final String phoneNumber, final String web, final Map<String, Document> docs) {

    for (final Document d : docs.values()) {
      if (d.text().contains(name)) {
        return true;
      }
    }

    if ((web != null) && (domiciledIn != null)) {

      final String domain = DomainCountry.countryToDomain.get(domiciledIn.trim());

      if ((domain == null) || domain.equals(".us")) {
        if (web.endsWith(".com") || web.endsWith(".org") || web.endsWith(".net")) {
          return true;
        }
      } else if (web.endsWith(domain)) {
        return true;
      } else {
        return false;
      }
    }

    return false;
  }

  public boolean year(final String date, final String name, final String domiciledIn,
      final String phoneNumber, final String web, final Map<String, Document> docs) {
    for (final Document d : docs.values()) {
      if (d.text().contains(date)) {
        return true;
      }
    }
    return false;
  }

  public static void main(final String[] args) {
    final TaskTwoWriter ttw = new TaskTwoWriter();
    ttw.makeAllTrue();
  }

  public boolean phone(final String date, final String name, final String domiciledIn,
      final String phoneNumber, final String web, final Map<String, Document> docs) {
    boolean valid = false;
    if ((phoneNumber != null) && (domiciledIn != null)) {

      final String p = phone(phoneNumber);
      if (p.equals(domiciledIn)) {
        valid = true;
      }
    }
    return valid;
  }

  public String phone(final String number) {

    final Set<String> c = new HashSet<>();
    PhoneNumberUtil.getInstance().getSupportedRegions().forEach(code -> {
      final List<PhoneNumber> list = phone.extractPhoneNumber(number, code);
      if (!list.isEmpty()) {
        c.add(DomainCountry.countryCodeToCountry.get(list.get(0).getCountryCode()));
      }
    });

    if (!c.isEmpty()) {
      if (c.size() == 1) {
        return c.iterator().next();
      } else {
        // TODO
      }
    }
    return "";
  }
}
