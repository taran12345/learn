--liquibase formatted sql
--changeset unknown:escape_spl_char_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false
create or replace function escape_spl_char_fnc(p_text VARCHAR2) return VARCHAR2 as
begin
  return REPLACE(REPLACE(REPLACE((p_text), '\', '\\'), '"', '\"'), CHR(9), '\t');
exception
when others then
return null;
end escape_spl_char_fnc;
#

--changeset unknown:perm_escape_spl_char_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON escape_spl_char_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON escape_spl_char_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.escape_spl_char_fnc FOR escape_spl_char_fnc
#
