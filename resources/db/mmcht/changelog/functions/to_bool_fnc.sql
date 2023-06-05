--liquibase formatted sql
--changeset unknown:to_bool_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function to_bool_fnc
  ( p_string varchar2
  ) return varchar2
is
begin
  return
    case upper(p_string)
      when 'Y' then 'true'
      when 'N' then 'false'
      when 'YES' then 'true'
      when 'NO' then 'false'
      when '1' then 'true'
      when '0' then 'false'
      else 'null'
      end;
end;
#

--changeset unknown:perm_to_bool_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON to_bool_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON to_bool_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.to_bool_fnc FOR to_bool_fnc
#
