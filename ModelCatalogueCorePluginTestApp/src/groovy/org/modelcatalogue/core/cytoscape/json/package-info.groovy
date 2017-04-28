/**
 * Package for printing elements as JSON suitable for
 * displaying as a graph in Cytoscape.
 * Based on the xml package and uses some classes such as
 * PrintContext from there.
 * Note the JSON structure is flat: A list of nodes and list of edges, rather than
 * the XML tree structure which more or less directly reflects the class relationships
 * in a model.
 *
 * Still TODO: implement more ValidationRule and DataType printing
 * TODO: use RelationshipType.sourceToDestination as relationship name
 * @see org.modelcatalogue.core.xml
 */
package org.modelcatalogue.core.cytoscape.json
