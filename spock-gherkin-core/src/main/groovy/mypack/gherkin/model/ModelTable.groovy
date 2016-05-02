package mypack.gherkin.model

import gherkin.formatter.model.DataTableRow
import groovy.transform.Canonical
import groovy.transform.ToString

@Canonical
@ToString(includeNames = true, includePackage = false)
class ModelTable {
    // TODO: consider alternate data representation
    List<DataTableRow> dataTableRows

    static ModelTable from(List<DataTableRow> dataTableRows) {
        if (dataTableRows) {
            return new ModelTable(dataTableRows)
        }
        return null
    }
}
