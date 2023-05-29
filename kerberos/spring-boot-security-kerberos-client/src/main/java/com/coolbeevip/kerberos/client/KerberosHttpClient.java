package com.coolbeevip.kerberos.client;

import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.apache.hc.client5.http.auth.*;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.SPNegoSchemeFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.CloseMode;
import org.springframework.util.StringUtils;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class KerberosHttpClient extends CloseableHttpClient {
  private static final Credentials credentials = new NullCredentials();
  private final String userPrincipal;
  private final String keyTabLocation;
  private final CloseableHttpClient httpClient;

  public KerberosHttpClient(String krb5Conf, String userPrincipal, String keyTabLocation, boolean debug) {
    this.userPrincipal = userPrincipal;
    this.keyTabLocation = keyTabLocation;
    System.setProperty("java.security.krb5.conf", Paths.get(krb5Conf).normalize().toAbsolutePath().toString());
    System.setProperty("sun.security.krb5.debug", debug ? "true" : "false");
    System.setProperty("http.use.global.creds", "false");

    HttpClientBuilder builder = HttpClientBuilder.create();
    Lookup<AuthSchemeFactory> authSchemeRegistry = RegistryBuilder.<AuthSchemeFactory>create()
        .register(StandardAuthScheme.SPNEGO, new SPNegoSchemeFactory(
            KerberosConfig.custom()
                .setStripPort(KerberosConfig.Option.ENABLE)
                .setUseCanonicalHostname(KerberosConfig.Option.DISABLE)
                .build(),
            SystemDefaultDnsResolver.INSTANCE))
        .build();
    builder.setDefaultAuthSchemeRegistry(authSchemeRegistry);
    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(new AuthScope(null, -1), credentials);
    builder.setDefaultCredentialsProvider(credentialsProvider);
    this.httpClient = builder.build();
  }

  @Override
  protected CloseableHttpResponse doExecute(HttpHost target, ClassicHttpRequest request, HttpContext context) throws IOException {
    try {
      LoginContext lc = buildLoginContext();
      lc.login();
      Subject serviceSubject = lc.getSubject();
      return Subject.doAs(serviceSubject, (PrivilegedAction<CloseableHttpResponse>) () -> {
        try {
          return httpClient.execute(target, request, context);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } catch (LoginException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T execute(ClassicHttpRequest request, HttpClientResponseHandler<? extends T> responseHandler) throws IOException {
    try {
      LoginContext lc = buildLoginContext();
      lc.login();
      Subject serviceSubject = lc.getSubject();
      return Subject.doAs(serviceSubject, (PrivilegedAction<T>) () -> {
        try {
          return httpClient.execute(request, responseHandler);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } catch (LoginException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T execute(ClassicHttpRequest request, HttpContext context,
                       HttpClientResponseHandler<? extends T> responseHandler) throws IOException {
    try {
      LoginContext lc = buildLoginContext();
      lc.login();
      Subject serviceSubject = lc.getSubject();
      return Subject.doAs(serviceSubject, (PrivilegedAction<T>) () -> {
        try {
          return httpClient.execute(request, context, responseHandler);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } catch (LoginException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T execute(HttpHost target, ClassicHttpRequest request,
                       HttpClientResponseHandler<? extends T> responseHandler) throws IOException {
    try {
      LoginContext lc = buildLoginContext();
      lc.login();
      Subject serviceSubject = lc.getSubject();
      return Subject.doAs(serviceSubject, (PrivilegedAction<T>) () -> {
        try {
          return httpClient.execute(target, request, responseHandler);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } catch (LoginException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <T> T execute(HttpHost target, ClassicHttpRequest request, HttpContext context, HttpClientResponseHandler<?
      extends T> responseHandler) throws IOException {
    try {
      LoginContext lc = buildLoginContext();
      lc.login();
      Subject serviceSubject = lc.getSubject();
      return Subject.doAs(serviceSubject, (PrivilegedAction<T>) () -> {
        try {
          return httpClient.execute(target, request, context, responseHandler);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } catch (LoginException e) {
      throw new RuntimeException(e);
    }
  }

  private LoginContext buildLoginContext() throws LoginException {
    ClientLoginConfig loginConfig = new ClientLoginConfig(keyTabLocation, userPrincipal, null);
    Set<Principal> princ = new HashSet<Principal>(1);
    princ.add(new KerberosPrincipal(userPrincipal));
    Subject sub = new Subject(false, princ, new HashSet<Object>(), new HashSet<Object>());
    LoginContext lc = new LoginContext("", sub, null, loginConfig);
    return lc;
  }

  @Override
  public void close() throws IOException {
    if(this.httpClient!=null) this.httpClient.close();
  }

  @Override
  public void close(CloseMode closeMode) {
    if(this.httpClient!=null) {
      try {
        this.httpClient.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static class ClientLoginConfig extends Configuration {

    private final String keyTabLocation;
    private final String userPrincipal;
    private final Map<String, Object> loginOptions;

    public ClientLoginConfig(String keyTabLocation, String userPrincipal, Map<String, Object> loginOptions) {
      super();
      this.keyTabLocation = keyTabLocation;
      this.userPrincipal = userPrincipal;
      this.loginOptions = loginOptions;
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
      Map<String, Object> options = new HashMap<String, Object>();
      if (!StringUtils.hasText(keyTabLocation) || !StringUtils.hasText(userPrincipal)) {
        options.put("useTicketCache", "true");
      } else {
        options.put("useKeyTab", "true");
        options.put("keyTab", this.keyTabLocation);
        options.put("principal", this.userPrincipal);
        options.put("storeKey", "true");
      }
      options.put("doNotPrompt", "true");
      options.put("isInitiator", "true");
      if (loginOptions != null) {
        options.putAll(loginOptions);
      }
      return new AppConfigurationEntry[]{new AppConfigurationEntry(
          "com.sun.security.auth.module.Krb5LoginModule",
          AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options)};
    }
  }

  private static class NullCredentials implements Credentials {

    @Override
    public Principal getUserPrincipal() {
      return null;
    }

    @Override
    public char[] getPassword() {
      return new char[0];
    }
  }
}
