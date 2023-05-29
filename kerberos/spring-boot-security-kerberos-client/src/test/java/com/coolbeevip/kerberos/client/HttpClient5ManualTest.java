package com.coolbeevip.kerberos.client;

import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.apache.hc.client5.http.auth.*;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.SPNegoSchemeFactory;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.security.kerberos.client.KerberosRestTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
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

import static org.hamcrest.MatcherAssert.assertThat;

public class HttpClient5ManualTest {
  private static final Credentials credentials = new NullCredentials();
  static String krb5Conf = "/Users/zhanglei/Work/github/tutorials/kerberos/krb-workdir/krb5.conf";
  static String userPrincipal = "client/localhost@EXAMPLE.COM";
  static String keyTabLocation = "/Users/zhanglei/Work/github/tutorials/kerberos/krb-workdir/example.keytab";

  @Test
  public void test() throws IOException, ParseException {
    System.setProperty("java.security.krb5.conf", Paths.get(krb5Conf).normalize().toAbsolutePath().toString());
    System.setProperty("sun.security.krb5.debug", "true");
    System.setProperty("http.use.global.creds", "false");

    try {
      LoginContext lc = buildLoginContext();
      lc.login();
      Subject serviceSubject = lc.getSubject();
      String value = Subject.doAs(serviceSubject, (PrivilegedAction<String>) () -> {
        CloseableHttpClient client = buildHttpClient();
        try {
          CloseableHttpResponse response = client.execute(new HttpGet("http://localhost:8080/endpoint"));
          return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
      assertThat(value, Matchers.is("data from kerberized server"));
    } catch (Exception e) {
      throw new RestClientException("Error running rest call", e);
    }
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

    private ClientLoginConfig(String keyTabLocation, String userPrincipal, Map<String, Object> loginOptions) {
      super();
      this.keyTabLocation = keyTabLocation;
      this.userPrincipal = userPrincipal;
      this.loginOptions = loginOptions;
    }

    @Override
    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {

      Map<String, Object> options = new HashMap<String, Object>();

      // if we don't have keytab or principal only option is to rely on
      // credentials cache.
      if (!StringUtils.hasText(keyTabLocation) || !StringUtils.hasText(userPrincipal)) {
        // cache
        options.put("useTicketCache", "true");
      } else {
        // keytab
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

  private static class CallbackHandlerImpl implements CallbackHandler {

    private final String userPrincipal;
    private final String password;

    private CallbackHandlerImpl(String userPrincipal, String password) {
      super();
      this.userPrincipal = userPrincipal;
      this.password = password;
    }

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

      for (Callback callback : callbacks) {
        if (callback instanceof NameCallback) {
          NameCallback nc = (NameCallback) callback;
          nc.setName(userPrincipal);
        } else if (callback instanceof PasswordCallback) {
          PasswordCallback pc = (PasswordCallback) callback;
          pc.setPassword(password.toCharArray());
        } else {
          throw new UnsupportedCallbackException(callback, "Unknown Callback");
        }
      }
    }
  }
}
