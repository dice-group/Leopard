package org.aksw.leopard.io.taskreader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

abstract public class ATaskReader {

  protected static final Logger LOG = LogManager.getLogger(TaskOneReader.class);

  protected Set<String> allowedPredicates = new HashSet<>();

  abstract protected void setAllowedPredicates();

  protected Set<Triple> getTriples(final String file) {
    // set allowed predicates
    if (allowedPredicates.isEmpty()) {
      setAllowedPredicates();
      if (allowedPredicates.isEmpty()) {
        throw new UnsupportedOperationException("allowed predicates not set.");
      }
    }

    // read file
    List<String> lines = new ArrayList<String>();
    try {
      lines = Files.readAllLines(Paths.get(file));
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
    final Set<Triple> triples = new HashSet<>();
    for (final String line : lines) {
      final String[] split = line.split("> ");
      if (split.length > 2) {
        final String s = replaceBrackets(split[0]);
        final String p = replaceBrackets(split[1]);
        final String o = replaceBrackets(split[2]);

        if (allowedPredicates.contains(p)) {
          final boolean added = triples.add(Triple.getTriple(s, p, o));
          if (!added) {
            LOG.info("Duplicate: " + Triple.getTriple(s, p, o));
          }
        }
      } else {
        LOG.warn(line);
      }
    }
    LOG.info("Task data lines: " + lines.size());
    LOG.info("Task triple counter: " + triples.size());

    return triples;
  }

  protected String replaceBrackets(final String s) {
    return s.replaceAll("<", "").replaceAll(">", "").trim();
  }

  protected String cleanLiterals(final String o) {
    return o.replaceAll(Pattern.quote("\"@en ."), "").replaceAll(Pattern.quote("\" ."), "")
        .replaceAll("\"", "");
  }

  protected String cleanURL(String o) {
    o = o.trim()//
        .replaceAll("<", "").replaceAll(">", "")//
        .replaceAll("\"", "")//
        .replaceAll(Pattern.quote("^^"), "")//
        .replaceAll(Pattern.quote(Voc.xmlsAnyURI), "")//
        .replaceAll(Pattern.quote(" ."), "")//
        .toLowerCase();

    if (!o.startsWith("http://")) {
      o = "http://".concat(o);
    }
    try {
      return new URL(o).toString();
    } catch (final MalformedURLException e) {
      LOG.error(e.getLocalizedMessage(), e);
      return "";
    }
  }
}
