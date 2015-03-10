package org.modelcatalogue.core.util
/**
 * @deprecated it doesn't play well with late evaluation (export as asset).
 */
@Deprecated
class ListCountAndType<T> extends ListAndCount implements ListWithTotalAndType<T>{
    Class<T> itemType
}
