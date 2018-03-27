package org.aksw.leopard.facebook;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;

public class Test {

  public static final Logger LOG = LogManager.getLogger(Test.class);

  public void facebook() throws FacebookException {
    final Facebook facebook = new FacebookFactory().getInstance();
    LOG.info(facebook.getOAuthAppAccessToken());
  }
}
