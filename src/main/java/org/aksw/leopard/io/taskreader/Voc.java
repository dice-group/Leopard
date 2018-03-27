package org.aksw.leopard.io.taskreader;

public class Voc {

  public static String aksw = "http://swc2017.aksw.org/";
  public static String akswHasTruthValue = aksw.concat("hasTruthValue");

  public static String mdaas = "http://ont.thomsonreuters.com/mdaas/";
  public static String mdaasIsDomiciledIn = mdaas.concat("isDomiciledIn");

  public static String permid = "http://permid.org/";
  public static String permidOntology = permid.concat("ontology/");
  public static String permidHasHeadquartersPhoneNumber =
      permidOntology.concat("organization/hasHeadquartersPhoneNumber");
  public static String permidHasLatestOrganizationFoundedDate =
      permidOntology.concat("organization/hasLatestOrganizationFoundedDate");

  public static String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
  public static String rdfSubject = rdf.concat("subject");
  public static String rdfPredicate = rdf.concat("predicate");
  public static String rdfObject = rdf.concat("object");
  public static String rdfType = rdf.concat("type");

  public static String vcard = "http://www.w3.org/2006/vcard/ns#";
  public static String vcardOrganizationName = vcard.concat("organization-name");
  public static String vcardHasURL = vcard.concat("hasURL");

  public static String xmls = "http://www.w3.org/2001/XMLSchema#";
  public static String xmlsAnyURI = xmls.concat("anyURI");
  public static String xmlDouble = xmls.concat("double");

}
