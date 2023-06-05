--liquibase formatted sql
--changeset unknown:to_number_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function to_number_fnc(p_num in number)
return varchar2
is
begin
if p_num is null then
   return 'null';
else
   return p_num;
end if;
end;
#

--changeset unknown:perm_to_number_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON to_number_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON to_number_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.to_number_fnc FOR to_number_fnc
#
