--liquibase formatted sql
--changeset unknown:get_revenue_agree_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_revenue_agree_fnc(p_personal_account_nbr in varchar2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_revenue_agreement is
SELECT  aa.PERSONAL_ACCOUNT_NBR          AS RA_ACCOUNT_ID,
        aa.AGRE_ID                       AS RA_AGREEMENT_ID,
    	to_char(aa.EFFECTIVE_DATE, 'YYYY-MM-DD HH24:MI:SS') AS RA_EFFECTIVE_DATE,
       	a.DESCRIPTION                    AS RA_NAME,
       	a.AGRETY_CDE                     AS RA_TYPE_CODE,
       	(CASE a.PROCESSING_TYPE
                    WHEN 'CC' THEN 'CARD'
                    WHEN 'OB' THEN 'ALTP'
                    ELSE a.PROCESSING_TYPE
         END)                AS RA_PROCESSING_TYPE,
       	a.CURRENCY_TYPE_CDE              AS RA_CURRENCY,
       	TRANSF.PERSONAL_ACCOUNT_NBR_DEST AS RA_REVENUE_TRANSFER_ACCOUNT_NO
  	FROM FPA.ACCT_AGREEMENTS aa
    	INNER JOIN FPA.AGREEMENTS a
       		ON (a.AGRE_ID = aa.AGRE_ID AND a.THRU_DATE IS NULL)
       	LEFT JOIN FPA.FPA_AGREEMENT_FEE_TRANSF_ACCTS transf
        	ON transf.AGRE_ID = aa.AGRE_ID
 	WHERE aa.PERSONAL_ACCOUNT_NBR = to_char(p_personal_account_nbr);

type typ_revenue_agreement is record (
                                  RA_ACCOUNT_ID         VARCHAR2(30),
                                  RA_AGREEMENT_ID       number(12),
                                  RA_EFFECTIVE_DATE     VARCHAR2(100 CHAR),
                                  RA_NAME               VARCHAR2(100),
                                  RA_TYPE_CODE          VARCHAR2(30),
                                  RA_PROCESSING_TYPE    VARCHAR2(30),
                                  RA_CURRENCY           VARCHAR2(3),
                                  RA_REVENUE_TRANSFER_ACCOUNT_NO VARCHAR2(40)
                                 );
  local_obj varchar2(15000);
  tmp_json_array clob ;

  typ_cr_revenue_agreement  cr_revenue_agreement%rowtype;
  begin
    if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
       return '{}';
    end if;
    open cr_revenue_agreement ;
    fetch cr_revenue_agreement into typ_cr_revenue_agreement;
    if cr_revenue_agreement%found then
       local_obj := null;
       local_obj := '{
              "RA_ACCOUNT_ID":'||to_string_fnc(typ_cr_revenue_agreement.RA_ACCOUNT_ID)||',
              "RA_AGREEMENT_ID":'||to_string_fnc(typ_cr_revenue_agreement.RA_AGREEMENT_ID)||',
              "RA_EFFECTIVE_DATE":'||to_string_fnc(typ_cr_revenue_agreement.RA_EFFECTIVE_DATE)||',
              "RA_NAME":'||to_string_fnc(typ_cr_revenue_agreement.RA_NAME)||',
              "RA_TYPE_CODE": '||to_string_fnc(typ_cr_revenue_agreement.RA_TYPE_CODE)||',
              "RA_CURRENCY":"'||typ_cr_revenue_agreement.RA_CURRENCY||'",
              "RA_REVENUE_TRANSFER_ACCOUNT_NO": '||to_string_fnc(typ_cr_revenue_agreement.RA_REVENUE_TRANSFER_ACCOUNT_NO)||',
              "RA_PROCESSING_TYPE": '||to_string_fnc(typ_cr_revenue_agreement.RA_PROCESSING_TYPE)||',
              "RA_REVENUE_AGREEMENT_TERMS": '||GET_REV_AGREE_TERMS_FNC(typ_cr_revenue_agreement.RA_AGREEMENT_ID)||',
              "RA_REVENUE_AGREEMENT_FEES": '||GET_REV_AGREE_FEES_FNC(typ_cr_revenue_agreement.RA_AGREEMENT_ID)||'
          }';

      close cr_revenue_agreement;
      return local_obj;
    else
      return '{}';
    end if;
exception
when others then
 return P_Personal_Account_Nbr||sqlerrm;
end get_revenue_agree_fnc;
#

--changeset unknown:perm_get_revenue_agree_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_revenue_agree_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_revenue_agree_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_revenue_agree_fnc FOR get_revenue_agree_fnc
#
