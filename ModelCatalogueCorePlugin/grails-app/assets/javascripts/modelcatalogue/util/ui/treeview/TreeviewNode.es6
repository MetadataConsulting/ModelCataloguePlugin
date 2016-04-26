(function(window, angular){
    const symbols = {
        // collapsed: Symbol('collapsed'),
        item: Symbol('item'),
        id: Symbol('id'),
        numberOfChildren: Symbol('numberOfChildren'),
        loadingChildren: Symbol('loadingChildren'),
        showingMore: Symbol('showingMore'),
        children: Symbol('children'),
        active: Symbol('active')
    };

    const cache = {}; // or new WeakMap() if memory problems occur;


    class TreeviewNode {

        constructor(id, item) {
            this.collapsed = true;
            this[symbols.item] = item;
            this[symbols.id] = id;
            this[symbols.active] = false;
            this[symbols.loadingChildren] = false;
            this[symbols.numberOfChildren] = 0;
            this[symbols.children] = []
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

        get numberOfChildren() {
            return this[symbols.numberOfChildren];
        }

        get loadingChildren() {
            return this[symbols.loadingChildren];
        }

        get active() {
            return this[symbols.active];
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
