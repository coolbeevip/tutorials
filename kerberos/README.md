https://www.baeldung.com/spring-security-kerberos

```shell
bin/kadmin.sh /kdc/kerby-data/conf/ -k /kdc/kerby-data/keytabs/admin.keytab

KadminTool.local: addprinc -pw password HTTP/demo.coolbeevip.com@EXAMPLE.COM
Principal "HTTP/demo.coolbeevip.com@EXAMPLE.COM" created.
KadminTool.local: ktadd -k /kdc/kerby-data/keytabs/demo.coolbeevip.com.keytab -norandkey HTTP/demo.coolbeevip.com@EXAMPLE.COM
Export Keytab to /kdc/kerby-data/keytabs/baeldung.keytab
KadminTool.local:
```


demo.coolbeevip.com 127.0.0.1