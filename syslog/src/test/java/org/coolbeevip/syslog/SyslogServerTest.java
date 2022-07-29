package org.coolbeevip.syslog;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.SocketUtils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class SyslogServerTest {

  private static SyslogService syslogService;
  private static List<Rfc5424SyslogEvent> events = new ArrayList<>();

  private final String rfc5424Message
      = "<34>1 2003-10-11T22:14:15.003Z mymachine.example.com su - ID47 - BOM'su root' failed for lonvick on /dev/pts/8";
  private final String rfc5424WithStructuredData = "<34>1 2003-10-11T22:14:15.003Z mymachine.example.com su - ID47 "
      + "[exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"] BOM'su root' failed for lonvick on /dev/pts/8";

  private final String rfc5424LongMessage = "<34>1 2003-10-11T22:14:15.003Z mymachine.example.com su - ID47 - "
      + "Lorem ipsum dolor sit amet, tempor democritum vix ad, est partiendo laboramus ei. "
      + "Munere laudem commune vis ad, et qui altera singulis. Ut assum deleniti sit, vix constituto assueverit appellantur at, et meis voluptua usu. "
      + "Quem imperdiet in ius, mei ex dictas mandamus, ut pri tation appetere oportere. Et est harum dictas. \n Omnis quaestio mel te, ex duo autem molestie. "
      + "Ei sed dico minim, nominavi facilisis evertitur quo an, te adipiscing contentiones his. Cum partem deseruisse at, ne iuvaret mediocritatem pro. "
      + "Ex prima utinam convenire usu, volumus legendos nec et, natum putant quo ne. Invidunt necessitatibus at ius, ne eum wisi dicat mediocrem. "
      + "\n Cu usu odio labores sententiae. Ex eos duis singulis necessitatibus, dico omittam vix at. Sit iudico option detracto an, sit no modus exerci oportere. "
      + "Vix dicta munere at, no vis feugiat omnesque convenire. Duo at quod illum dolor, nec amet tantas iisque no, mei quod graece volutpat ea.\n "
      + "Ornatus legendos theophrastus id mei. Cum alia assum abhorreant et, nam indoctum intellegebat ei. Unum constituto quo cu. "
      + "Vero tritani sit ei, ea commodo menandri usu, ponderum hendrerit voluptatibus sed te. "
      + "\n Semper aliquid fabulas ei mel. Vix ei nullam malorum bonorum, movet nemore scaevola cu vel. "
      + "Quo ut esse dictas incorrupte, ex denique splendide nec, mei dicit doming omnium no. Nulla putent nec id, vis vide ignota eligendi in.";

  @Test
  @SneakyThrows
  public void test() {

    try (DatagramSocket socket = new DatagramSocket()) {

      socket.send(packet(syslogService.getHost(), syslogService.getPort(), rfc5424Message));
      socket.send(packet(syslogService.getHost(), syslogService.getPort(), rfc5424WithStructuredData));
      socket.send(packet(syslogService.getHost(), syslogService.getPort(), rfc5424LongMessage));

    }
    Awaitility.await().atMost(60, SECONDS).until(() -> events.size() == 3);
  }

  private DatagramPacket packet(String host, int port, String message) throws UnknownHostException {
    InetAddress address = InetAddress.getByName(host);
    return new DatagramPacket(message.getBytes(StandardCharsets.UTF_8), message.getBytes(StandardCharsets.UTF_8).length, address, port);
  }

  @Before
  public void setup() {
    events.clear();
  }


  @BeforeClass
  @SneakyThrows
  public static void setupClass() {
    syslogService = new SyslogService("127.0.0.1", SocketUtils.findAvailableTcpPort(), SyslogProtocol.UDP, (event) -> {
      log.info("{}", event.toJSON());
      events.add(event);
    });

  }

  @AfterClass
  @SneakyThrows
  public static void tearDownClass() {
    syslogService.shutdown();
  }
}
