package org.aksw.leopard.dbpedia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Surfaceforms {
  public static final Logger LOG = LogManager.getLogger(Surfaceforms.class);

  protected int maxSurfaceformLength;
  protected int minSurfaceformLength;

  protected Map<String, Set<String>> uriToSurfaceforms = null;
  protected Map<String, Set<String>> surfaceformToUris = null;

  public static void main(final String[] args) throws MalformedURLException {

    final String file = "data/dbpedia/en_surface_forms.tsv";
    final Surfaceforms s = new Surfaceforms(file);
    // s.uriToSurfaceforms.entrySet().stream().limit(100).forEach(LOG::info);
    // s.surfaceformToUris.entrySet().stream().limit(100).forEach(LOG::info);

    LOG.info(s.getURIs("Lemonade Stand"));
    for (final String uri : s.getURIs("Lemonade Stand")) {
      s.getSurfaceforms(uri).forEach(LOG::info);
    }
  }

  /**
   *
   * Constructor.
   *
   * @param file
   */
  public Surfaceforms(final String file) {
    this(file, 2, 100);
  }

  public Surfaceforms(final String file, final int minSurfaceformLength,
      final int maxSurfaceformLength) {
    this.maxSurfaceformLength = maxSurfaceformLength;
    this.minSurfaceformLength = minSurfaceformLength;

    // init uriToSurfaceforms
    try {
      readSurfaceFormsFromFile(Paths.get(file));
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
    LOG.info("Finished intializing surface forms! Found uris: " + uriToSurfaceforms.size());

    // init surfaceformToUris
    surfaceformToUris = new HashMap<>();
    for (final Entry<String, Set<String>> entry : uriToSurfaceforms.entrySet()) {
      for (final String sf : entry.getValue()) {
        if (surfaceformToUris.get(sf) == null) {
          surfaceformToUris.put(sf, new HashSet<>());
        }
        surfaceformToUris.get(sf).add(entry.getKey());
      }
    }
  }

  /**
   *
   * @param uri DBpedia resource
   * @return sfs
   */
  public Set<String> getSurfaceforms(final String uri) {
    return uriToSurfaceforms.get(uri);
  }

  /**
   *
   * @param surfaceform
   * @return DBpedia resources
   */
  public Set<String> getURIs(final String surfaceform) {
    return surfaceformToUris.get(surfaceform);
  }

  protected void readSurfaceFormsFromFile(final Path file) throws IOException {
    final BufferedReader br = new BufferedReader(new FileReader(file.toFile()));
    uriToSurfaceforms = new HashMap<>();
    br.lines().forEach(line -> {
      if (!line.startsWith("#")) {
        final String[] lineParts = line.split("\t");
        final String[] surfaceFormsPart = Arrays.copyOfRange(lineParts, 1, lineParts.length);
        final Set<String> filteredSurfaceForms = new HashSet<>();
        for (final String surfaceForm : surfaceFormsPart) {
          if ((surfaceForm.length() >= minSurfaceformLength)
              && (surfaceForm.length() <= maxSurfaceformLength)) {
            filteredSurfaceForms.add(surfaceForm);
          }
        }
        if (!filteredSurfaceForms.isEmpty()) {
          uriToSurfaceforms.put(lineParts[0], filteredSurfaceForms);
          uriToSurfaceforms.put(lineParts[0].replace("http://en.", "http://"),
              filteredSurfaceForms);
        }
      }
    });
    br.close();
  }
}
