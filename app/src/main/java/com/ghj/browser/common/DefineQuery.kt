package com.ghj.browser.common

object DefineQuery {

    // 히스토리 테이블 생성
    val CREATE_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS HISTORY_TBL ( " +
                               "    VISIT_DATE  VARCHAR(14)  NOT NULL ,  " +
                               "    TITLE       VARCHAR(200)          ,  " +
                               "    URL         VARCHAR(100) NOT NULL ,  " +
                               "    FAVICON     VARCHAR(10000)            " +
                               ");"
    // 히스토리 테이블 삭제
    val DROP_HISTORY_TABLE = "DROP TABLE IF EXISTS HISTORY_TBL"

    // 히스토리 테이블에 데이터 입력
    val INSERT_HISTORY_URL = "INSERT INTO HISTORY_TBL( VISIT_DATE , TITLE , URL , FAVICON ) VALUES( ?, ?, ?, ? )"

    // 히스토리 테이블에 favicon 데이터 입력
    val UPDATE_HISTORY_FAVICON = "UPDATE HISTORY_TBL " +
                                 "SET    FAVICON = ? " +
                                 "WHERE  ROWID = (SELECT MAX(ROWID) FROM HISTORY_TBL)"

    // 히스토리 테이블 날짜(yyyyMMdd) 목록 조회
    val SELECT_HISTORY_DATE_GROUP = "SELECT    DATE " +
                                    "FROM      ( SELECT SUBSTR(VISIT_DATE, 1, 8) AS DATE" +
                                    "            FROM   HISTORY_TBL ) " +
                                    "GROUP  BY DATE " +
                                    "ORDER  By DATE DESC"

    // 히스토리 테이블 날짜(yyyyMMdd)별 방문사이트 목록 조회
    val SELECT_HISTORY_URL_BY_DATE = "SELECT   VISIT_DATE , " +
                                     "         TITLE      , " +
                                     "         URL        , " +
                                     "         FAVICON      " +
                                     "FROM     HISTORY_TBL  " +
                                     "WHERE    SUBSTR(VISIT_DATE, 1, 8)==? " +
                                     "ORDER BY VISIT_DATE DESC"

    // 해당 날짜의 방문사이트 개수 조회
    val SELECT_HISTORY_CNT_BY_DATE = "SELECT   COUNT(VISIT_DATE) as CNT " +
                                     "FROM     HISTORY_TBL              " +
                                     "WHERE    VISIT_DATE LIKE ?||'%'"

    // 히스토리 테이블의 모든데이터 삭제
    val DELETE_HISTORY_DATA_ALL = "DELETE FROM HISTORY_TBL"

    // 히스토리 테이블의 데이터 삭제
    val DELETE_HISTORY_DATA = "DELETE FROM HISTORY_TBL " +
                              "WHERE "

    // 히스토리 테이블의 데이터 검색
    val SELECT_HISTORY_SEARCH = "SELECT   VISIT_DATE , " +
                                "         TITLE      , " +
                                "         URL        , " +
                                "         FAVICON      " +
                                "FROM     HISTORY_TBL  " +
                                "WHERE    TITLE LIKE '%'||?||'%' OR URL LIKE '%'||?||'%' " +
                                "ORDER BY VISIT_DATE DESC"


    // 콘솔로그 테이블 생성
    val CREATE_CONSOLE_LOG_TABLE = "CREATE TABLE IF NOT EXISTS CONSOLE_LOG_TBL ( " +
                                   "      LOG_DATE VARCHAR(14)  NOT NULL , " +
                                   "      URL      VARCHAR(200) NOT NULL , " +
                                   "      LOG      VARCHAR(5000)           " +
                                   ");"

    // 콘솔로그 테이블 삭제
    val DROP_CONSOLE_LOG_TABLE = "DROP TABLE IF EXISTS CONSOLE_LOG_TBL"

    // 콘솔로그 데이터 입력
    val INSERT_CONSOLE_LOG = "INSERT INTO CONSOLE_LOG_TBL(LOG_DATE, URL, LOG) VALUES(?, ?, ?)"

    // 콘솔로그 데이터 조회
    val SELECT_CONSOLE_LOG = "SELECT   LOG_DATE , " +
                             "         URL ,      " +
                             "         LOG        " +
                             "FROM     CONSOLE_LOG_TBL " +
                             "ORDER BY LOG_DATE DESC"

    // 콘솔로그 전체 데이터 삭제
    val DELETE_CONSOLE_LOG_DATA_ALL = "DELETE FROM CONSOLE_LOG_TBL"

}