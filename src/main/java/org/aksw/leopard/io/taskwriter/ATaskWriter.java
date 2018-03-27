package org.aksw.leopard.io.taskwriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public abstract class ATaskWriter {

  public static final Logger LOG = LogManager.getLogger(ATaskWriter.class);

  protected void writeFile(final String file, final List<String> lines) {
    try {
      Paths.get(file).toFile().getParentFile().mkdirs();
      Files.write(Paths.get(file), lines, StandardCharsets.UTF_8);
    } catch (final IOException e) {
      LOG.error(e.getLocalizedMessage(), e);
    }
  }

  protected void add(final List<String> content, final String s, final String p, final String o) {
    content.add(new StringBuffer()//
        .append(s)//
        .append(p)//
        .append(o)//
        .append(" .").toString());
  }
}
