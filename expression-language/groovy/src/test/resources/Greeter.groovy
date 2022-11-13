class Greeter {
  String join(Map<String, Object> arguments) {
    arguments.get("first") + arguments.get("delimiter") + arguments.get("last")
  }
}
