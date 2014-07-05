function TextPrinter() {
  this.text = "" ;
  
  this.getText = function() { return this.text ; };

  this.print = function(text) { 
    this.text += text ; 
  };

  this.println = function(text) { 
    this.print('    ');
    this.text += text + '\n'; 
  };

  this.h1 = function(text) { this.print("#" + text + "#\n"); };

  this.h2 = function(text) { this.print(" ##" + text + "##\n"); };

  this.h3 = function(text) { this.print("  ###" + text + "###\n"); };

  this.printJSON = function(obj) { 
    this.print(JSON.stringify(obj, null, "  ")) ; 
    this.print('\n');
  };

  this.printError = function(e) { 
    var stacktrace = e.stack || e.stacktrace || "";
    this.println(e.message) ; 
    this.println(stacktrace) ; 
  };

  this.printTable = function(array, properties) {
    var fconfig = null ;
    if(properties[0].substring) {
      fconfig = [] ;
      for(var i = 0; i < array.length; i++) {
        for(var j = 0; j < properties.length; j++) {
          var prop = properties[j];
          var cellValue = this.getPropByString(array[i], prop) ;
          if(fconfig[j] == null) {
            fconfig[j] = { field: prop, width: prop.length }
          } 
          if(fconfig[j].width <= cellValue.length) {
            fconfig[j].width = cellValue.length;
          }
        }
      }
    } else {
      fconfig = properties ;
      for(var i = 0; i < array.length; i++) {
        for(var j = 0; j < fconfig.length; j++) {
          if(fconfig[j].width == null) {
            var header = fconfig[j].header ;
            if(header == null) header = fconfig[j].field ;
            fconfig[j].width = header.length;
          }
          var cellValue = this.getPropByString(array[i], fconfig[j].field) ;
          if(fconfig[j].width <= cellValue.length) {
            fconfig[j].width = cellValue.length;
          }
        }
      }
    }
    this.printRowHeader(fconfig) ;
    this.printRows(array, fconfig) ;
    this.print('\n') ;
  };

  this.printRowHeader = function(fconfig) {
    this.print('    ');
    for(var i = 0; i < fconfig.length; i++) {
      var header = fconfig[i].header ;
      if(header == null) header = fconfig[i].field ;
      var width = fconfig[i].width;
      if(fconfig[i].maxWidth && width > fconfig[i].maxWidth) {
        width = fconfig[i].maxWidth;
      }
      this.printCell(header, width) ;
    }
    this.print('\n') ;
  };

  this.printRows = function(array, fconfig) {
    for(var i = 0; i < array.length; i++) {
      this.printRow(array[i], fconfig) ;
    }
  };

  this.printRow = function(bean, fconfig) {
    this.print('    ');
    for(var i = 0; i < fconfig.length; i++) {
      var cell = this.getPropByString(bean, fconfig[i].field) ;
      var width = fconfig[i].width ;
      if(fconfig[i].maxWidth && width > fconfig[i].maxWidth) {
        width = fconfig[i].maxWidth;
      }
      this.printCell(cell, width) ;
    }
    this.print('\n') ;
  };

  this.printCell = function(value, width) {
    if(value.length > width) {
      value = value.substring(0, 10) + "..." + value.substring(value.length - (width - 10 -3), value.length);
    }
    this.print(value) ;
    for(var i = value.length; i < width + 2; i++) {
      this.print(' ') ;
    }
  };

  this.getPropByString = function(obj, propString) {
    if (!propString) throw new Error("property '" + propSrting + "' is not valid") ;

    var prop, props = propString.split('.');
    for (var i = 0, iLen = props.length - 1; i < iLen; i++) {
      prop = props[i];

      var candidate = obj[prop];
      if (candidate !== undefined) {
        obj = candidate;
      } else {
        return "" ;
      }
    }
    var value = obj[props[i]] ;
    if(typeof value == 'number') {
      if(value % 1 === 0)  return value.toString();
      return value.toFixed(2).toString(); 
    }
    return value != null ? value.toString() : "" ;
  }
}

console = new TextPrinter() ;
console.print = function(text) { 
  java.lang.System.out.print(text) ;
};
console.println = function(text) { 
  java.lang.System.out.print(text + '\n') ;
};
