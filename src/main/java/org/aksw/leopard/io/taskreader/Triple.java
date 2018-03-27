package org.aksw.leopard.io.taskreader;

public class Triple {

  public String s = "";
  public String p = "";
  public String o = "";

  public Triple(final String s, final String p, final String o) {
    this.s = s;
    this.p = p;
    this.o = o;
  }

  @Override
  public String toString() {
    return "Triple [s=" + s + ", p=" + p + ", o=" + o + "]";
  }

  public static Triple getTriple(final String s, final String p, final String o) {
    return new Triple(s, p, o);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((o == null) ? 0 : o.hashCode());
    result = (prime * result) + ((p == null) ? 0 : p.hashCode());
    result = (prime * result) + ((s == null) ? 0 : s.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Triple other = (Triple) obj;
    if (o == null) {
      if (other.o != null) {
        return false;
      }
    } else if (!o.equals(other.o)) {
      return false;
    }
    if (p == null) {
      if (other.p != null) {
        return false;
      }
    } else if (!p.equals(other.p)) {
      return false;
    }
    if (s == null) {
      if (other.s != null) {
        return false;
      }
    } else if (!s.equals(other.s)) {
      return false;
    }
    return true;
  }

}
