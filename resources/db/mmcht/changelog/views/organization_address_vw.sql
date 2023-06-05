--liquibase formatted sql
--changeset unknown:organization_address_vw runOnChange:true dbms:oracle endDelimiter:# stripComments:false
-- Each row in this view represents a merchant's organizational address.
CREATE OR REPLACE VIEW organization_address_vw AS (
                   SELECT A.ADDRESS1          ,
                          A.ADDRESS2          ,
                          A.POSTAL_CODE       ,
                          PA.PARTY_ID         ,
                          GA1.NAME CITY_NAME  ,
                          GA2.NAME STATE_NAME ,
                          GA3.NAME COUNTRY_NAME,
                          GA3.CODE COUNTRY_CDE
                    FROM FPA.PARTIES P,    FPA.PARTY_ADDRESSES PA,
                         FPA.ADDRESSES A,  FPA.GEO_AREAS GA1 ,
                         FPA.GEO_AREAS GA2,FPA.GEO_AREAS GA3
                   WHERE P.PARTY_ID = PA.PARTY_ID
                     AND PA.THRU_DATE IS NULL
                     AND P.PART_TYPE IN ('ORGA','EXTEO','BANK','INTEO','PORT' )
                     AND PA.ADDRESS_ID = A.ADDRESS_ID
                     AND A.GEO_AREA_ID = GA1.GEO_AREA_ID
                     AND GA1.GEO_AREA_ID_WITHIN = GA2.GEO_AREA_ID
                     AND GA2.GEO_AREA_ID_WITHIN = GA3.GEO_AREA_ID)
#

--changeset unknown:organization_address_vw_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant SELECT ON organization_address_vw TO ${liquibase.schema}_rw
#

grant SELECT ON organization_address_vw TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.organization_address_vw FOR organization_address_vw
#
