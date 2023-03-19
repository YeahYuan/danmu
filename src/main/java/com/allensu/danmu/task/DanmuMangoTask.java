package com.allensu.danmu.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.allensu.danmu.utils.HttpTools;
import com.allensu.danmu.entity.DanmuConfig;
import com.allensu.danmu.entity.DanmuEntity;
import com.allensu.danmu.entity.DanmuTaskTrans;
import com.allensu.danmu.mapper.DanmuConfigMapper;
import com.allensu.danmu.mapper.DanmuMapper;
import com.allensu.danmu.mapper.DanmuTaskTransMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class DanmuMangoTask {

    private final static int MIN_COUNT = 1000;
    private final static int MAX_COUNT = 2000;
    private final static int DEFAULT_DURATION_MINUTES = 10;

    @Autowired
    DanmuConfigMapper configMapper;
    @Autowired
    DanmuTaskTransMapper transMapper;
    @Autowired
    DanmuMapper danmuMapper;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void execute() {
        List<DanmuConfig> configList = configMapper.selectList(new LambdaQueryWrapper<DanmuConfig>().le(DanmuConfig::getNextExecuteTime, LocalDateTime.now()));
        log.info("***开始执行弹幕任务，获取到需要执行的弹幕配置{}条", configList.size());
        for (DanmuConfig config : configList) {
            executeForOneVideo(config);
        }
        log.info("***弹幕任务执行完成");
    }

    private void executeForOneVideo(DanmuConfig config) {
        log.info("******开始执行【{}】弹幕任务", config.getTitle());
        int count;
        if (config.getRemark().equalsIgnoreCase("new")) {
            count = getDanmuInsertCountForNewProgram(config);
        } else {
            count = getDanmuInsertCount(config);
        }
        log.info("---视频【{}】共新增{}条弹幕", config.getTitle(), count + "");
        saveDanmuTrans(config, count);
        LocalDateTime nextExecuteTime = parseExecutionTime(config, count);
        config.setNextExecuteTime(nextExecuteTime);
        config.setUpdateTime(LocalDateTime.now());
        saveConfig(config);
        log.info("******【{}】弹幕任务执行完成", config.getTitle());
    }

    private int getDanmuInsertCountForNewProgram(DanmuConfig config) {
        int count = 0;
        int time = 0;
        while (true) {
            log.info("-----开始获取【{}】节目第{}毫秒的弹幕", config.getTitle(), time+"");
            String url = config.getUrl().replace("{time}", time + "");
            String resStr = HttpTools.get(url);
            JSONObject resData = getResData(resStr, config);
            if (Objects.isNull(resData)) {
                continue;
            }
            time = resData.getInteger("next");
            List<DanmuEntity> danmuList = parseDanmuList(resData, config);
            if (Objects.isNull(danmuList)) {
                break;
            }
            log.info("-----此次请求共获取到{}条弹幕", danmuList.size());
            count = count + batchInsertDanmu(danmuList);
        }
        return count;
    }

    private int getDanmuInsertCount(DanmuConfig config) {
        int count = 0;
        for (int i = 0; i <= config.getDuration(); i++) {
            log.info("-----开始获取【{}】节目第{}分钟的弹幕", config.getTitle(), i+"");
            String url = config.getUrl().replace("{time}", i + "");
            String resStr = HttpTools.get(url);
            List<DanmuEntity> danmuList = parseDanmuList(resStr, config);
            log.info("-----此次请求共获取到{}条弹幕", danmuList.size());
            count = count + batchInsertDanmu(danmuList);
        }
        return count;
    }

    private LocalDateTime parseExecutionTime(DanmuConfig config, int count) {
        LocalDateTime now = LocalDateTime.now();
        long absMin = Duration.between(now, config.getUpdateTime()).abs().toMinutes();
        log.info("---上次执行间隔了{}分钟", absMin);
        absMin = absMin == 0L ? DEFAULT_DURATION_MINUTES : absMin;
        LocalDateTime nextExecuteTime;
        if (count <= MIN_COUNT) {
            nextExecuteTime = now.plusMinutes(2 * absMin);
            log.info("因此次更新的弹幕数量为{}, 少于设定的{}条, 下一次执行时间为{}分钟后的{}", count + "", MIN_COUNT + "", (2 * absMin) + "", nextExecuteTime);
        } else if (count >= MAX_COUNT) {
            nextExecuteTime = now.plusMinutes(absMin / 2);
            log.info("因此次更新的弹幕数量为{}, 多于设定的{}条, 下一次执行时间为{}分钟后的{}", count + "", MAX_COUNT + "", (absMin / 2) + "", nextExecuteTime);
        } else {
            nextExecuteTime = now.plusMinutes(absMin);
            log.info("因此次更新的弹幕数量为{}, 下一次执行时间为{}分钟后的{}", count + "", absMin + "", nextExecuteTime);
        }
        return nextExecuteTime;
    }

    private void saveConfig(DanmuConfig config) {
        try {
            configMapper.updateById(config);
        } catch (Exception e) {
            log.error("数据库更新失败", e);
        }
    }

    private void saveDanmuTrans(DanmuConfig config, int count) {
        DanmuTaskTrans trans = getDanmuTaskTrans(config, count);
        try {
            transMapper.insert(trans);
        } catch (Exception e) {
            log.error("数据库插入失败", e);
        }
    }

    private DanmuTaskTrans getDanmuTaskTrans(DanmuConfig config, int count) {
        DanmuTaskTrans trans = new DanmuTaskTrans();
        trans.setTitle(config.getTitle());
        trans.setCount(count);
        trans.setCreateTime(LocalDateTime.now());
        return trans;
    }

    private int batchInsertDanmu(List<DanmuEntity> danmuList) {
        int count = 0;
        for (DanmuEntity danmu : danmuList) {
            try {
                count = count + danmuMapper.insert(danmu);
            } catch (Exception e) {
                if (e instanceof DuplicateKeyException) {
                    continue;
                }
                log.error("数据库插入失败", e);
            }
        }
        log.info("-----此次请求共插入{}条弹幕", count + "");
        return count;
    }

    private JSONObject getResData(String resStr, DanmuConfig config) {
        JSONObject strJson;
        try {
            strJson = JSONObject.parseObject(resStr);
        } catch (Exception e) {
            log.error("json解析出错：", e);
            return null;
        }
        return strJson.getJSONObject("data");
    }

    private List<DanmuEntity> parseDanmuList(JSONObject data, DanmuConfig config) {
        JSONArray jsonArray = data.getJSONArray("items");
        if (Objects.isNull(jsonArray)) {
            return null;
        }
        List<DanmuEntity> danmuList = new ArrayList<>();
        for (Object o : jsonArray) {
            JSONObject obj = (JSONObject) o;
            DanmuEntity danmu = parseDanmu(obj, config);
            danmuList.add(danmu);
        }
        return danmuList;
    }

    private List<DanmuEntity> parseDanmuList(String resStr, DanmuConfig config) {
        List<DanmuEntity> danmuList = new ArrayList<>();
        JSONObject strJson;
        try {
            strJson = JSONObject.parseObject(resStr);
        } catch (Exception e) {
            log.error("json解析出错：", e);
            return danmuList;
        }
        for (Object o : strJson.getJSONObject("data").getJSONArray("items")) {
            JSONObject obj = (JSONObject) o;
            DanmuEntity danmu = parseDanmu(obj, config);
            danmuList.add(danmu);
        }
        return danmuList;
    }

    private DanmuEntity parseDanmu(JSONObject obj, DanmuConfig config) {
        DanmuEntity danmu = new DanmuEntity();
        danmu.setIds(obj.getString("ids"));
        danmu.setUid(obj.getInteger("uid"));
        danmu.setUuid(obj.getString("uuid"));
        danmu.setTime(obj.getInteger("time"));
        String content = obj.getString("content")
                .replaceAll("[\t\n\r]", "")
                .replaceAll("\\\\", "")
                .replaceAll("\"", "");
        danmu.setContent(content.length() > 200 ? content.substring(0, 200) : content);
        danmu.setTitle(config.getTitle());
        return danmu;
    }

}
