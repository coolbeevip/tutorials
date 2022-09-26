package com.coolbeevip.xml.xsd2java;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;

import static org.hamcrest.MatcherAssert.assertThat;

public class marshalTest {

  @Test
  @SneakyThrows
  public void marshalTest() {
    // marshal
    UserRequest request = new UserRequest();
    request.setId(1);
    request.setName("Tom");
    request.setCreated(Calendar.getInstance());

    JAXBContext context = JAXBContext.newInstance(UserRequest.class);
    Marshaller mar = context.createMarshaller();
    mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    mar.marshal(request, new File("target/request.xml"));

    // unmarshal
    JAXBContext ctx = JAXBContext.newInstance(UserRequest.class);
    UserRequest req = (UserRequest) ctx.createUnmarshaller().unmarshal(new FileReader("target/request.xml"));

    // assert
    assertThat(request.getId(), Matchers.is(req.getId()));
    assertThat(request.getName(), Matchers.is(req.getName()));
    assertThat(request.getCreated().getTime(), Matchers.is(req.getCreated().getTime()));
  }
}
