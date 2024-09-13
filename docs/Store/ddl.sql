-- Store example
-- Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder).
-- Date: 2024-09-13 11:22:52



CREATE TABLE "client"."client_clients_table" (
  "id" serial PRIMARY KEY NOT NULL,
  "name" varchar(100) NOT NULL,
  "description" text,
  "billing_address_line1" varchar(100) NOT NULL,
  "billing_address_line2" varchar(100),
  "billing_city" varchar(50) NOT NULL,
  "billing_state" varchar(50) NOT NULL,
  "billing_country" varchar(50) NOT NULL,
  "billing_postcode" varchar(10) NOT NULL,
  "postal_address_line1" varchar(100) NOT NULL,
  "postal_address_line2" varchar(100),
  "postal_city" varchar(50) NOT NULL,
  "postal_state" varchar(50) NOT NULL,
  "postal_country" varchar(50) NOT NULL,
  "postal_postcode" varchar(10) NOT NULL,
  "promotion_id" varchar(10),
  "segment_id" varchar(10),
  "business_area_id" varchar(10),
  "created_by" varchar(10) NOT NULL,
  "created_at" timestamptz NOT NULL,
  "updated_by" varchar(10) NOT NULL,
  "updated_at" timestamptz NOT NULL
);

CREATE TABLE "client"."client_client_addresses_table" (
  "id" serial PRIMARY KEY NOT NULL,
  "name" varchar(100) NOT NULL,
  "description" text,
  "client_id" integer NOT NULL,
  "address_line1" varchar(100) NOT NULL,
  "address_line2" varchar(100),
  "city" varchar(50) NOT NULL,
  "state" varchar(50) NOT NULL,
  "country" varchar(50) NOT NULL,
  "postcode" varchar(10) NOT NULL,
  "created_by" varchar(10) NOT NULL,
  "created_at" timestamptz NOT NULL,
  "updated_by" varchar(10) NOT NULL,
  "updated_at" timestamptz NOT NULL
);

CREATE TABLE "order_orders_table" (
  "id" serial PRIMARY KEY NOT NULL,
  "name" varchar(100) NOT NULL,
  "description" text,
  "client_id" integer NOT NULL,
  "delivery_address_id" integer NOT NULL,
  "status" order_status NOT NULL,
  "promotion_id" varchar(10),
  "segment_id" varchar(10),
  "business_area_id" varchar(10),
  "created_by" varchar(10) NOT NULL,
  "created_at" timestamptz NOT NULL,
  "updated_by" varchar(10) NOT NULL,
  "updated_at" timestamptz NOT NULL
);

CREATE TABLE "order_order_lines_table" (
  "id" serial PRIMARY KEY NOT NULL,
  "order_id" integer NOT NULL,
  "item_id" integer NOT NULL,
  "promotion_id" varchar(10),
  "segment_id" varchar(10),
  "business_area_id" varchar(10),
  "price" decimal(10,2) NOT NULL,
  "quantity" decimal(10,2) NOT NULL,
  "created_by" varchar(10) NOT NULL,
  "created_at" timestamptz NOT NULL,
  "updated_by" varchar(10) NOT NULL,
  "updated_at" timestamptz NOT NULL
);

CREATE TABLE "order_items_table" (
  "id" serial PRIMARY KEY NOT NULL,
  "item_id" integer NOT NULL,
  "price" decimal(10,2) NOT NULL,
  "created_by" varchar(10) NOT NULL,
  "created_at" timestamptz NOT NULL,
  "updated_by" varchar(10) NOT NULL,
  "updated_at" timestamptz NOT NULL
);

CREATE TABLE "analytics"."analytics_segments_table" (
  "id" varchar(10) PRIMARY KEY NOT NULL,
  "name" varchar(100) NOT NULL,
  "created_by" varchar(10) NOT NULL,
  "created_at" timestamptz NOT NULL,
  "updated_by" varchar(10) NOT NULL,
  "updated_at" timestamptz NOT NULL
);

CREATE TABLE "analytics"."analytics_business_areas_table" (
  "id" varchar(10) PRIMARY KEY NOT NULL,
  "name" varchar(100) NOT NULL,
  "created_by" varchar(10) NOT NULL,
  "created_at" timestamptz NOT NULL,
  "updated_by" varchar(10) NOT NULL,
  "updated_at" timestamptz NOT NULL
);

CREATE TABLE "analytics"."analytics_promotions_table" (
  "id" varchar(10) PRIMARY KEY NOT NULL,
  "name" varchar(100) NOT NULL,
  "created_by" varchar(10) NOT NULL,
  "created_at" timestamptz NOT NULL,
  "updated_by" varchar(10) NOT NULL,
  "updated_at" timestamptz NOT NULL
);


CREATE UNIQUE INDEX "client_client_addresses_table_client_id_id" ON "client"."client_client_addresses_table" ("client_id", "id");


COMMENT ON TABLE "client"."client_clients_table" IS 'Client';

COMMENT ON COLUMN "client"."client_clients_table"."id" IS 'Client Id';

COMMENT ON COLUMN "client"."client_clients_table"."name" IS 'Client name';

COMMENT ON COLUMN "client"."client_clients_table"."description" IS 'Description';

COMMENT ON COLUMN "client"."client_clients_table"."billing_address_line1" IS 'First address line';

COMMENT ON COLUMN "client"."client_clients_table"."billing_address_line2" IS 'Second address line';

COMMENT ON COLUMN "client"."client_clients_table"."billing_city" IS 'City';

COMMENT ON COLUMN "client"."client_clients_table"."billing_state" IS 'State';

COMMENT ON COLUMN "client"."client_clients_table"."billing_country" IS 'Country';

COMMENT ON COLUMN "client"."client_clients_table"."billing_postcode" IS 'Post code';

COMMENT ON COLUMN "client"."client_clients_table"."postal_address_line1" IS 'First address line';

COMMENT ON COLUMN "client"."client_clients_table"."postal_address_line2" IS 'Second address line';

COMMENT ON COLUMN "client"."client_clients_table"."postal_city" IS 'City';

COMMENT ON COLUMN "client"."client_clients_table"."postal_state" IS 'State';

COMMENT ON COLUMN "client"."client_clients_table"."postal_country" IS 'Country';

COMMENT ON COLUMN "client"."client_clients_table"."postal_postcode" IS 'Post code';

COMMENT ON COLUMN "client"."client_clients_table"."promotion_id" IS 'Promotion Id';

COMMENT ON COLUMN "client"."client_clients_table"."segment_id" IS 'Segment Id';

COMMENT ON COLUMN "client"."client_clients_table"."business_area_id" IS 'Business Area Id';

COMMENT ON COLUMN "client"."client_clients_table"."created_by" IS 'User created record';

COMMENT ON COLUMN "client"."client_clients_table"."created_at" IS 'Timestamp of record create';

COMMENT ON COLUMN "client"."client_clients_table"."updated_by" IS 'User updated record';

COMMENT ON COLUMN "client"."client_clients_table"."updated_at" IS 'Timestamp of record update';

COMMENT ON TABLE "client"."client_client_addresses_table" IS 'Client address';

COMMENT ON COLUMN "client"."client_client_addresses_table"."id" IS 'Address Id';

COMMENT ON COLUMN "client"."client_client_addresses_table"."name" IS 'Address name';

COMMENT ON COLUMN "client"."client_client_addresses_table"."description" IS 'Description';

COMMENT ON COLUMN "client"."client_client_addresses_table"."client_id" IS 'Client Id';

COMMENT ON COLUMN "client"."client_client_addresses_table"."address_line1" IS 'First address line';

COMMENT ON COLUMN "client"."client_client_addresses_table"."address_line2" IS 'Second address line';

COMMENT ON COLUMN "client"."client_client_addresses_table"."city" IS 'City';

COMMENT ON COLUMN "client"."client_client_addresses_table"."state" IS 'State';

COMMENT ON COLUMN "client"."client_client_addresses_table"."country" IS 'Country';

COMMENT ON COLUMN "client"."client_client_addresses_table"."postcode" IS 'Post code';

COMMENT ON COLUMN "client"."client_client_addresses_table"."created_by" IS 'User created record';

COMMENT ON COLUMN "client"."client_client_addresses_table"."created_at" IS 'Timestamp of record create';

COMMENT ON COLUMN "client"."client_client_addresses_table"."updated_by" IS 'User updated record';

COMMENT ON COLUMN "client"."client_client_addresses_table"."updated_at" IS 'Timestamp of record update';

COMMENT ON TABLE "order_orders_table" IS 'Order';

COMMENT ON COLUMN "order_orders_table"."id" IS 'Order Id';

COMMENT ON COLUMN "order_orders_table"."name" IS 'Order name';

COMMENT ON COLUMN "order_orders_table"."description" IS 'Description';

COMMENT ON COLUMN "order_orders_table"."client_id" IS 'Client Id';

COMMENT ON COLUMN "order_orders_table"."delivery_address_id" IS 'Delivery address';

COMMENT ON COLUMN "order_orders_table"."status" IS 'Order status';

COMMENT ON COLUMN "order_orders_table"."promotion_id" IS 'Promotion Id';

COMMENT ON COLUMN "order_orders_table"."segment_id" IS 'Segment Id';

COMMENT ON COLUMN "order_orders_table"."business_area_id" IS 'Business Area Id';

COMMENT ON COLUMN "order_orders_table"."created_by" IS 'User created record';

COMMENT ON COLUMN "order_orders_table"."created_at" IS 'Timestamp of record create';

COMMENT ON COLUMN "order_orders_table"."updated_by" IS 'User updated record';

COMMENT ON COLUMN "order_orders_table"."updated_at" IS 'Timestamp of record update';

COMMENT ON TABLE "order_order_lines_table" IS 'Order line';

COMMENT ON COLUMN "order_order_lines_table"."id" IS 'OrderLine Id';

COMMENT ON COLUMN "order_order_lines_table"."order_id" IS 'Order Id';

COMMENT ON COLUMN "order_order_lines_table"."item_id" IS 'Item Id';

COMMENT ON COLUMN "order_order_lines_table"."promotion_id" IS 'Promotion Id';

COMMENT ON COLUMN "order_order_lines_table"."segment_id" IS 'Segment Id';

COMMENT ON COLUMN "order_order_lines_table"."business_area_id" IS 'Business Area Id';

COMMENT ON COLUMN "order_order_lines_table"."price" IS 'Amount';

COMMENT ON COLUMN "order_order_lines_table"."quantity" IS 'Quantity';

COMMENT ON COLUMN "order_order_lines_table"."created_by" IS 'User created record';

COMMENT ON COLUMN "order_order_lines_table"."created_at" IS 'Timestamp of record create';

COMMENT ON COLUMN "order_order_lines_table"."updated_by" IS 'User updated record';

COMMENT ON COLUMN "order_order_lines_table"."updated_at" IS 'Timestamp of record update';

COMMENT ON TABLE "order_items_table" IS 'Item';

COMMENT ON COLUMN "order_items_table"."id" IS 'Item Id';

COMMENT ON COLUMN "order_items_table"."item_id" IS 'Item Id';

COMMENT ON COLUMN "order_items_table"."price" IS 'Amount';

COMMENT ON COLUMN "order_items_table"."created_by" IS 'User created record';

COMMENT ON COLUMN "order_items_table"."created_at" IS 'Timestamp of record create';

COMMENT ON COLUMN "order_items_table"."updated_by" IS 'User updated record';

COMMENT ON COLUMN "order_items_table"."updated_at" IS 'Timestamp of record update';

COMMENT ON TABLE "analytics"."analytics_segments_table" IS 'Segment';

COMMENT ON COLUMN "analytics"."analytics_segments_table"."id" IS 'Segment Id';

COMMENT ON COLUMN "analytics"."analytics_segments_table"."name" IS 'Name';

COMMENT ON COLUMN "analytics"."analytics_segments_table"."created_by" IS 'User created record';

COMMENT ON COLUMN "analytics"."analytics_segments_table"."created_at" IS 'Timestamp of record create';

COMMENT ON COLUMN "analytics"."analytics_segments_table"."updated_by" IS 'User updated record';

COMMENT ON COLUMN "analytics"."analytics_segments_table"."updated_at" IS 'Timestamp of record update';

COMMENT ON TABLE "analytics"."analytics_business_areas_table" IS 'Business area';

COMMENT ON COLUMN "analytics"."analytics_business_areas_table"."id" IS 'Business Area Id';

COMMENT ON COLUMN "analytics"."analytics_business_areas_table"."name" IS 'Name';

COMMENT ON COLUMN "analytics"."analytics_business_areas_table"."created_by" IS 'User created record';

COMMENT ON COLUMN "analytics"."analytics_business_areas_table"."created_at" IS 'Timestamp of record create';

COMMENT ON COLUMN "analytics"."analytics_business_areas_table"."updated_by" IS 'User updated record';

COMMENT ON COLUMN "analytics"."analytics_business_areas_table"."updated_at" IS 'Timestamp of record update';

COMMENT ON TABLE "analytics"."analytics_promotions_table" IS 'Promotion';

COMMENT ON COLUMN "analytics"."analytics_promotions_table"."id" IS 'Promotion Id';

COMMENT ON COLUMN "analytics"."analytics_promotions_table"."name" IS 'Name';

COMMENT ON COLUMN "analytics"."analytics_promotions_table"."created_by" IS 'User created record';

COMMENT ON COLUMN "analytics"."analytics_promotions_table"."created_at" IS 'Timestamp of record create';

COMMENT ON COLUMN "analytics"."analytics_promotions_table"."updated_by" IS 'User updated record';

COMMENT ON COLUMN "analytics"."analytics_promotions_table"."updated_at" IS 'Timestamp of record update';



ALTER TABLE "client"."client_clients_table" ADD CONSTRAINT "client_clients_table_promotion_id" FOREIGN KEY ("promotion_id") REFERENCES "analytics"."analytics_promotions_table" ("id");

ALTER TABLE "client"."client_clients_table" ADD CONSTRAINT "client_clients_table_segment_id" FOREIGN KEY ("segment_id") REFERENCES "analytics"."analytics_segments_table" ("id");

ALTER TABLE "client"."client_clients_table" ADD CONSTRAINT "client_clients_table_business_area_id" FOREIGN KEY ("business_area_id") REFERENCES "analytics"."analytics_business_areas_table" ("id");

ALTER TABLE "client"."client_client_addresses_table" ADD CONSTRAINT "client_client_addresses_table_client_id" FOREIGN KEY ("client_id") REFERENCES "client"."client_clients_table" ("id");

ALTER TABLE "order_orders_table" ADD CONSTRAINT "order_orders_table_client_id" FOREIGN KEY ("client_id") REFERENCES "client"."client_clients_table" ("id");

ALTER TABLE "order_orders_table" ADD CONSTRAINT "order_orders_table_delivery_address_id" FOREIGN KEY ("client_id", "delivery_address_id") REFERENCES "client"."client_client_addresses_table" ("client_id", "id");

ALTER TABLE "order_orders_table" ADD CONSTRAINT "order_orders_table_promotion_id" FOREIGN KEY ("promotion_id") REFERENCES "analytics"."analytics_promotions_table" ("id");

ALTER TABLE "order_orders_table" ADD CONSTRAINT "order_orders_table_segment_id" FOREIGN KEY ("segment_id") REFERENCES "analytics"."analytics_segments_table" ("id");

ALTER TABLE "order_orders_table" ADD CONSTRAINT "order_orders_table_business_area_id" FOREIGN KEY ("business_area_id") REFERENCES "analytics"."analytics_business_areas_table" ("id");

ALTER TABLE "order_order_lines_table" ADD CONSTRAINT "order_order_lines_table_order_id" FOREIGN KEY ("order_id") REFERENCES "order_orders_table" ("id");

ALTER TABLE "order_order_lines_table" ADD CONSTRAINT "order_order_lines_table_item_id" FOREIGN KEY ("item_id") REFERENCES "order_items_table" ("id");

ALTER TABLE "order_order_lines_table" ADD CONSTRAINT "order_order_lines_table_promotion_id" FOREIGN KEY ("promotion_id") REFERENCES "analytics"."analytics_promotions_table" ("id");

ALTER TABLE "order_order_lines_table" ADD CONSTRAINT "order_order_lines_table_segment_id" FOREIGN KEY ("segment_id") REFERENCES "analytics"."analytics_segments_table" ("id");

ALTER TABLE "order_order_lines_table" ADD CONSTRAINT "order_order_lines_table_business_area_id" FOREIGN KEY ("business_area_id") REFERENCES "analytics"."analytics_business_areas_table" ("id");


