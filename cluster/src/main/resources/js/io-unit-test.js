var fconfig =[
  {field: "string", header: "STRING"},
  {field: "int",    header: "INTEGER"},
  {field: "boolean",    header: "BOOLEAN"},
  {field: "boolean",    header: "BOOLEAN"},
  {field: "nested.string",    header: "Nested"},
]

var objects = [
  {"string": "a string 1", "int": 10, "boolean": true, "nested": {"string": "nested string"}},
  {"string": "a string 22", "int": 10, "boolean": true},
  {"string": "a string 333", "int": 10, "boolean": true}
]

console.printTable(objects, ["string", "int", "boolean"]);
console.printTable(objects, fconfig);


var obj = {foo: {bar: {baz: 'baz value....'}}};

console.println(console.getPropByString(obj, 'foo.bar.baz')); // x
console.println(console.getPropByString(obj, 'foo.bar.baz.buk')); 
