describe('mc.util.objectVisitor', function () {
  beforeEach(module('mc.util.objectVisitor'));

  it("should exist", inject(function (objectVisitor) {
    expect(objectVisitor).toBeDefined();
    expect(objectVisitor.visit).toBeFunction();

  }));

  it("visit recursive structure", inject(function (objectVisitor, $rootScope) {
    let structure = {
        id: 12345,
        name: 'Data Model',
        dataClass: {
          name: 'Data Class'
        }
    };
    structure.dataClass.dataModel = structure;


    let result = objectVisitor.visit(structure, function (value, name){
      if (name === 'dataModel') {
        return {id: value.id, name: value.name};
      }
      return value;
    });

    $rootScope.$digest();

    expect(result).toBeDefined();
    expect(result.dataClass).toBeDefined();
    expect(result.dataClass.dataModel).toBeDefined();
    expect(result.dataClass.dataModel.id).toBe(structure.id);
    expect(result.dataClass.dataModel.name).toBe(structure.name);
    expect(result.dataClass.dataModel.dataClass).toBeUndefined();

  }));

});
