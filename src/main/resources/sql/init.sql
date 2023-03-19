CREATE TABLE `allen_su`.`danmu_config_mango`
(
    `id`                INT NOT NULL AUTO_INCREMENT,
    `title`             VARCHAR(45) NULL COMMENT '标题',
    `program`           VARCHAR(45) NULL COMMENT '节目',
    `duration`          INT NULL COMMENT '时长',
    `url`               VARCHAR(200) NULL COMMENT '时间用{time}替换',
    `next_execute_time` DATETIME NULL DEFAULT now() COMMENT '下次执行时间',
    `update_time`       DATETIME NULL DEFAULT now() COMMENT '更新时间',
    `remark`            VARCHAR(200) NULL,
    PRIMARY KEY (`id`)
);

insert into `allen_su`.`danmu_config_mango` values
(1, '蘑菇屋1', '蘑菇屋', 70, 'https://bullet-ws.hitv.com/bullet/tx/2023/03/19/145454/15887959/{time}.json', now(), now(), ''),
(2, '和你唱3加更', '我想和你唱', 32, 'https://galaxy.bz.mgtv.com/cdn/opbarrage?version=3.0.0&vid=18495919&cid=528132&time={time}', now(), now(), 'new');

CREATE TABLE `allen_su`.`danmu_mango_task_trans`
(
    `id`          INT NOT NULL AUTO_INCREMENT,
    `title`       VARCHAR(45) NULL COMMENT '标题',
    `create_time` DATETIME NULL DEFAULT now() COMMENT '执行时间',
    `count`       INT NULL COMMENT '新增弹幕数量',
    `remark`      VARCHAR(200) NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `allen_su`.`danmu_mango_details`
(
    `id`          INT NOT NULL AUTO_INCREMENT,
    `ids`         VARCHAR(45) NULL COMMENT '弹幕id',
    `uid`         INT NULL COMMENT 'uid',
    `uuid`        VARCHAR(45) NULL COMMENT 'uuid',
    `int_time`    INT NULL COMMENT '时间',
    `content`     VARCHAR(200) NULL COMMENT '弹幕内容',
    `title`       VARCHAR(45) NULL COMMENT '标题',
    `create_time` DATETIME NULL DEFAULT now() COMMENT '插入时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `ids_UNIQUE` (`ids` ASC) VISIBLE,
    INDEX         `uuid_idx` (`uuid` ASC) VISIBLE
);
