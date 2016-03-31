# More Readable Geb Specifications

Geb is a very nice tool but especially with testing JavaScript it can become very messy. I've migrated most of the Geb specs to use quite simple DSL to declare the website functions.

The DSL expects to have following static import into your specification:

```
import static org.modelcatalogue.core.geb.Common.*
```

Your class has to extend

```
org.modelcatalogue.core.geb.AbstractModelCatalogueGebSpec
```

### Authentication

You can pick user of one of three preferred roles to login as the first line of your spec:

```
login admin   // or
login curator // or
login viewer
```

### Navigating to Particular Catalogue Element

To simply navigate to particular element you need to know the path in the data model tree view.

You start with selecting the desired data model and than you continue the path with `/` (slash - divide) as you were clicking on the tree view.

```
select('NHIC') / 'NHIC' / 'Data Classes' / 'NHIC Dataset'
```

Please, take a note you need to repeat the data model name as the first thing to expand the data model root node in the tree view.

### Page Elements
In a next section I'll be referring to page element in the DSL. You can use plain string selectors such as `h3` or for more complex use cases a `CatalogueContent` object which basically consist from a selector and attributes acceptable by `$` method. Model Catalogue actions have their counterparts in instances of `CatalogueActions` which are defined by role such as `navigation-right` and the action id e.g. `search-menu`. You can simply declare a nested action as well:

```
private static final CatalogueAction reindexCatalogue =
    CatalogueAction.runFirst('navigation-right', 'admin-menu', 'reindex-catalogue')
```

The `CatalogueAction` doesn't distinguish between menu items and action buttons enabling easy change in the future without the need to rewrite the specs.

You can also declare page content as `Closure<Navigator>` but it can't be stored in any variable and has to be used inlined e.g.

```
check { $('span.foo').parent() } displayed
```

### Checking Element Presence
Once you have page elements declared (usually as constant in the specification class) you can check if they are present.

```
check content displayed            // wait until the content is displayed
check content gone                 // wait until the content is gone
check content enabled              // wait until the button is enabled
check content disabled             // wait until the button is disabled
check content has 'text-success'   // wait until the content has class 'text-success'
check content is 'Hello'           // wait until the text value of content is 'Hello'
check content contains 'Hello'     // wait until the text value of content contains 'Hello'
check content present once         // wait until the content selects exactly one element
check content present 3 times      // wait until the content selects exactly 3 elements
check content test {               // wait until the content is editable
    it.editable                    //   use this for features not yet supported by the DSL
}
```


### Interacting with Inputs
You can fill the inputs by referring to its name or id or any `CatalogueContent` instance.

```
fill 'name' with 'Foo'                               // fill the input with name or id 'name' with 'Foo'
fill content with 'Foo'                              // fill the content with `Foo`
fill content with 'Foo' and prefer first item        // select the first item from content assist if exists
fill content with 'Foo' and pick first existing item // select the fist item and fail if it does not exist
```

### Executing Actions and Other Mouse Interactions
When you want to click certain element you can do so simply with `click` method and the content or action.

```
click save                            // execute the action stored in 'save' variable
click 'a'                             // click on the link
click { $('.my-class' ).parent('a') } // click on the link which is parent of element having 'my-class' class
click first of save                   // execute the first action stored defined by the 'save' variable
click last of save                    // execute the last action stored defined by the 'save' variable
```

### Other

_Selecting  tab_

```
selectTab 'name' // e.g. 'history', 'properties', ...
```

_Refreshing browser_

```
refresh browser
```

_Scrolling Window_

```
scroll up       // scroll up by 250 px
scroll down     // scroll down by 250 px
```

_Add import to the data model and than continue selecting the items in the tree view_

```
select 'Test 1'
addDataModelImport 'NHIC', 'SI', 'XmlSchema'
selectInTreeView 'Test 1'
selectInTreeView 'Data Classes'
```

### Usual Pitfals
Very often you would like to check if the modal dialog closes. You can do so with

```
check backdrop gone
```

Also some actions cannot be executed until there are any growl messages shown. To wait until they disappear you can do following before attempting to click

```
check closeGrowlMessage gone
```
