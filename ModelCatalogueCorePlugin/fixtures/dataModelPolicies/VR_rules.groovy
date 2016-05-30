import org.modelcatalogue.core.DataModelPolicy

fixture{
    for (int i = 1 ; i <= 12 ; i++) {
        "DMP$i"(DataModelPolicy, name:"Policy $i", policyText: 'check dataType property "name" is unique')
    }
}
