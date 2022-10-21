package com.coolbeevip.xml.xsd2java;

import com.github.javafaker.Faker;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Calendar;

import static org.hamcrest.MatcherAssert.assertThat;

public class MarshalTest {

  @Test
  @SneakyThrows
  public void userMarshalTest() {
    String filePath = "target/user.xml";
    JAXBContext context = JAXBContext.newInstance(User.class);

    ObjectFactory factory = new ObjectFactory();
    User userSchema = factory.createUser();
    userSchema.setId(0);
    userSchema.setName("B Tom");
    userSchema.setCreated(Calendar.getInstance());

    getMarshaller(context).marshal(userSchema, new File(filePath));

    User user = (User) context.createUnmarshaller().unmarshal(new FileReader(filePath));
    assertThat(user.getName(), Matchers.is(user.getName()));
  }

  @Test
  @SneakyThrows
  public void groupMarshalTest() {
    String filePath = "target/group.xml";

    ObjectFactory factory = new ObjectFactory();
    Group groupSchema = factory.createGroup();
    groupSchema.setName("A组");

    Faker faker = new Faker();
    for (int i = 0; i < 10; i++) {
      User userSchema = factory.createUser();
      userSchema.setId(i);
      userSchema.setName(faker.name().fullName());
      userSchema.setCreated(Calendar.getInstance());
      groupSchema.getUsers().add(userSchema);
    }

    JAXBContext context = JAXBContext.newInstance(Group.class);
    getMarshaller(context).marshal(groupSchema, new File(filePath));

    Group group = (Group) context.createUnmarshaller().unmarshal(new FileReader(filePath));
    assertThat(group.getName(), Matchers.is(groupSchema.getName()));
    assertThat(group.getUsers().size(), Matchers.is(groupSchema.getUsers().size()));
  }

  @Test
  @SneakyThrows
  public void unmarshalTest() {
    String filePath = "src/test/resources/group.xml";
    JAXBContext context = JAXBContext.newInstance(Group.class);
    Group group = (Group) context.createUnmarshaller().unmarshal(new FileReader(filePath));
  }

  private Marshaller getMarshaller(JAXBContext context) throws JAXBException {
    Marshaller jaxbMarshaller = context.createMarshaller();
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    return jaxbMarshaller;
  }
}
