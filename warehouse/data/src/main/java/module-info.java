module warehouse.data {
  requires transitive warehouse.core;
  requires com.fasterxml.jackson.core;
  requires transitive com.fasterxml.jackson.databind;

  exports data;
  opens data;
}
