/**
 * Package for printing elements as JSON suitable for
 * displaying as a graph in Cytoscape.
 * Based on the xml package and uses some classes such as
 * PrintContext from there.
 * Note the JSON structure is flat: A list of nodes and list of edges, rather than
 * the XML tree structure which more or less directly reflects the class relationships
 * in a model.
 *
 * Works for sample data NHIC, and actual database NHIC Ovarian Cancer.
 * Issue: Stack overflows on large data sets. Is the problem the large PrintContext?
 * Issue: Sometimes an element is null, which causes a null pointer exception when accessing e.g. synonym relationships.
 * Issue: Trying to export HPO dataset goes to a strange URL which the URL mapping is not happy with.
 * TODO: Make Tail-Recursive to overcome Stack Overflows (e.g. on COSD). Probably best to make PrintContext into a stack.
 * TODO: implement more ValidationRule and DataType printing, probably by passing an extra parameter, a map of data, to super.printElement
 * TODO: use RelationshipType.sourceToDestination as relationship name
 * @see org.modelcatalogue.core.xml
 */
package org.modelcatalogue.core.cytoscape.json
