-- Simple Example
-- Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder).
-- Date: 2024-08-19 17:06:08


CREATE TABLE audit (
  "table" text NOT NULL,
  "id" text NOT NULL,
  "counter" bigserial NOT NULL,
  "dt" timestamptz NOT NULL,
  "operation" varchar(1) NOT NULL,
  "prev_data" jsonb,
  "new_data" jsonb,
  "updated_by" text,
  PRIMARY KEY ("table", "id", "counter")
);

CREATE OR REPLACE FUNCTION jsonb_diff_val(val1 JSONB, val2 JSONB)
    RETURNS JSONB AS
$$
DECLARE
    result JSONB;
    v      RECORD;
BEGIN
    result = val1;
    FOR v IN SELECT * FROM jsonb_each(val2)
        LOOP
            IF result @> jsonb_build_object(v.key, v.value)
            THEN
                result = result - v.key;
            ELSIF result ? v.key THEN
                CONTINUE;
            ELSE
                result = result || jsonb_build_object(v.key, 'null');
            END IF;
        END LOOP;
    RETURN result;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION persons_audit_func()
    RETURNS trigger AS
$$
BEGIN
    if (TG_OP = 'INSERT') then
        INSERT INTO audit ("table",
                            id,
                            dt,
                            operation,
                            prev_data,
                            new_data,
                            updated_by)
        VALUES (TG_TABLE_NAME,
                NEW."id"::text,
                CURRENT_TIMESTAMP,
                'I',
                null,
                to_jsonb(NEW),
                NEW."updated_by");

        RETURN NEW;
    elsif (TG_OP = 'UPDATE') then
        INSERT INTO audit ("table",
                            id,
                            dt,
                            operation,
                            prev_data,
                            new_data,
                            updated_by)
        VALUES (TG_TABLE_NAME,
                NEW."id"::text,
                CURRENT_TIMESTAMP,
                'U',
                jsonb_diff_val(to_jsonb(OLD), to_jsonb(NEW)),
                jsonb_diff_val(to_jsonb(NEW), to_jsonb(OLD)),
                NEW."updated_by");

        RETURN NEW;
    elsif (TG_OP = 'DELETE') then
        INSERT INTO audit ("table",
                            id,
                            dt,
                            operation,
                            prev_data,
                            new_data,
                            updated_by)
        VALUES (TG_TABLE_NAME,
                OLD."id"::text,
                CURRENT_TIMESTAMP,
                'D',
                to_jsonb(OLD),
                NULL,
                NULL);

        RETURN OLD;
    end if;

END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER persons_audit_trigger
    AFTER INSERT OR UPDATE OR DELETE
    ON "persons"
    FOR EACH ROW
EXECUTE FUNCTION persons_audit_func(); 

CREATE OR REPLACE FUNCTION groups_audit_func()
    RETURNS trigger AS
$$
BEGIN
    if (TG_OP = 'INSERT') then
        INSERT INTO audit ("table",
                            id,
                            dt,
                            operation,
                            prev_data,
                            new_data,
                            updated_by)
        VALUES (TG_TABLE_NAME,
                NEW."id"::text,
                CURRENT_TIMESTAMP,
                'I',
                null,
                to_jsonb(NEW),
                NEW."updated_by");

        RETURN NEW;
    elsif (TG_OP = 'UPDATE') then
        INSERT INTO audit ("table",
                            id,
                            dt,
                            operation,
                            prev_data,
                            new_data,
                            updated_by)
        VALUES (TG_TABLE_NAME,
                NEW."id"::text,
                CURRENT_TIMESTAMP,
                'U',
                jsonb_diff_val(to_jsonb(OLD), to_jsonb(NEW)),
                jsonb_diff_val(to_jsonb(NEW), to_jsonb(OLD)),
                NEW."updated_by");

        RETURN NEW;
    elsif (TG_OP = 'DELETE') then
        INSERT INTO audit ("table",
                            id,
                            dt,
                            operation,
                            prev_data,
                            new_data,
                            updated_by)
        VALUES (TG_TABLE_NAME,
                OLD."id"::text,
                CURRENT_TIMESTAMP,
                'D',
                to_jsonb(OLD),
                NULL,
                NULL);

        RETURN OLD;
    end if;

END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER groups_audit_trigger
    AFTER INSERT OR UPDATE OR DELETE
    ON "groups"
    FOR EACH ROW
EXECUTE FUNCTION groups_audit_func(); 

CREATE OR REPLACE FUNCTION group_members_audit_func()
    RETURNS trigger AS
$$
BEGIN
    if (TG_OP = 'INSERT') then
        INSERT INTO audit ("table",
                            id,
                            dt,
                            operation,
                            prev_data,
                            new_data,
                            updated_by)
        VALUES (TG_TABLE_NAME,
                NEW."id"::text,
                CURRENT_TIMESTAMP,
                'I',
                null,
                to_jsonb(NEW),
                NEW."updated_by");

        RETURN NEW;
    elsif (TG_OP = 'UPDATE') then
        INSERT INTO audit ("table",
                            id,
                            dt,
                            operation,
                            prev_data,
                            new_data,
                            updated_by)
        VALUES (TG_TABLE_NAME,
                NEW."id"::text,
                CURRENT_TIMESTAMP,
                'U',
                jsonb_diff_val(to_jsonb(OLD), to_jsonb(NEW)),
                jsonb_diff_val(to_jsonb(NEW), to_jsonb(OLD)),
                NEW."updated_by");

        RETURN NEW;
    elsif (TG_OP = 'DELETE') then
        INSERT INTO audit ("table",
                            id,
                            dt,
                            operation,
                            prev_data,
                            new_data,
                            updated_by)
        VALUES (TG_TABLE_NAME,
                OLD."id"::text,
                CURRENT_TIMESTAMP,
                'D',
                to_jsonb(OLD),
                NULL,
                NULL);

        RETURN OLD;
    end if;

END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER group_members_audit_trigger
    AFTER INSERT OR UPDATE OR DELETE
    ON "group_members"
    FOR EACH ROW
EXECUTE FUNCTION group_members_audit_func(); 
