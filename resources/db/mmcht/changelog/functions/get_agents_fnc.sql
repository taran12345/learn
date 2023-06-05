--liquibase formatted sql
--changeset unknown:get_agents_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_agents_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_agents is
SELECT SEF.SALEN_ID AS AGENT_DETAIL_AGENT_ID,
       SE.NAME      AS AGENT_DETAIL_AGENT_NAME
  from FPA.FPA_SALES_ENTITY_FMAS SEF
  INNER JOIN FPA.FPA_SALES_ENTITIES SE  ON SEF.SALEN_ID = SE.ID
  WHERE SEF.PERSONAL_ACCOUNT_NBR = to_char(P_PERSONAL_ACCOUNT_NBR);

type typ_agents is record (
                            AGENT_DETAIL_AGENT_ID       NUMBER(12),
                            AGENT_DETAIL_AGENT_NAME     VARCHAR2(240)
                              );
  type tbl_typ_agents is table of typ_agents index by PLS_INTEGER;
  v_tbl_typ_agents  tbl_typ_agents;
  local_obj varchar2(10000);
  tmp_json_array clob := '[';
  begin
        if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
           return '[]';
        end if;
        open cr_agents ;
        fetch cr_agents bulk collect into v_tbl_typ_agents;
        close cr_agents;
        if v_tbl_typ_agents.count > 0 then
            for i in 1..v_tbl_typ_agents.count
            loop
              local_obj := null;
              local_obj := '{
                              "AGENT_DETAIL_AGENT_ID":'||'"'||v_tbl_typ_agents(i).AGENT_DETAIL_AGENT_ID||'"'||','||'
                              "AGENT_DETAIL_AGENT_NAME": '||TO_STRING_FNC(v_tbl_typ_agents(i).AGENT_DETAIL_AGENT_NAME)||'},';
            tmp_json_array := tmp_json_array||local_obj;
            end loop;
            tmp_json_array := SUBSTR(tmp_json_array, 0, LENGTH(tmp_json_array) - 1);
            tmp_json_array := tmp_json_array||']';
            return tmp_json_array;
    end if;
    return '[]';
exception
when others then
 return P_Personal_Account_Nbr||sqlerrm;
end get_agents_fnc;
#

--changeset unknown:perm_get_agents_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_agents_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_agents_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_agents_fnc FOR get_agents_fnc
#
