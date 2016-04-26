(function(window, angular){
    const symbols = {
        collapsed: Symbol('collapsed'),
        item: Symbol('item'),
        id: Symbol('id'),
        onCollapse: Symbol('onCollapse')
    };

    const cache = {}; // or new WeakMap() if memory problems occur;


    class TreeviewNode {

        constructor(id, item) {
            this[symbols.collapsed] = true;
            this[symbols.onCollapse] = function() {};
            this[symbols.item] = item;
            this[symbols.id] = id;
        }

        get collapsed() {
            return this[symbols.collapsed];
        }

        get expanded() {
            return !this[symbols.collapsed];
        }

        get item() {
            return this[symbols.item];
        }

        get id() {
            return this[symbols.id];
        }

        toggleCollapse() {
            this[symbols.collapsed] = !this[symbols.collapsed];
            this[symbols.onCollapse](this)
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
    }

    angular.module('mc.util.ui.treeview.TreeviewNode', [])
        .constant('TreeviewNodeFactory', TreeviewNodeFactory)
})(window, angular);
