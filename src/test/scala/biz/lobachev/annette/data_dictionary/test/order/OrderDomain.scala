package biz.lobachev.annette.data_dictionary.test.order

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Go.{goEnumPackage, goStructPackage, goTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Java.javaTablePackage

object OrderDomain {

  val data = domain(
    "Order",
    "Order model",
    "Order model provides tables and structures for simple order management system.",
  )
    .withLabels(
      goTablePackage("github.com/valerylobachev/order/entity"),
      goEnumPackage("github.com/valerylobachev/order/data_model"),
      goStructPackage("github.com/valerylobachev/order/data_model"),
      javaTablePackage("orders.entities"),
    )
    .withEnums(
      nativeEnum("OrderStatus", "Order status", 10)
        .withValues(
          "DRAFT"    -> "Draft"    -> "Draft",
          "APPROVED" -> "Approved" -> "Approved",
          "POSTED"   -> "Posted"   -> "Posted",
        ),
    )
    .withDataElements(
      "MaterialId" :! "materialId" :# varchar(20) :@ "Material Id",
      "CustomerId" :! "customerId" :# varchar(20) :@ "Customer Id",
      "OrderId" :! "orderId" :# varchar(20) :@ "Order Id",
    )
    .withTables(
      table("Material", "Material")
        .withPK(
          "id" :# "MaterialId",
        )
        .withFields(
          "name" :# varchar(200) :@ "Material name" :@@ "Material name used in invoices",
          "description" :# string :@ "Material description" :@@ "Detailed description of material in markdown format",
        )
        .withIndexes(
          index("name_search", "Index to find material by name", "name"),
        ),
      table("Customer", "Customer")
        .withPK(
          "id" :# "CustomerId",
        )
        .withFields(
          "name" :# varchar(200) :@ "Customer name" :@@ "Customer name used in invoices",
          "description" :# string :@ "Customer description" :@@ "Detailed description of customer in markdown format",
        )
        .withIndexes(
          index("name_search", "Index to find customer by name", "name"),
        ),
      table("Order", "Order")
        .withPK(
          "id" :# "OrderId",
        )
        .withFields(
          "orderDate" :# date :@ "Date of order" :== "CURRENT_DATE",
          "postingDate" :#? date :@ "Date of order posting",
          "status" :# enumeration("OrderStatus") :@ "Order status" := "DRAFT",
          dataElementTableField("CustomerId"),
          "description" :# string :@ "Order description" :@@ "Detailed description of order in markdown format",
        )
        .withIndexes(
          index("status", "Index to filter orders by status", "status"),
          index("filter", "Index to filter order by orderDate, customer & status", "orderDate", "customerId", "status"),
          index("customer", "Index to filter orders by customer", "customerId"),
        )
        .withRelations(
          manyToOne("customerId", "Reference to customer", "Customer", "customerId" -> "id")
            .withAssociation("customer", "orders"),
        ),
      table("OrderLine", "Order Line")
        .withPK(
          "id" :#++ integer :@ "Order Line Id",
        )
        .withFields(
          dataElementTableField("OrderId"),
          dataElementTableField("MaterialId"),
          "price" :# decimal(9, 2) :@ "Material price",
          "quantity" :# decimal(9, 0) :@ "Material quantity",
          "amount" :# decimal(9, 2) :@ "Amount" :& GenStoredAlwaysAs("price * quantity"),
        )
        .withIndexes(
          index("orderId", "Index to filter lines by order", "orderId"),
          index("materialId", "Index to filter lines by material id", "materialId"),
        )
        .withRelations(
          manyToOne("orderId", "Reference to order", "Order", "orderId" -> "id")
            .withAssociation("order", "orderLines"),
          manyToOne("materialId", "Reference to material", "Material", "materialId" -> "id")
            .withAssociation("material", "orderLines"),
        ),
    )
    .withStructs(
      struct("MaterialDto", "Material DTO")
        .likeTable("Material"),
      struct("CustomerDto", "Customer DTO")
        .likeTable("Customer"),
      struct("OrderLineDto", "Order Line DTO")
        .likeTable("OrderLine")
        .withFields(
          "material" :##? linkedStruct("MaterialDto") :@ "Material master data",
        ),
      struct("OrderDto", "Order DTO")
        .likeTable("Order")
        .withFields(
          "totalAmount" :## decimal(9, 2) :@ "Total amount of order",
          "customer" :##? linkedStruct("CustomerDto") :@ "Customer master data",
          "lines" :## linkedStructArray("OrderLineDto") :@ "Order lines",
        ),
    )
}
