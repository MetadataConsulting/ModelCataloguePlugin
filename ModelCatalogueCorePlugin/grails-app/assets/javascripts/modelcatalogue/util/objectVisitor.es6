angular.module('mc.util.objectVisitor', ['mc.util.objectVisitor']).factory('objectVisitor', function($q){

    let symbols = {
      pendingPromises: Symbol('pendingPromise'),
    };

    class ObjectVisitor {

      visit(object, transformation, indexOrName = '$root') {
        let objectVisitor = this, deferred = $q.defer();

        if (angular.isObject(object) || angular.isArray(object)) {
          if (!object[symbols.pendingPromises]) {
            object[symbols.pendingPromises] = {};
          }
          object[symbols.pendingPromises][indexOrName] = deferred.promise;
          angular.forEach(object, function(value, name){
            let existing = value && value[symbols.pendingPromises] ? value[symbols.pendingPromises][name] : undefined;
            if (existing) {
              existing.then(function (result) {
                if (result !== object[name]) {
                  object[name] = result;
                }
              })
            } else {
              let result = objectVisitor.visit(value, transformation, name);
              if (result !== object[name]) {
                object[name] = result;
              }
            }
          });
        }

        let result = transformation(object, indexOrName);
        if ((angular.isObject(result) || angular.isArray(result)) && result[symbols.pendingPromises]) {
          delete result[symbols.pendingPromises][indexOrName];
        }
        deferred.resolve(result);
        return result;
      }
    }

    return new ObjectVisitor();
});
