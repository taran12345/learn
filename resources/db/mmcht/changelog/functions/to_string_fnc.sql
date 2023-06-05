--liquibase formatted sql
--changeset unknown:to_string_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function to_string_fnc(p_text VARCHAR2) return VARCHAR2 as
begin
if p_text is null then
   return 'null';
else
   return '"'||REPLACE(REPLACE(REPLACE((p_text), '\', '\\'), '"', '\"'), CHR(9), '\t')||'"';
end if;
exception
when others then
return null;
end to_string_fnc;
#

--changeset unknown:perm_to_string_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON to_string_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON to_string_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.to_string_fnc FOR to_string_fnc
#
