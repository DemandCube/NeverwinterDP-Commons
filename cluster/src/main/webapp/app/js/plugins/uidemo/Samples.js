define([
  'jquery', 
  'underscore', 
  'backbone',
  'util/DateTime',
  'module/Module',
  'ui/UIBreadcumbs',
  'ui/UICollapsible',
  'ui/UIBean',
  'ui/UITable',
], function($, _, Backbone, DateTime, module, UIBreadcumbs, 
            UICollabsible, UIBean, UITable) {
  
  var MyUICollabsible = UICollabsible.extend({
    label: "Sample UICollapsible", 
    config: {
      actions: [
        { 
          action: "back", label: "Back",
          onClick: function(thisUI) {
            console.log("on click back") ;
          }
        },
        {
          action: "save", label: "Save",
          onClick: function(thisUI) {
            console.log("on click save") ;
          }
        }
      ]
    }
  }) ;
  
  var SingleUIBean = UIBean.extend({
    label: "UIBean Single",
    config: {
      beans: {
        singleBean: {
          name: 'bean', label: 'Bean',
          fields: [
            {
              field:  "selectOption", label: "Select Option",
              select: {
                getOptions: function(field, bean) {
                  var options = [
                    { label: 'Option 1', value: 'opt1' },
                    { label: 'Option 2', value: 'opt2' }
                  ];
                  return options ;
                }
              }
            },
            { 
              field: "input",   label: "Input", required: true,  
              validator: { name: 'empty', errorMsg: "custom error message" } 
            },
            { 
              field: "intInput",   label: "Integer Input", defaultValue: 0,
              validator: { 
                name: 'integer', from: 0, to: 100, errorMsg: "Expect an integer from 0 to 100" 
              }
            },
            { 
              field: "multipleIntInput",  label: "Multiple Int Input", multiple: true,
              defaultValue: [ 10, 15 ], validator: { name: 'integer', to: 100} 
            },
            { 
              field: "numberInput",   label: "Number Input", defaultValue: 0,
              validator: { 
                name: 'number', from: 0, to: 100, errorMsg: "Expect a number from 0 to 100" 
              }
            },
            { 
              field: "email",  label: "Email", required: true,
              validator: { name: 'email'} 
            },
            { 
              field: "multipleInput",  label: "Multiple Input", multiple: true,
              validator: { name: 'empty'} 
            },
            { 
              field: "autocomplete",  label: "Autocomplete",
              custom: {
                getDisplay: function(bean) {
                  return bean.autocomplete == null ? null : bean.autocomplete;
                },
                set: function(bean, obj) { bean.autocomplete = obj ;},
                
                autocomplete: {
                  search: function(term, bean) {
                    var result = [
                      { value: 'value 1', label: "Value(1)" },
                      { value: 'value 2', label: "Value(2)" }
                    ];
                    return result ;
                  }
                }
              }
            },
            { 
              field: "customField",  label: "Custom Field",
              custom: {
                getDisplay: function(bean) {
                  return bean.nestedObject == null ? null : bean.nestedObject.field ;
                },
                set: function(bean, obj) { bean.nestedObject = obj ;},
                
                autocomplete: {
                  search: function(term, bean) {
                    console.log("search term: " + term + ', bean input = ' + bean.input) ;
                    var result = [
                      { value: { field: 'custom 1' }, label: "Custom(1)" },
                      { value: { field: 'custom 2' }, label: "Custom(2)" }
                    ];
                    return result ;
                  }
                }
              }
            },
            { 
              field: "datepicker",  label: "Date Picker",
              datepicker: { 
                getDate: function(bean) {
                  bean.datepicker = '04/02/2014 00:00:00 GMT+0700' ;
                  return DateTime.fromDateTimeToDDMMYYYY(bean.datepicker) ;
                },
                setDate: function(date, bean) {
                  bean.datepicker = DateTime.fromDDMMYYYYToDateTime(date) ;
                },
              } 
            },
            { field: "textarea", label: "Text Area", textarea: {} },
          ],
          edit: {
            disable: false , 
            actions: [
              {
                action:'save', label: "Save", icon: "check",
                onClick: function(thisUI, beanConfig, beanState) { 
                  console.log('on save..............') ;
                }
              },
              {
                action:'cancel', label: "Cancel", icon: "back",
                onClick: function(thisUI, beanConfig, beanState) { 
                  console.log('on cancel..............') ;
                }
              }
            ],
          },
          view: {
            actions: [
              {
                action:'more', label: "More", icon: "bars",
                onClick: function(thisUI, beanConfig, beanState) { 
                  console.log('on more....') ;
                }
              }
            ]
          }
        }
      }
    }
  });
  
  var MultipleUIBean = UIBean.extend({
    label: "UIBean Multiple",
    config: {
      type: 'multiple', // single, array, multiple
      beans: {
        bean1: {
          name: 'bean1', label: 'Bean 1',
          fields: [
            {
              field:  "option", label: "Bean 1 Option",
              option: [
               {label: 'Option 1', value: 'opt1'},
               {label: 'Option 2', value: 'opt2'}
              ]
            },
            { field: "input",   label: "Bean 1 Input" },
            { field: "multipleInput",  label: "Bean 1 Multiple Input", multiple: true
            },
            { field: "textarea", label: "Bean 1 Text Area", textarea: {} },
          ]
        },
        
        bean2: {
          name: 'bean2', label: 'Bean 2',
          fields: [
            {
              field:  "option", label: "Bean 2 Option",
              option: [
               {label: 'Option 1', value: 'opt1'},
               {label: 'Option 2', value: 'opt2'}
              ]
            },
            { field: "input",   label: "Bean 2 Input" },
            { field: "multipleInput",  label: "Bean 2 Multiple Input", multiple: true
            },
            { field: "textarea", label: "Bean 2 Text Area", textarea : {} },
          ]
        }
      }
    }
  });
  
  var ArrayUIBean = UIBean.extend({
    label: "UIBean Array",
    config: {
      type: 'array',
      beans: {
        bean: {
          label: 'Contact Bean',
          fields: [
            {
              field:  "option", label: "Option",
              option: [
               {label: 'Option 1', value: 'opt1'},
               {label: 'Option 2', value: 'opt2'}
              ]
            },
            { field: "input",   label: "Input" },
            { field: "multipleInput",  label: "Multiple Input", multiple: true
            },
            { field: "textarea", label: "Text Area", type: "textarea" }
          ]
        }
      }
    }
  });
  
  var SampleUITable = UITable.extend({
    label: "UITable Sample",
    config: {
      toolbar: {
        dflt: {
          actions: [
            {
              action: "onNew", icon: "add", label: "New", 
              onClick: function(thisTable) { 
                thisTable.onAddBean() ;
              } 
            }
          ]
        },
        filter: {
          fields: [
            { label: 'By Bean',   field: 'bean', type: 'string', operator: 'EQUAL' },
            { label: 'By Option',  field: 'option', type: 'string', operator: 'LIKE'},
            { label: 'By Textarea', field: 'textarea', operator: 'LIKE'}
          ],
          onFilter: function(thisTable, query) {
            console.log('onFilter');
          },
        }
      },
      
      bean: {
        label: 'Contact Bean',
        fields: [
          { 
            field: "bean",   label: "Bean", defaultValue: 'default value', 
            toggled: true, filterable: true,
            onClick: function(thisTable, row) {
              var bean = thisTable.getItemOnCurrentPage(row) ;
              console.log('on click bean ' + JSON.stringify(bean)) ;
            }
          },
          {
            field:  "option", label: "Option", toggled: true, filterable: true,
            option: [
             {label: 'Option 1', value: 'opt1'},
             {label: 'Option 2', value: 'opt2'}
            ]
          },
          { 
            field: "multipleInput",  label: "Multiple Input", multiple: true,
            defaultValue: [ 'val1', 'val2' ], validator: { name: 'empty'},
            toggled: true
          },
          { 
            field: "textarea", label: "Text Area", type: "textarea", 
            filterable: false, toggled: true
          },
          { 
            field: "customField",  label: "Custom Field",toggled: true,
            onClick: function(thisTable, row) {
              var bean = thisTable.getItemOnCurrentPage(row) ;
              console.log('on click bean ' + JSON.stringify(bean)) ;
            },
            custom: {
              getDisplay: function(bean) {
                return bean.nestedObject == null ? null : bean.nestedObject.field ;
              },
              set: function(bean, obj) { bean.nestedObject = obj ;},
              
              autocomplete: {
                search: function(term, bean) {
                  console.log("search term: " + term + ', bean input = ' + bean.input) ;
                  var result = [
                    { value: { field: 'custom 1' }, label: "Custom(1)" },
                    { value: { field: 'custom 2' }, label: "Custom(2)" }
                  ];
                  return result ;
                }
              }
            }
          },
        ],
        actions:[
          {
            icon: "delete", label: "Delete",
            onClick: function(thisTable, row) { 
              thisTable.markDeletedItemOnCurrentPage(row) ;
              console.log('Mark delete row ' + row);
            }
          },
          {
            icon: "edit", label: "Modified",
            onClick: function(thisTable, row) { 
              thisTable.onEditBean(row) ;
            }
          }
        ]
      }
    }
  });
  
  var UISamples = module.UIScreen.extend({
    initialize: function (options) {
    },

    activate: function() { 
      this.viewStack = new UIBreadcumbs({el: "#workspace"}) ;
      var UICollabsible = new MyUICollabsible() ;

      var bean = {
        option: 'opt2' ,
        input: "Input Field",
        multipleInput: ["input 1", "input 2"],
        textarea: "Text areaaaaaaaa aaaaaaaaa aaa aaaaaaaaaa aa aaa",
        nestedObject: {
          field: "nested object field"
        }
      };
      var singleUIBean = new SingleUIBean() ;
      singleUIBean.bind('singleBean', bean, true) ;
      UICollabsible.add(singleUIBean) ;
      
      var multipleUIBean = new MultipleUIBean() ;
      multipleUIBean.bind('bean1', jQuery.extend({}, bean)) ;
      multipleUIBean.bind('bean2', jQuery.extend({}, bean)) ;
      UICollabsible.add(multipleUIBean) ;
      
      var arrayUIBean = new ArrayUIBean() ;
      var beans = [jQuery.extend({}, bean), jQuery.extend({}, bean)] ;
      arrayUIBean.bindArray('bean', beans) ;
      UICollabsible.add(arrayUIBean) ;
      
      var sampleUITable = new SampleUITable() ;
      var beans = [] ;
      for(var i = 0; i < 151; i++) {
        var bean = {
          bean: "bean " + i,
          option: 'opt' + (i%2 + 1),
          multipleInput: ["input 1", "input 2"],
          textarea: "Textarea " + i,
          nestedObject: {
            field: "nested field " + i
          },
          peristableState: 'ORIGIN'
        };
        beans.push(bean) ;
      }
      sampleUITable.setBeans(beans) ;
      UICollabsible.add(sampleUITable) ;
      
      this.viewStack.push(UICollabsible) ;
    },
    
    deactivate: function() { }
    
  });
  
  return UISamples ;
});