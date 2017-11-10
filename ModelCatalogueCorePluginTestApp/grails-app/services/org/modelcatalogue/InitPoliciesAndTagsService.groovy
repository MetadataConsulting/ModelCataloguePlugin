package org.modelcatalogue

import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.builder.api.ModelCatalogueTypes

class InitPoliciesAndTagsService {

    CatalogueBuilder catalogueBuilder

    void initPoliciesAndTags() {
        log.info "start:initPoliciesAndTags"

        catalogueBuilder.build {
            dataModelPolicy(name: 'Unique of Kind', overwrite: true) {
                check dataClass property 'name' is 'unique'
                check dataElement property 'name' is 'unique'
                check dataType property 'name' is 'unique'
                check validationRule property 'name' is 'unique'
                check measurementUnit property 'name' is 'unique'
                check ModelCatalogueTypes.CATALOGUE_ELEMENT property 'modelCatalogueId' is 'unique'
            }
            dataModelPolicy(name: 'Default Checks') {
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#authors' is 'required' otherwise 'Metadata "Authors" is missing for {2}'
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#reviewers' is 'required' otherwise 'Metadata "Reviewers" is missing for {2}'
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#owner' is 'required' otherwise 'Metadata "Owner" is missing for {2}'
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#reviewed' is 'required' otherwise 'Metadata "Reviewed" is missing for {2}'
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#approved' is 'required' otherwise 'Metadata "Approved" is missing for {2}'
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#namespace' is 'required' otherwise 'Metadata "Namespace" is missing for {2}'
                check dataModel extension 'http://www.modelcatalogue.org/metadata/#organization' is 'required' otherwise 'Metadata "Organization" is missing for {2}'

                check dataElement property 'dataType' is 'required' otherwise 'Data type is missing for {2}'
                check dataElement property 'name' is 'unique' otherwise 'Data element\'s name is not unique for {2}'
                check dataType property 'name' is 'unique' otherwise 'Data type\'s name is not unique for {2}'
                check dataType property 'name' apply regex: /[^_ -]+/ otherwise 'Name of {2} contains illegal characters ("_", "-" or " ")'
            }
            dataModel(name: 'Clinical Tags') {
                tag(name: 'Highly Sensitive PI data')
                tag(name: 'Sensitive PI data')
                tag(name: 'Highly Sensitive data')
                tag(name: 'Sensitive data')
                tag(name: 'Internal data')
                tag(name: 'External data')
            }
            log.info "complete:initPoliciesAndTags"
        }
    }
}
