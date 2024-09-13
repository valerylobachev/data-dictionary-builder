-- Simple Example
-- Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder).
-- Date: 2024-09-13 11:22:51



CREATE TABLE "persons" (
  "id" serial PRIMARY KEY NOT NULL,
  "firstname" varchar(40) NOT NULL,
  "lastname" varchar(40) NOT NULL,
  "search" tsvector NOT NULL,
  "attributes" jsonb NOT NULL,
  "external_ids" jsonb NOT NULL,
  "updated_by" varchar(20) NOT NULL,
  "updated_at" timestamptz NOT NULL
);

CREATE TABLE "groups" (
  "id" smallserial PRIMARY KEY NOT NULL,
  "name" varchar(100) NOT NULL,
  "updated_by" varchar(20) NOT NULL,
  "updated_at" timestamptz NOT NULL
);

CREATE TABLE "group_members" (
  "id" serial PRIMARY KEY NOT NULL,
  "group_id" smallint NOT NULL,
  "person_id" integer NOT NULL,
  "updated_by" varchar(20) NOT NULL,
  "updated_at" timestamptz NOT NULL
);


CREATE INDEX "persons_lastname_firstname" ON "persons" ("lastname", "firstname");

CREATE UNIQUE INDEX "group_members_person_group_ids" ON "group_members" ("group_id", "person_id");


COMMENT ON TABLE "persons" IS 'Person';

COMMENT ON COLUMN "persons"."id" IS 'Person Id';

COMMENT ON COLUMN "persons"."firstname" IS 'Person first name';

COMMENT ON COLUMN "persons"."lastname" IS 'Person last name';

COMMENT ON COLUMN "persons"."search" IS 'Search index';

COMMENT ON COLUMN "persons"."attributes" IS 'Person attributes';

COMMENT ON COLUMN "persons"."external_ids" IS 'Person external Ids';

COMMENT ON COLUMN "persons"."updated_by" IS 'User updated record';

COMMENT ON COLUMN "persons"."updated_at" IS 'Timestamp of record update';

COMMENT ON TABLE "groups" IS 'Group';

COMMENT ON COLUMN "groups"."id" IS 'Group Id';

COMMENT ON COLUMN "groups"."name" IS 'Group name';

COMMENT ON COLUMN "groups"."updated_by" IS 'User updated record';

COMMENT ON COLUMN "groups"."updated_at" IS 'Timestamp of record update';

COMMENT ON TABLE "group_members" IS 'Group member';

COMMENT ON COLUMN "group_members"."id" IS 'Group member id';

COMMENT ON COLUMN "group_members"."group_id" IS 'Group Id';

COMMENT ON COLUMN "group_members"."person_id" IS 'Person Id';

COMMENT ON COLUMN "group_members"."updated_by" IS 'User updated record';

COMMENT ON COLUMN "group_members"."updated_at" IS 'Timestamp of record update';



ALTER TABLE "group_members" ADD CONSTRAINT "group_members_group_id" FOREIGN KEY ("group_id") REFERENCES "groups" ("id");


