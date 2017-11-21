describe('TreeviewNode', function() {

    beforeEach(module('modelcatalogue.core.ui.states.dataModel.components.catalogueElementTreeview.model'));

    it('can construct new treeview node', inject(function(TreeviewNodeFactory){
        const id = '/foo/bar/123';
        const item = {name: 'Test Item'};
        const anotherItem = {name: 'Something different'};

        expect(TreeviewNodeFactory).toBeDefined();
        let node = TreeviewNodeFactory.create(id, item);

        expect(node).toBeDefined();
        expect(node.collapsed).toBeTruthy();
        expect(node.expanded).toBeFalsy();
        expect(node.item).toBe(item);
        expect(node.id).toBe(id);

        node.toggleCollapse();

        expect(node.collapsed).toBeFalsy();
        expect(node.expanded).toBeTruthy();

        node.toggleCollapse();
        expect(node.collapsed).toBeTruthy();
        expect(node.expanded).toBeFalsy();

        let another = TreeviewNodeFactory.create(id, anotherItem);

        // always the very same instance is returned for same id
        expect(node).toBe(another);

        // but the item is updated
        expect(node.item).toBe(anotherItem)

    }));

});
