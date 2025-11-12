
DROP TABLE lawd_code;
CREATE TABLE IF NOT EXISTS lawd_code (
    lawd10           VARCHAR(10)     NOT NULL COMMENT '법정동코드(10자리) PK',
    -- lawd5는 lawd10을 이용해서 자동으로 계산되는 컬럼으로, GENERATED ALWAYS AS를 사용
    -- lawd10을 VARCHAR가 아닌 CHAR로 선언 시 SQL 실행 오류 발생
    lawd5            VARCHAR(5)      GENERATED ALWAYS AS (LEFT(lawd10, 5)) STORED COMMENT '법정동코드(5자리)',
    sido_cd          VARCHAR(2)               COMMENT '시도코드(코드 분해)',
    sgg_cd           VARCHAR(3)               COMMENT '시군구코드(코드 분해)',
    umd_cd           VARCHAR(3)               COMMENT '읍면동코드(코드 분해)',
    ri_cd            VARCHAR(2)               COMMENT '리코드(코드 분해)',
    locatjumin_cd    VARCHAR(10)              COMMENT '지역코드_주민',
    locatjijuk_cd    VARCHAR(10)              COMMENT '지역코드_지적',
    locatadd_nm      VARCHAR(50)              COMMENT '지역주소명(전체 경로형)',
    locat_order      VARCHAR(3)               COMMENT '서열',
    locat_rm         VARCHAR(300)             COMMENT '비고',
    locathigh_cd     VARCHAR(10)              COMMENT '상위지역코드',
    locallow_nm      VARCHAR(10)              COMMENT '상위지역코드',
    adpt_de          DATE                     COMMENT '생성일(YYYYMMDD)',
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (lawd10),
    KEY idx_lawd5 (lawd5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



-- 조회용 뷰(중복 제거 + “현존” 우선 규칙)
CREATE OR REPLACE VIEW v_lawd_code_sgg AS
SELECT t.*
FROM (
         SELECT
             lawd5,
             MIN(CASE WHEN (abolish_flag IS NULL OR abolish_flag NOT LIKE '%폐지%') THEN 0 ELSE 1 END) AS alive_rank,
             MIN(name) AS sgg_nm   -- 동일 lawd5 다수 이름이 있으면 사전식 최소값 선택(원하면 다른 규칙으로)
         FROM lawd_code
         GROUP BY lawd5
     ) x    -- ← 여기서 x가 서브쿼리의 alias (별칭)
         JOIN lawd_code t
              ON t.lawd5 = x.lawd5
                  AND ( (t.abolish_flag IS NULL OR t.abolish_flag NOT LIKE '%폐지%') = (x.alive_rank = 0) )
                  AND t.name = x.sgg_nm;