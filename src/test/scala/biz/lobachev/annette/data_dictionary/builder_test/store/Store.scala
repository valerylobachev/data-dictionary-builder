package biz.lobachev.annette.data_dictionary.builder_test.store

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.labels.GolangPackage.goModelPackage
import biz.lobachev.annette.data_dictionary.builder.labels.TablePrefixSuffix.{tableNamePrefix, tableNameSuffix}
import biz.lobachev.annette.data_dictionary.builder.model._

object Store {
  val data = domain("Store", "Store example", "Store data model example")
    .withLabels(
      tableNameSuffix("table"),
      goModelPackage("github.com/valerylobachev/store/logic/repository/entity"),
    )
    .withEnums(
      enumDef("OrderStatus", "Order Status", 1)
        .withValues(
          "P" -> "Placed",
          "D" -> "Delivered",
          "C" -> "Canceled",
        ),
    )
    .withDataElements(
      // format: off
        dataElement("UserId",       "userId",       StringVarchar(10),       "User Id"),
        dataElement("ClientId",     "clientId",     IntInt(),                "Client Id"),
        dataElement("AddressId",    "addressId",    IntInt(),                "Address Id"),
        dataElement("OrderId",      "orderId",      IntInt(),                "Order Id"),
        dataElement("OrderLineId",  "orderLineId",  IntInt(),                "OrderLine Id"),
        dataElement("ItemId",       "itemId",       IntInt(),                "Item Id"),
        dataElement("ItemCode",     "itemCode",     StringVarchar(20),       "Item Code"),
        dataElement("Amount",       "amount",       BigDecimalNumeric(10,2), "Amount"),
        dataElement("Quantity",     "quantity",     BigDecimalNumeric(10,2), "Quantity"),
        dataElement("Postcode",     "postcode",     StringVarchar(10),       "Post code"),
        dataElement("Name",         "name",         StringVarchar(100),      "Name"),
        dataElement("Description",  "description",  StringText(),            "Description"),
        dataElement("PromotionId",        "promotionId",    StringVarchar(10), "Promotion Id"),
        dataElement("SegmentId",          "segmentId",      StringVarchar(10), "Segment Id"),
        dataElement("BusinessAreaId",     "businessAreaId", StringVarchar(10), "Business Area Id"),
        // format: on
    )
    .withComponents(
      component("Shared", "Shared data structures")
        .withEntities(
          embeddedEntity("Modification", "Modification data structure")
            .withFields(
              // format: off
              "createdBy" :# "UserId"            :@ "User created record",
              "createdAt"       :# InstantTimestamp()  :@ "Timestamp of record create",
              "updatedBy"       :# "UserId"            :@ "User updated record",
              "updatedAt"       :# InstantTimestamp()  :@ "Timestamp of record update"
              // format: on
            ),
          embeddedEntity("Address", "Address data structure")
            .withFields(
              // format: off
              "addressLine1" :#  StringVarchar(100) :@ "First address line",
              "addressLine2"       :#? StringVarchar(100) :@ "Second address line",
              "city"               :#  StringVarchar(50)  :@ "City",
              "state"              :#  StringVarchar(50)  :@ "State",
              "country"            :#  StringVarchar(50)  :@ "Country",
              "postcode"           :# "Postcode" ,
              // format: on
            ),
          embeddedEntity("Analytics", "Analytics data structure")
            .withFields(
              // format: off
              "promotionId" :#? "PromotionId",
              "segmentId"         :#? "SegmentId",
              "businessAreaId"    :#? "BusinessAreaId",
              // format: on
            )
            .withRelations(
              manyToOne("promotionId", "Reference to Promotion", "Promotion", "promotionId" -> "id"),
              manyToOne("segmentId", "Reference to Segment", "Segment", "segmentId"         -> "id"),
              manyToOne(
                "businessAreaId",
                "Reference to BusinessArea",
                "BusinessArea",
                "businessAreaId"                                                                    -> "id",
              ),
            ),
        ),
      component("Client", "Client tables")
        .withSchema("client")
        .withLabels(
          tableNamePrefix("client"),
        )
        .withEntities(
          tableEntity("Client", "Client")
            .withPK(
              "id" :#++ "ClientId",
            )
            .withFields(
              // format: off
              "name" :# "Name" :@ "Client name",
              "description" :#? "Description",
              include("billing", "Address"),
              include("postal", "Address"),
              includeWithRelations("Analytics"),
              include("Modification")
              // format: on
            ),
          tableEntity("ClientAddress", "Client address")
            .withPK(
              "id" :#++ "AddressId",
            )
            .withFields(
              // format: off
              "name"  :# "Name"         :@ "Address name",
              "description" :#? "Description",
              "clientId"    :# "ClientId",
              include("Address"),
              include("Modification")
              // format: on
            )
            .withRelations(
              manyToOne("clientId", "Reference to client", "Client", "clientId" -> "id"),
            )
            .withIndexes(
              uniqueIndex("clientIdId", "Unique clientId & id", "clientId", "id"),
            ),
        ),
      component("Order", "Order tables")
        .withLabels(
          tableNamePrefix("order"),
        )
        .withEntities(
          tableEntity("Order", "Order")
            .withPK(
              "id" :#++ "OrderId",
            )
            .withFields(
              // format: off
              "name"        :#  "Name"                     :@ "Order name",
              "description"       :#? "Description",
              "clientId"          :#  "ClientId",
              "deliveryAddressId" :#  "AddressId"                :@ "Delivery address",
              "status"            :#  EnumString("OrderStatus")  :@ "Order status",
              includeWithRelations("Analytics"),
              include("Modification")
              // format: on
            )
            .withRelations(
              manyToOne("clientId", "Reference to client", "Client", "clientId" -> "id"),
              manyToOne(
                "deliveryAddressId",
                "Reference to address",
                "ClientAddress",
                "clientId"                                                              -> "clientId",
                "deliveryAddressId"                                                     -> "id",
              ),
            ),
          tableEntity("OrderLine", "Order line")
            .withPK(
              "id" :#++ "OrderLineId",
            )
            .withFields(
              // format: off
              "orderId" :# "OrderId",
              "itemId"        :# "ItemId",
              includeWithRelations("Analytics"),
              "price"         :# "Amount",
              "quantity"      :# "Quantity",
              include("Modification")
              // format: on
            )
            .withRelations(
              manyToOne("orderId", "Reference to order", "Order", "orderId" -> "id"),
              manyToOne("itemId", "Reference to item", "Item", "itemId"     -> "id"),
            ),
          tableEntity("Item", "Item")
            .withPK(
              "id" :#++ "ItemId",
            )
            .withFields(
              // format: off
              "itemId" :# "ItemId",
              "price"        :# "Amount",
              include("Modification")
              // format: on
            ),
        ),
      component("Analytics", "Analytic tables")
        .withSchema("analytics")
        .withLabels(
          tableNamePrefix("analytics"),
        )
        .withEntities(
          tableEntity("Segment", "Segment")
            .withPK(
              "id" :# "SegmentId",
            )
            .withFields(
              "name" :# "Name",
              include("Modification"),
            ),
          tableEntity("BusinessArea", "Business area")
            .withPK(
              "id" :# "BusinessAreaId",
            )
            .withFields(
              "name" :# "Name",
              include("Modification"),
            ),
          tableEntity("Promotion", "Promotion", "Promotion")
            .withPK(
              "id" :# "PromotionId",
            )
            .withFields(
              "name" :# "Name",
              include("Modification"),
            ),
        ),
    )
}
