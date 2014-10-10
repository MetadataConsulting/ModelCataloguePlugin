angular.module('mc.core.ui.bs.shoppingCart', ['mc.core.ui.shoppingCart']).run [ '$templateCache', ($templateCache) ->
    $templateCache.put 'modelcatalogue/core/ui/shoppingCart.html', '''
      <div class="shopping-cart"><span class="fa fa-fw fa-shopping-cart text-muted"></span></div>
    '''
  ]