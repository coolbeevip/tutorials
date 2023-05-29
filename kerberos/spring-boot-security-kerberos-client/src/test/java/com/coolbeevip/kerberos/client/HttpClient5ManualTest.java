package com.coolbeevip.kerberos.client;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.security.auth.login.LoginException;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;

public class HttpClient5ManualTest {
  static String krb5Conf = "/Users/zhanglei/Work/github/tutorials/kerberos/krb-workdir/krb5.conf";
  static String userPrincipal = "client/localhost@EXAMPLE.COM";
  static String keyTabLocation = "/Users/zhanglei/Work/github/tutorials/kerberos/krb-workdir/example.keytab";

  @Test
  public void test2() throws IOException {
    KerberosHttpClient client = new KerberosHttpClient(krb5Conf, userPrincipal, keyTabLocation, true);
    try (CloseableHttpResponse response = client.execute(new HttpGet("http://localhost:8080/endpoint"))) {
      assertThat(response.getCode(), Matchers.is(200));
      assertThat(EntityUtils.toString(response.getEntity(), "UTF-8"), Matchers.is("data from kerberized server"));
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}
