angular.module('mc.core.ui.catalogueElementTreeview.item').run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/core/ui/catalogueElementTreeview/item.html', '''
    <!-- This is a template for a recursive directive catalogue-element-treeview-item. It assumes the directive
          is called within a ul element. -->
    <li class="catalogue-element-treeview-item">
      <!-- Content of the node itself -->
      <div class="catalogue-element-treeview-text-content"
           ng-class="{'active': node.active, 'archived': element.$$archived}">

        <span class="catalogue-element-treeview-labels">
          <!-- if element has no type say no data -->
          <span ng-if="::!element.elementType">
            <a class="catalogue-element-treeview-icon btn btn-link">
              <span class="fa fa-fw fa-ban"></span>
            </a>
            No Data
          </span>
          <!-- if element has type ... -->
          <a ng-if="::element.elementType" class="catalogue-element-treeview-icon  btn btn-link"
             ng-click="select(node)">
            <!-- carets depending on node.collapsed -->
            <span ng-if="!extraParameters.prefetch &amp;&amp; node.numberOfChildren &amp;&amp; !node.loadingChildren">
              <span class="fa fa-fw fa-caret-right text-muted" ng-if="node.collapsed">
              </span>
              <span class="fa fa-fw fa-caret-down text-muted" ng-if="!node.collapsed">
              </span>
            </span>
            <!-- other icons depending on other conditions -->
            <span class="fa fa-fw fa-refresh text-muted" ng-if="node.loadingChildren"></span>
            <span class="fa fa-fw text-muted" ng-if="extraParameters.prefetch"></span>
            <span class="fa fa-fw" ng-if="!node.numberOfChildren && !node.loadingChildren"></span>
          </a>

          <span class="catalogue-element-treeview-name"
                ng-class="{
                  'text-warning': element.status == 'DRAFT',
                  'text-info': element.status == 'PENDING',
                  'text-danger': (element.status == 'DEPRECATED' || element.undone)
                }"
                ng-click="select(node)">
            <span ng-class="node.icon" class="text-muted"></span>
              {{node.name}}
            <span class="text-muted">
              {{node.metadataOccurrencesToAsterisk}}
            </span>
            <small class="text-muted" ng-if="element.$$localName">{{element.name}}</small>
            <small class="text-muted">
              <span ng-if="::element.latestVersionId"
                    class="catalogue-element-treeview-version-number">
                {{node.dataModelWithVersion}}
              </span>
            </small>
          </span>
          <small class="text-muted" ng-if="::node.href">
            <a class="catalogue-element-treeview-link"
               ng-href="{{::node.href}}"
               title="{{node.name}}"
               target="_blank">
              <span class="fa fa-external-link text-muted"></span>
            </a>
          </small>
        </span>
      </div>
      <!-- show the children -->
      <ul ng-if="node.children" ng-hide="node.collapsed" class="catalogue-element-treeview-list">
        <!-- recursive call to catalogue-element-treeview-item for each child node -->
        <catalogue-element-treeview-item treeview="::treeview" sly-repeat="child in node.children"
                                         extra-parameters="::{'path': path, 'descendPath': extraParameters.descendPath.concat(child.id)}"
                                         element="child">
        </catalogue-element-treeview-item>
        <!-- show more -->
        <li ng-if="node.numberOfChildren > node.children.length" class="catalogue-element-treeview-item">
          <span class="catalogue-element-treeview-labels" ng-click="node.showMore()">
            <a class="catalogue-element-treeview-icon btn btn-link catalogue-element-treeview-show-more"
               ng-class="'show-more-' + nodeid">
              <span class="fa fa-fw fa-chevron-down"></span>
            </a>
            <a class="text-muted">
              Show more
            </a>
          </span>
        </li>
      </ul>
    </li>
    '''
]
###
This used to be in the div with class="catalogue-element-treeview-text-content":
<!--
The following displays a "total" number of children of a node in the treeview.
  It doesn't seem to display what we want for certain elements such as the Data Model. It does seem to be useful for Data Classes and Data Element Tags.
At the top level numberOfChildren seems to come from DataModelController's "content" method. (descend="content").
We're turning it off for now since we don't understand how to fine-tune it. --James 7 June 2017
-->
<!--
<span class="badge pull-right" ng-if="node.numberOfChildren" ng-switch="node.numberOfChildren">
<span ng-switch-default>{{::node.numberOfChildren}}</span>
          <span ng-switch-when="2147483647" class="fa fa-question fa-inverse"></span>
</span>
        -->

###
