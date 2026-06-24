-- Department permission profile and department-member authorization seed.
-- Safe to run repeatedly.

DECLARE
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM USER_SEQUENCES WHERE SEQUENCE_NAME = 'SEQ_DEPARTMENT_PERMISSIONS';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'CREATE SEQUENCE SEQ_DEPARTMENT_PERMISSIONS START WITH 1 INCREMENT BY 1 NOCACHE';
    END IF;

    SELECT COUNT(*) INTO v_count FROM USER_SEQUENCES WHERE SEQUENCE_NAME = 'SEQ_USER_DEPT_AUTHS';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE 'CREATE SEQUENCE SEQ_USER_DEPT_AUTHS START WITH 1 INCREMENT BY 1 NOCACHE';
    END IF;

    SELECT COUNT(*) INTO v_count FROM USER_TABLES WHERE TABLE_NAME = 'DEPARTMENT_PERMISSIONS';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE TABLE DEPARTMENT_PERMISSIONS (
                DEPARTMENT_PERMISSION_ID NUMBER PRIMARY KEY,
                DEPARTMENT_NAME VARCHAR2(100) NOT NULL,
                PERMISSION_ID NUMBER NOT NULL,
                CREATED_AT TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
                CONSTRAINT UK_DEPT_PERM_DEPT_PID UNIQUE (DEPARTMENT_NAME, PERMISSION_ID)
            )';
    END IF;

    SELECT COUNT(*) INTO v_count FROM USER_TABLES WHERE TABLE_NAME = 'USER_DEPARTMENT_AUTHORIZATIONS';
    IF v_count = 0 THEN
        EXECUTE IMMEDIATE '
            CREATE TABLE USER_DEPARTMENT_AUTHORIZATIONS (
                USER_DEPARTMENT_AUTH_ID NUMBER PRIMARY KEY,
                USER_ID NUMBER NOT NULL,
                DEPARTMENT_NAME VARCHAR2(100) NOT NULL,
                AUTHORIZED_YN CHAR(1) DEFAULT ''Y'' NOT NULL,
                CREATED_AT TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
                UPDATED_AT TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
                CONSTRAINT UK_USER_DEPT_AUTH_USER UNIQUE (USER_ID)
            )';
    END IF;
END;
/

MERGE INTO USER_DEPARTMENT_AUTHORIZATIONS auth
USING (
    SELECT USER_ID, TRIM(DEPARTMENT_NAME) AS DEPARTMENT_NAME
    FROM USERS
    WHERE DEPARTMENT_NAME IS NOT NULL
) src
ON (auth.USER_ID = src.USER_ID)
WHEN MATCHED THEN
    UPDATE SET
        auth.DEPARTMENT_NAME = src.DEPARTMENT_NAME,
        auth.UPDATED_AT = SYSTIMESTAMP
WHEN NOT MATCHED THEN
    INSERT (
        USER_DEPARTMENT_AUTH_ID,
        USER_ID,
        DEPARTMENT_NAME,
        AUTHORIZED_YN,
        CREATED_AT,
        UPDATED_AT
    )
    VALUES (
        SEQ_USER_DEPT_AUTHS.NEXTVAL,
        src.USER_ID,
        src.DEPARTMENT_NAME,
        'Y',
        SYSTIMESTAMP,
        SYSTIMESTAMP
    );

COMMIT;

MERGE INTO DEPARTMENT_PERMISSIONS dp
USING (
    SELECT '구매팀' DEPARTMENT_NAME, p.PERMISSION_ID
      FROM PERMISSIONS p
     WHERE p.USE_YN = 'Y'
       AND p.PERMISSION_CODE IN (
           'dashboard.read',
           'products.read',
           'suppliers.read',
           'purchase-requests.read',
           'purchase-requests.write',
           'approvals.read',
           'approvals.process',
           'purchase-orders.read',
           'purchase-orders.write'
       )
    UNION ALL
    SELECT '물류운영팀', p.PERMISSION_ID
      FROM PERMISSIONS p
     WHERE p.USE_YN = 'Y'
       AND p.PERMISSION_CODE IN (
           'dashboard.read',
           'products.read',
           'suppliers.read',
           'warehouses.read',
           'warehouses.write',
           'receipts.read',
           'receipts.write',
           'inspections.read',
           'inspections.process',
           'stock.read',
           'stock-history.read'
       )
    UNION ALL
    SELECT '재고관리팀', p.PERMISSION_ID
      FROM PERMISSIONS p
     WHERE p.USE_YN = 'Y'
       AND p.PERMISSION_CODE IN (
           'dashboard.read',
           'warehouses.read',
           'stock.read',
           'stock.adjust',
           'stock-history.read',
           'receipts.read'
       )
    UNION ALL
    SELECT '영업팀', p.PERMISSION_ID
      FROM PERMISSIONS p
     WHERE p.USE_YN = 'Y'
       AND p.PERMISSION_CODE IN (
           'dashboard.read',
           'products.read',
           'suppliers.read',
           'purchase-requests.read',
           'purchase-requests.write'
       )
    UNION ALL
    SELECT '시스템관리팀', p.PERMISSION_ID
      FROM PERMISSIONS p
     WHERE p.USE_YN = 'Y'
       AND p.PERMISSION_CODE IN (
           'dashboard.read'
       )
) src
ON (dp.DEPARTMENT_NAME = src.DEPARTMENT_NAME AND dp.PERMISSION_ID = src.PERMISSION_ID)
WHEN NOT MATCHED THEN
    INSERT (
        DEPARTMENT_PERMISSION_ID,
        DEPARTMENT_NAME,
        PERMISSION_ID,
        CREATED_AT
    )
    VALUES (
        SEQ_DEPARTMENT_PERMISSIONS.NEXTVAL,
        src.DEPARTMENT_NAME,
        src.PERMISSION_ID,
        SYSTIMESTAMP
    );

COMMIT;
