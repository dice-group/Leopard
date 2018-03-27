package org.aksw.leopard.io;

public class ExtractedData {

  public String approach = null;

  public String domiciledIn = null;
  public String foundedDate = null;
  public String phone = null;

  public double domiciledInScore = 0D;
  public double foundedDateScore = 0D;
  public double phoneScore = 0D;

  public ExtractedData(final String approach, final String domiciledIn, final String foundedDate,
      final String phone, final double domiciledInScore, final double foundedDateScore,
      final double phoneScore) {
    super();
    this.approach = approach;
    this.domiciledIn = domiciledIn;
    this.foundedDate = foundedDate;
    this.phone = phone;
    this.domiciledInScore = domiciledInScore;
    this.foundedDateScore = foundedDateScore;
    this.phoneScore = phoneScore;
  }

  public static ExtractedData get(final String approach, final String domiciledIn,
      final String foundedDate, final String phone, final Double domiciledInScore,
      final Double foundedDateScore, final Double phoneScore) {

    return new ExtractedData(approach, domiciledIn, foundedDate, phone, domiciledInScore,
        foundedDateScore, phoneScore);
  }

  @Override
  public String toString() {
    return "ExtractedData [approach=" + approach + ", domiciledIn=" + domiciledIn + ", foundedDate="
        + foundedDate + ", phone=" + phone + ", domiciledInScore=" + domiciledInScore
        + ", foundedDateScore=" + foundedDateScore + ", phoneScore=" + phoneScore + "]";
  }

}
