(function(window, angular){
    const symbols = {
        item: Symbol('item'),
        id: Symbol('id')
    };

    const cache = {}; // or new WeakMap() if memory problems occur;


    class TreeviewNode {

        constructor(id, item) {
            this.collapsed = true;
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
            return cache[id] = new TreeviewNode(id, item);
        }

        static get(id) {
            return cache[id]
        }
    }

    angular.module('mc.util.ui.treeview.TreeviewNode', [])
        .constant('TreeviewNodeFactory', TreeviewNodeFactory)
})(window, angular);
