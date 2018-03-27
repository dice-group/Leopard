package org.aksw.leopard.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class Phone {

  public static final Logger LOG = LogManager.getLogger(Phone.class);
  final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

  /*
   * @param phone eg. "044 668 18 00"
   *
   * @param defaultRegion e.g. "CH"
   *
   * @return
   */
  public boolean checkPhone(final String phone, final String defaultRegion) {

    PhoneNumber pn = null;
    try {
      pn = phoneUtil.parse(phone, defaultRegion);
    } catch (final NumberParseException e) {
      LOG.error("NumberParseException was thrown: " + e.toString());
    }
    return (phoneUtil.isValidNumber(pn));
  }

  /**
   * Extracts a phone number from the given input string.
   *
   * @param input
   * @param defaultRegion
   * @return list of phone numbers
   */
  public List<PhoneNumber> extractPhoneNumber(final String input, final String defaultRegion) {
    final List<PhoneNumber> phones = new ArrayList<>();
    final Iterator<PhoneNumberMatch> existsPhone =
        phoneUtil.findNumbers(input, defaultRegion).iterator();

    while (existsPhone.hasNext()) {
      phones.add((existsPhone.next().number()));
    }
    return phones;
  }

  /**
   *
   * @param numberToParse
   * @return 04934122302401 for german number +49 341 1234567
   */
  public String internationalPhoneNumber(final PhoneNumber numberToParse) {
    String intern = phoneUtil.format(numberToParse, PhoneNumberFormat.INTERNATIONAL);
    intern = intern.replaceAll("[^\\d]", "");
    while (intern.length() < 14) {
      intern = "0".concat(intern);
    }
    return intern;
  }
}
