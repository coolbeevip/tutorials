package org.coolbeevip.arrow.labs;

import org.apache.arrow.vector.BigIntVector;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.Float4Vector;
import org.apache.arrow.vector.Float8Vector;
import org.apache.arrow.vector.IntVector;
import org.apache.arrow.vector.VarBinaryVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.types.pojo.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class PersonVectorized {

  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final SchemaRepositories schemaRepositories;

  public PersonVectorized(SchemaRepositories schemaRepositories) {
    this.schemaRepositories = schemaRepositories;
  }


  public void vectorized(Person person, int index, VectorSchemaRoot personSchemaRoot) {
    setString(personSchemaRoot.getVector("firstName"), index, person.getFirstName());
    setString(personSchemaRoot.getVector("lastName"), index, person.getLastName());
    setInt(personSchemaRoot.getVector("age"), index, person.getAge());

    Schema addressSchema = this.schemaRepositories.getSchema(Address.class);
    List<FieldVector> childrenFromFields = personSchemaRoot.getVector("address")
        .getChildrenFromFields();
    Address address = person.getAddress();
    setString(childrenFromFields.get(0), index, address.getStreet());
    setInt(childrenFromFields.get(1), index, address.getStreetNumber());
    setString(childrenFromFields.get(2), index, address.getCity());
    setInt(childrenFromFields.get(3), index, address.getPostalCode());
  }

  private void setInt(FieldVector vector, int index, int value) {
    ((IntVector) vector).setSafe(index, value);
  }

  private void setLong(FieldVector vector, int index, long value) {
    ((BigIntVector) vector).setSafe(index, value);
  }

  private void setFloat(FieldVector vector, int index, float value) {
    ((Float4Vector) vector).setSafe(index, value);
  }

  private void setDouble(FieldVector vector, int index, double value) {
    ((Float8Vector) vector).setSafe(index, value);
  }

  private void setBytes(FieldVector vector, int index, byte[] bytes) {
    VarBinaryVector binaryVector = ((VarBinaryVector) vector);
    binaryVector.setIndexDefined(index);
    binaryVector.setValueLengthSafe(index, bytes.length);
    binaryVector.set(index, bytes);
  }

  private void setString(FieldVector vector, int index, String value) {
    ((VarCharVector) vector).setSafe(index, value.getBytes());
  }
}