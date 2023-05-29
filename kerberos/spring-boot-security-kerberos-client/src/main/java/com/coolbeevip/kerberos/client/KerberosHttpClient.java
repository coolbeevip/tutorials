package com.coolbeevip.kerberos.client;

import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.apache.hc.client5.http.auth.*;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.SPNegoSchemeFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.RegistryBuilder;
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

public class KerberosHttpClient implements AutoCloseable {
  private static final Credentials credentials = new NullCredentials();
  private final CloseableHttpClient httpClient;
  final String krb5Conf;
  final String userPrincipal;
  final String keyTabLocation;

  public KerberosHttpClient(String krb5Conf, String userPrincipal, String keyTabLocation, boolean debug) {
    this.krb5Conf = krb5Conf;
    this.userPrincipal = userPrincipal;
    this.keyTabLocation = keyTabLocation;
    System.setProperty("java.security.krb5.conf", Paths.get(krb5Conf).normalize().toAbsolutePath().toString());
    System.setProperty("sun.security.krb5.debug", debug ? "true" : "false");
    System.setProperty("http.use.global.creds", "false");
    this.httpClient = buildHttpClient();
  }

  public CloseableHttpResponse execute(ClassicHttpRequest request) throws LoginException {
    LoginContext lc = buildLoginContext();
    lc.login();
    Subject serviceSubject = lc.getSubject();
    return Subject.doAs(serviceSubject, (PrivilegedAction<CloseableHttpResponse>) () -> {
      try {
        return this.httpClient.execute(request);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private CloseableHttpClient buildHttpClient() {
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
    CloseableHttpClient httpClient = builder.build();
    return httpClient;
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
  public void close() throws Exception {
    if (this.httpClient != null) {
      this.httpClient.close();
    }
  }

  private static class NullCredentials implements Credentials {

    @Override
    public Principal getUserPrincipal() {
      return null;
    }

    @Override
    public char[] getPassword() {
      return null;
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
}
