/* $HeadURL::                                                                         $
 * $Id$
 *
 * Copyright (c) 2006 by Topaz, Inc.
 * http://topazproject.org
 *
 * Licensed under the Educational Community License version 1.0
 * http://opensource.org/licenses/ecl1.php
 */
package org.topazproject.ws.pap;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;

import org.topazproject.authentication.ProtectedService;
import org.topazproject.authentication.UnProtectedService;
import org.topazproject.authentication.reauth.AbstractReAuthStubFactory;

/**
 * Factory class to generate Profiles web-service client stubs.
 *
 * @author Pradeep Krishnan
 */
public class ProfilesClientFactory extends AbstractReAuthStubFactory {
  private static ProfilesClientFactory instance = new ProfilesClientFactory();

  /**
   * Creates an Profiles service client stub..
   *
   * @param service the service configuration (url and credentials)
   *
   * @return Returns an Profiles service client stub.
   *
   * @throws MalformedURLException If the service url is misconfigured
   * @throws ServiceException If there is an error in creating the stub
   */
  public static Profiles create(ProtectedService service)
                         throws MalformedURLException, ServiceException {
    Profiles stub = instance.createStub(service);

    if (service.hasRenewableCredentials())
      stub = (Profiles) instance.newProxyStub(stub, service);

    return stub;
  }

  private Profiles createStub(ProtectedService service)
                       throws MalformedURLException, ServiceException {
    URL                    url     = new URL(service.getServiceUri());
    ProfilesServiceLocator locator = new ProfilesServiceLocator();

    locator.setMaintainSession(true);

    Profiles profiles = locator.getProfilesServicePort(url);

    if (service.requiresUserNamePassword()) {
      Stub stub = (Stub) profiles;
      stub._setProperty(Stub.USERNAME_PROPERTY, service.getUserName());
      stub._setProperty(Stub.PASSWORD_PROPERTY, service.getPassword());
    }

    return profiles;
  }

  /**
   * Creates a client that does not require any authentication.
   *
   * @param profilesServiceUri the uri for profiles service
   *
   * @return Returns an Profiles service client stub.
   *
   * @throws MalformedURLException If the service url is misconfigured
   * @throws ServiceException If there is an error in creating the stub
   */
  public static Profiles create(String profilesServiceUri)
                         throws MalformedURLException, ServiceException {
    return create(new UnProtectedService(profilesServiceUri));
  }

  /*
   * @see org.topazproject.authentication.StubFactory#newStub
   */
  public Object newStub(ProtectedService service) throws Exception {
    return createStub(service);
  }
}
