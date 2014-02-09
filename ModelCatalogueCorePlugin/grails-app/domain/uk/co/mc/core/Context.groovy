package uk.co.mc.core

/**
 *
 * Context is a type of relationship
 * A conceptual domain provides a CONTEXT for a model
 * Example:
 * Library A and library B both have their own cataloguing systems for books
 * A model of a book is the same in both systems having data elements isbn, title, subject, author, date_published,
 * content.
 * However in system A, the data element subject is described by a value domain with an enumerated list: [POLITICS, SCIENCE, HISTORY]
 * In system B, the data element subject is described by a value domain with an enumerated list: [pol, sci, his]
 * A user wishing to describe/model a book can know, depending on the context i.e. the conceptual domain
 * which value domain to use for the subject
 *
 * To provide a context for a model the user creates a relationship of the context type
 *
 * If the user wants to find the different conceptual domains in which a model exists
 * they need to query for the context relationship type
 *
 * *  --------------------------------------------------------------------------------------------------------
 *  | Relationship Type |  Source              | Destination | Source->Destination    |  Destination<-Source |
 *  | ----------------- | ---------------------| ----------- | ---------------------- | -------------------- |
 *  |  [context]        |  ConceptualDomain    |  Model      | "provides context for" | "has context of"     |
 *  -----------------------------------------------------------------------------------------------------------
 *
 */
class Context extends RelationshipType{

    public final String name = "context"
    public final String sourceToDestination = "provides context for"
    public final String destinationToSource = "has context of"
    public final Class sourceClass = ConceptualDomain
    public final Class destinationClass = Model

    boolean validateSourceDestination(source, destination){
        if(!ConceptualDomain.isInstance(source)){ return false }
        if(!Model.isInstance(destination)){return false}
        return true
    }

}
