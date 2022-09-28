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

    // 히스토리 테이블의 URL 데이터
    val SELECT_HISTORY_URL = "SELECT   TITLE      , " +
                             "         URL        , " +
                             "         VISIT_DATE , " +
                             "         FAVICON      " +
                             "FROM     HISTORY_TBL  " +
                             "GROUP BY URL          " +
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


    // 웹킷로그 테이블 생성
    val CREATE_WEBKIT_LOG_TABLE = "CREATE TABLE IF NOT EXISTS WEBKIT_LOG_TBL ( " +
                                  "       INSERT_TIME VARCHAR(14)   NOT NULL , " +
                                  "       URL         VARCHAR(200)  NOT NULL , " +
                                  "       LOG_DATE    VARCHAR(14)   NOT NULL , " +
                                  "       _FUNCTION   VARCHAR(500)  , " +
                                  "       PARAMS      VARCHAR(5000) , " +
                                  "       DESCRIPTION VARCHAR(5000) " +
                                  ");"

    // 웹킷로그 테이블 삭제
    val DROP_WEBKIT_LOG_TABLE = "DROP TABLE IF EXISTS WEBKIT_LOG_TBL"

    // 웹킷로그 데이터 입력
    val INSERT_WEBKIT_LOG = "INSERT INTO WEBKIT_LOG_TBL(INSERT_TIME, URL, LOG_DATE, _FUNCTION, PARAMS, DESCRIPTION) VALUES(?, ?, ?, ?, ?, ?)"

    // 웹킷로그 그룹 조회
    val SELECT_WEBKIT_LOG_GROUP = "SELECT   INSERT_TIME ,    " +
                                  "         URL              " +
                                  "FROM     WEBKIT_LOG_TBL   " +
                                  "GROUP BY INSERT_TIME, URL " +
                                  "ORDER BY INSERT_TIME DESC "

    // 웹킷로그 데이터 조회
    val SELECT_WEBKIT_LOG = "SELECT   INSERT_TIME ,  " +
                            "         URL         ,  " +
                            "         LOG_DATE    ,  " +
                            "         _FUNCTION   ,  " +
                            "         PARAMS      ,  " +
                            "         DESCRIPTION    " +
                            "FROM     WEBKIT_LOG_TBL " +
                            "WHERE    INSERT_TIME=? AND URL=? " +
                            "ORDER BY LOG_DATE "

    // 웹킷로그 전체 데이터 삭제
    val DELETE_WEBKIT_LOG_DATA_ALL = "DELETE FROM WEBKIT_LOG_TBL"


    // 즐겨찾기 테이블 생성
    val CREATE_BOOKMARK_TABLE = "CREATE TABLE IF NOT EXISTS BOOKMARK_TBL ( " +
                                "       URL     VARCHAR(200)   PRIMARY KEY , " +
                                "       TITLE   VARCHAR(500)               , " +
                                "       FAVICON VARCHAR(10000)               " +
                                ");"

    // 즐겨찾기 테이블 삭제
    val DROP_BOOKMARK_TABLE = "DROP TABLE IF EXISTS BOOKMARK_TBL"

    // 즐겨찾기 데이터 입력
    val INSERT_BOOKMARK = "INSERT INTO BOOKMARK_TBL(URL, TITLE, FAVICON) VALUES(?, ?, ?)"

    // 즐겨찾기 데이터 삭제
    val DELETE_BOOKMARK = "DELETE FROM BOOKMARK_TBL " +
                          "WHERE       "

    // 즐겨찾기 데이터 조회
    val SELECT_BOOKMARK = "SELECT   URL     , " +
                          "         TITLE   , " +
                          "         FAVICON   " +
                          "FROM     BOOKMARK_TBL " +
                          "GROUP BY URL          " +
                          "ORDER BY ROWID DESC"

    // 해당URL의 즐겨찾기 여부
    val SELECT_BOOKMARK_CNT_BY_URL = "SELECT COUNT(URL) as CNT " +
                                     "FROM   BOOKMARK_TBL " +
                                     "WHERE  URL = ?"

    // 즐겨찾기 데이터 전체 삭제
    val DELETE_BOOKMARK_ALL = "DELETE FROM BOOKMARK_TBL"
}