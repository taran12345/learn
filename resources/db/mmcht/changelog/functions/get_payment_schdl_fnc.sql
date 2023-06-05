--liquibase formatted sql
--changeset unknown:get_payment_schdl_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_payment_schdl_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_payment_schdl is
select PYMT_SHDL.PERSONAL_ACCOUNT_NBR     AS PYMT_SHDL_PARENT_ACCOUNT,
      STAT_TYPE.DESCRIPTION               AS PYMT_SHDL_PARENT_STATUS,
      PYMT_SHDL.LAST_PAYMENT_DATE         AS PYMT_SHDL_LAST_PAYMENT_DATE,
      PYMT_SHDL.NEXT_PAYMENT_DATE         AS PYMT_SHDL_NEXT_PAYMENT_DATE,
      PYMT_SHDL.MINIMUM_PAYMENT           AS PYMT_SHDL_MIN_PAYMENT_AMOUNT,
      PYMT_SHDL.MAXIMUM_PAYMENT           AS PYMT_SHDL_MAX_PAYMENT_AMOUNT,
      PYMT_SHDL.MINIMUM_FLOAT             AS PYMT_SHDL_MIN_FLOAT_AMOUNT,
      PYMT_SHDL.DAYS_IN_ARREARS           AS PYMT_SHDL_DAYS_IN_ARREARS,
      decode(PYMT_SHDL.ARRDT_CODE, 'CAL', 'Calendar', 'BUS', 'Business', PYMT_SHDL.ARRDT_CODE)  AS PYMT_SHDL_ARREARS_DAY_TYPE,
      PYMT_SHDL.FMAPFT_CDE        AS PYMT_SHDL_FREQUENCY_TYPE,
      PYMT_SHDL.FMAPST_CDE       AS PYMT_SHDL_SCHEDULE_TYPE,
      to_boolean_fnc(PYMT_SHDL.PAYMENTS_ON_HOLD)       AS PYMT_SHDL_PAYMENTS_ON_HOLD,
      PYMT_SHDL.FPOHR_CODE        AS PYMT_SHDL_ON_HOLD_REASON_CDE,
      REASON.DESCRIPTION        AS PYMT_SHDL_ON_HOLD_REASON_DESC,
      to_boolean_fnc(REASON.ALLOW_PMT_IND)             AS PYMT_SHDL_ALLOW_PMT,
      to_boolean_fnc(REASON.ALLOW_DEBIT_IND)           AS PYMT_SHDL_ALLOW_DEBIT,
      to_boolean_fnc(REASON.ALLOW_MAN_IND)             AS PYMT_SHDL_ALLOW_ADD_PMT,
      to_boolean_fnc(PYMT_SHDL.CONSOLIDATED_TRANSFER_ONLY_IND) AS PYMT_SHDL_CONSOLIDATED_ONLY,
      to_boolean_fnc(PYMT_SHDL.CUSTOM_DESCRIPTOR_IND)          AS PYMT_SHDL_CUSTOM_DESCRIPTOR,
      PYMT_SHDL.CUSTOM_DESCRIPTOR_PREFIX  AS PYMT_SHDL_DESCRIPTOR_PREFIX,
      to_boolean_fnc(FIREPAY.NEXT_DAY_FUNDING_IND)             AS PYMT_SHDL_NEXT_DAY_FUNDING
FROM FPA.FMA_PAYMENT_SCHEDULE PYMT_SHDL
INNER JOIN FPA.FIREPAY_PERS_ACCNTS FIREPAY ON FIREPAY.PERSONAL_ACCOUNT_NBR = PYMT_SHDL.PERSONAL_ACCOUNT_NBR
INNER JOIN FPA.PERS_ACCNT_STATS STAT ON STAT.PERSONAL_ACCOUNT_NBR = FIREPAY.PERSONAL_ACCOUNT_NBR AND STAT.THRU_DATE IS NULL
INNER JOIN FPA.PERS_ACCNT_STAT_TYP STAT_TYPE ON STAT.PERSONAL_ACCOUNT_STAT_TYPE_CDE = STAT_TYPE.PERSONAL_ACCOUNT_STAT_TYPE_CDE
LEFT JOIN FPA.FMA_PMT_ON_HOLD_REASONS REASON ON REASON.CODE = PYMT_SHDL.FPOHR_CODE
WHERE PYMT_SHDL.PERSONAL_ACCOUNT_NBR = (
    select coalesce(CHILD.CONSOLIDATED_PAYMT_ACCT, CHILD.PERSONAL_ACCOUNT_NBR)
    from FPA.FMA_PAYMENT_SCHEDULE CHILD where CHILD.PERSONAL_ACCOUNT_NBR = to_char(p_personal_account_nbr)
);

type typ_payment_schdl is record (
                                  PYMT_SHDL_PARENT_ACCOUNT      VARCHAR2(249 CHAR),
                                  PYMT_SHDL_PARENT_STATUS       VARCHAR2(240),
                                  PYMT_SHDL_LAST_PAYMENT_DATE   VARCHAR2(100, CHAR),
                                  PYMT_SHDL_NEXT_PAYMENT_DATE   VARCHAR2(100, CHAR),
                                  PYMT_SHDL_MIN_PAYMENT_AMOUNT  NUMBER(12,2),
                                  PYMT_SHDL_MAX_PAYMENT_AMOUNT  NUMBER(12,2),
                                  PYMT_SHDL_MIN_FLOAT_AMOUNT    NUMBER(12,2),
                                  PYMT_SHDL_DAYS_IN_ARREARS     NUMBER(3),
                                  PYMT_SHDL_ARREARS_DAY_TYPE    VARCHAR2(20),
                                  PYMT_SHDL_FREQUENCY_TYPE      VARCHAR2(10 CHAR),
                                  PYMT_SHDL_SCHEDULE_TYPE       VARCHAR2(10 CHAR),
                                  PYMT_SHDL_PAYMENTS_ON_HOLD    VARCHAR2(5 CHAR),
                                  PYMT_SHDL_ON_HOLD_REASON_CDE  VARCHAR2(10),
                                  PYMT_SHDL_ON_HOLD_REASON_DESC VARCHAR2(240),
                                  PYMT_SHDL_ALLOW_PMT           VARCHAR2(5 CHAR),
                                  PYMT_SHDL_ALLOW_DEBIT         VARCHAR2(5 CHAR),
                                  PYMT_SHDL_ALLOW_ADD_PMT       VARCHAR2(5 CHAR),
                                  PYMT_SHDL_CONSOLIDATED_ONLY   VARCHAR2(5 CHAR),
                                  PYMT_SHDL_CUSTOM_DESCRIPTOR  VARCHAR2(5 CHAR),
                                  PYMT_SHDL_DESCRIPTOR_PREFIX  VARCHAR2(20 CHAR),
                                  PYMT_SHDL_NEXT_DAY_FUNDING    VARCHAR2(5 CHAR)
                                      );
  local_obj varchar2(10000);
  tmp_json_array clob ;

  typ_cr_payment_schdl  cr_payment_schdl%rowtype;
  begin
    if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
       return '{}';
    end if;
    open cr_payment_schdl ;
    fetch cr_payment_schdl into typ_cr_payment_schdl;
    if cr_payment_schdl%found then
       local_obj := null;
       local_obj := '{
              "PYMT_SHDL_PARENT_ACCOUNT":'||to_string_fnc(typ_cr_payment_schdl.PYMT_SHDL_PARENT_ACCOUNT)||',
              "PYMT_SHDL_PARENT_STATUS":'||to_string_fnc(typ_cr_payment_schdl.PYMT_SHDL_PARENT_STATUS)||',
              "PYMT_SHDL_LAST_PAYMENT_DATE":'||to_string_fnc(typ_cr_payment_schdl.PYMT_SHDL_LAST_PAYMENT_DATE)||',
              "PYMT_SHDL_NEXT_PAYMENT_DATE":'||to_string_fnc(typ_cr_payment_schdl.PYMT_SHDL_NEXT_PAYMENT_DATE)||',
              "PYMT_SHDL_MIN_PAYMENT_AMOUNT": '||to_number_fnc(typ_cr_payment_schdl.PYMT_SHDL_MIN_PAYMENT_AMOUNT)||','||'
              "PYMT_SHDL_MAX_PAYMENT_AMOUNT": '||to_number_fnc(typ_cr_payment_schdl.PYMT_SHDL_MAX_PAYMENT_AMOUNT)||','||'
              "PYMT_SHDL_MIN_FLOAT_AMOUNT":"'||typ_cr_payment_schdl.PYMT_SHDL_MIN_FLOAT_AMOUNT||'",
              "PYMT_SHDL_DAYS_IN_ARREARS":"'||typ_cr_payment_schdl.PYMT_SHDL_DAYS_IN_ARREARS||'",
              "PYMT_SHDL_ARREARS_DAY_TYPE":'||to_string_fnc(typ_cr_payment_schdl.PYMT_SHDL_ARREARS_DAY_TYPE)||',
              "PYMT_SHDL_FREQUENCY_TYPE":'||to_string_fnc(typ_cr_payment_schdl.PYMT_SHDL_FREQUENCY_TYPE)||',
              "PYMT_SHDL_SCHEDULE_TYPE":'||to_string_fnc(typ_cr_payment_schdl.PYMT_SHDL_SCHEDULE_TYPE)||',
              "PYMT_SHDL_PAYMENTS_ON_HOLD":"'||typ_cr_payment_schdl.PYMT_SHDL_PAYMENTS_ON_HOLD||'",
              "PYMT_SHDL_ON_HOLD_REASON_CDE":'||to_string_fnc(typ_cr_payment_schdl.PYMT_SHDL_ON_HOLD_REASON_CDE)||',
              "PYMT_SHDL_ON_HOLD_REASON_DESC":'||to_string_fnc(typ_cr_payment_schdl.PYMT_SHDL_ON_HOLD_REASON_DESC)||',
              "PYMT_SHDL_ALLOW_PMT":"'||typ_cr_payment_schdl.PYMT_SHDL_ALLOW_PMT||'",
              "PYMT_SHDL_ALLOW_DEBIT":"'||typ_cr_payment_schdl.PYMT_SHDL_ALLOW_DEBIT||'",
              "PYMT_SHDL_ALLOW_ADD_PMT":"'||typ_cr_payment_schdl.PYMT_SHDL_ALLOW_ADD_PMT||'",
              "PYMT_SHDL_CONSOLIDATED_ONLY":"'||typ_cr_payment_schdl.PYMT_SHDL_CONSOLIDATED_ONLY||'",
              "PYMT_SHDL_CUSTOM_DESCRIPTOR":"'||typ_cr_payment_schdl.PYMT_SHDL_CUSTOM_DESCRIPTOR||'",
              "PYMT_SHDL_DESCRIPTOR_PREFIX":'||to_string_fnc(typ_cr_payment_schdl.PYMT_SHDL_DESCRIPTOR_PREFIX)||',
              "PYMT_SHDL_NEXT_DAY_FUNDING":"'||typ_cr_payment_schdl.PYMT_SHDL_NEXT_DAY_FUNDING||'"
          }';

      close cr_payment_schdl;
      return local_obj;
    else
      return '{}';
    end if;
exception
when others then
 return P_Personal_Account_Nbr||sqlerrm;
end get_payment_schdl_fnc;
#

--changeset unknown:perm_get_payment_schdl_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_payment_schdl_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_payment_schdl_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_payment_schdl_fnc FOR get_payment_schdl_fnc
#
