package biz.lobachev.annette.data_dictionary.test.finance

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Audit.audit
import biz.lobachev.annette.data_dictionary.labels.Go.{goStructPackage, goEnumPackage, goTablePackage}

object Finance
    extends Enums
    with BC
    with CA
    with FI_CC
    with FI_GLS
    with FI_GLA
    with FI_LE
    with FI_BP
    with FI_ANL
    with LO {

  val data = domain(
    "Finance",
    "Finance model",
    "Finance model provides tables and data structures similar to SAP ERP Finance.",
  )
    .withLabels(
      goTablePackage("github.com/valerylobachev/finance/logic/repository/entity"),
      goEnumPackage("github.com/valerylobachev/finance/logic/model"),
      goStructPackage("github.com/valerylobachev/finance/logic/data_model"),
      audit("audit"),
    )
    .withEnums(enums*)
    .withComponents(
      bcComponent,
      caComponent,
      component("FI", "Finance")
        .withComponents(
          companyCodeComponent,
          glAccountComponent,
          glSettingsComponent,
          ledgerEntryComponent,
          businessPartnersGroup,
          analyticsComponent,
        ),
      logisticComponent,
    )

}
