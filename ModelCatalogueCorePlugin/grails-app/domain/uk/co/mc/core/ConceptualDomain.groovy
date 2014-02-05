package uk.co.mc.core

class ConceptualDomain extends CatalogueElement  {

	String name
	String description

	static auditable = true

	static hasMany = [valueDomains: ValueDomain]

	static constraints = {
		valueDomains nullable:true
		name blank: false
	}

	def prepareForDelete(){
		if(this.valueDomains.size()!=0){
			this.valueDomains.each{ p->
				p.prepareForDelete()
			}
		}
	}

	static mapping = {
		description type: 'text'
		valueDomains cascade: 'save-update'
	}
}
