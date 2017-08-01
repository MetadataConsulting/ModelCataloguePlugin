(function (window, angular) {
  const symbols = {
    item: Symbol('item'),
    id: Symbol('id')
  };

  const cache = {}; // or new WeakMap() if memory problems occur;

  class TreeviewNode {

    constructor(id, item) {
      this.collapsed = true;
      /** Number of children: Not the same as children.length. This is some
      /* calculated true number of children in the database, whereas
      /* this.children contains only the children currently loaded from the database.*/
      this.numberOfChildren = 2147483647; // integer max number
      this.loadingChildren = false;
      this[symbols.item] = item;
      this[symbols.id] = id;
      this.active = false;
      this.children = [];
    }

    get expanded() {
      return !this.collapsed;
    }

    get item() {
      return this[symbols.item];
    }

    get id() {
      return this[symbols.id];
    }

    get name() {
      if (this.item) {
        return this.item.$$localName ? this.item.$$localName : this.item.name;
      }
    }

    get href() {
      if (this.item && angular.isFunction(this.item.href)) {
        return this.item.href();
      }
    }

    get icon() {
      if (this.item && angular.isFunction(this.item.getIcon)) {
        return this.item.getIcon();
      }
    }

    get dataModelWithVersion() {
      if (this.item && angular.isFunction(this.item.getDataModelWithVersion)) {
        return this.item.getDataModelWithVersion();
      }
    }

    toggleCollapse() {
      this.collapsed = !this.collapsed;
    }

  }

  class TreeviewNodeFactory {
    static create(id, item) {
      let cached = cache[id];
      if (cached) {
        cached[symbols.item] = item;
        return cached;
      }
      else {
        return cache[id] = new TreeviewNode(id, item);
      }
    }

    static get(id) {
      return cache[id]
    }
  }

  angular.module('modelcatalogue.core.components.catalogueElementTreeview.model')
    .constant('TreeviewNodeFactory', TreeviewNodeFactory)
})(window, angular);
