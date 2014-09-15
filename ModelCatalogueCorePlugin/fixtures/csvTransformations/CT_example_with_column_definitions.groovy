import org.modelcatalogue.core.dataarchitect.ColumnTransformationDefinition
import org.modelcatalogue.core.dataarchitect.CsvTransformation

fixture{
    CT_example(CsvTransformation, name: "Example")
    CTD_cars(ColumnTransformationDefinition, tranformation: CT_example, source: DE_opel, destination: DE_vauxhall)
    CTD_author(ColumnTransformationDefinition, tranformation: CT_example, source: DE_writer, destination: DE_author, header: "creator")
    CTD_degrees(ColumnTransformationDefinition, tranformation: CT_example, source: DE_patient_temperature_uk, destination: DE_patient_temperature_us)
}