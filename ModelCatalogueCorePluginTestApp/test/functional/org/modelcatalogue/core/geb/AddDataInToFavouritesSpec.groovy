package org.modelcatalogue.core.geb

<<<<<<< Updated upstream
/**
 * Created by Berthe on 10/03/2017.
 */
class AddDataInToFavouritesSpec {
=======

class AddDataInToFavouritesSpec extends AbstractModelCatalogueGebSpec {

    private static final String cancerModelsSelector = "full-width-link ng-binding"

    def"go to Login "(){
        when:
        loginViewer()
        waitFor (120){browser.title=='Data Models'}
        go'#/dataModels?type=catalogue'
        click()

        then:
        click cancerModelsSelector



    }
  def"select an element and click favourite"(){


  }
>>>>>>> Stashed changes
}
