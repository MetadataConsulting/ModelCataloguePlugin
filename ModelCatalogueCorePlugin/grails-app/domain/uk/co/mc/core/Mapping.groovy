package uk.co.mc.core

/**
 * Created by adammilward on 07/02/2014.
 *
 * Mappings exist between Enumerated DataTypes
 * i.e. ["His": "history", "Sci": "science", "Pol": "politics"]
 *
 */
class Mapping extends RelationshipType{

    public final String name = "mapping"
    public final String sourceToDestination = "maps to"
    public final String destinationToSource = "maps from"
    public final Class sourceClass = DataType
    public final Class destinationClass = DataType

    Map map

    //FIXME mapping

    static constraints = {
        map nullable:true
    }

    boolean validateSourceDestination(source, destination){
        if(!DataType.isInstance(source)){ return false }
        if(!DataType.isInstance(destination)){return false}
        return true
    }

}
