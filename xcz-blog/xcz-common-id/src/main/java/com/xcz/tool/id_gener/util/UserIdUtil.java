package com.xcz.tool.id_gener.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xcz.common.cache.extend.CacheConstants;
import com.xcz.common.cache.extend.RedissonClientEnum;
import com.xcz.common.cache.utils.RedissonUtil;
import com.xcz.tool.id_gener.repository.entity.IdGenerate;
import com.xcz.tool.id_gener.repository.entity.IdTag;
import com.xcz.tool.id_gener.repository.mapper.IdGenerateMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Slf4j
@Component
public class UserIdUtil {

    /**
     * 使用包级别的构造函数，禁止外部实例化
     */
    protected UserIdUtil() {
    }

    private static final String USER_ID_POOL = CacheConstants.USER_ID_POOL;
    private static final String USER_ID_LOCK = CacheConstants.USER_ID_LOCK;
    private final AtomicBoolean isAdd = new AtomicBoolean(false);
    private static final RedissonClient redisson = RedissonUtil.getRedissonClient(RedissonClientEnum.DATABASE_0);


    @Resource
    private IdGenerateMapper idGenerateMapper;

    @PostConstruct
    public void init() {
        RSet<Long> idPool = redisson.getSet(USER_ID_POOL);
        if (idPool.size() < 100) {
            addGenerateId();
        }
        log.info("玩家id池，剩余id数量：{}",idPool.size());
    }

    /**
     * 添加新id
     */
    public void addGenerateId() {
        RSet<Long> idPool = redisson.getSet(USER_ID_POOL);
        RLock lock = redisson.getLock(USER_ID_LOCK);
        boolean getLock = false;
        try {
            getLock = lock.tryLock(0, -1, TimeUnit.SECONDS);
            if (getLock) {
                //获取还未满id列表
                List<IdGenerate> idGenerates = idGenerateMapper.selectList(
                        new LambdaQueryWrapper<IdGenerate>()
                                .eq(IdGenerate::getTag, IdTag.USER)
                                .apply("(max_id <> end_id OR max_id IS NULL)") // 添加 maxId != endId 条件
                );
                //随机获取里面一条数据
                IdGenerate idGenerate = idGenerates.get(RandomGenerator.getDefault().nextInt(idGenerates.size()));
                Long initId = idGenerate.getMaxId() == null ? idGenerate.getInitId() : idGenerate.getMaxId();
                Long step = idGenerate.getStep();
                Long newMax = Math.min(initId + step, idGenerate.getEndId());
                idGenerate.setMaxId(newMax);
                step = newMax - initId;

                Set<Long> newIds = LongStream.range(initId, initId + step).boxed().collect(Collectors.toSet());
                idPool.addAll(newIds);
                idGenerateMapper.updateById(idGenerate);
                log.info("添加新ID，数量：{}, 范围：[{} - {}]", step, initId, newMax);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (getLock && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 获取一个id
     * @return  id
     */
    public Long getId(){
        RSet<Long> idPool = redisson.getSet(USER_ID_POOL);
        Long id = idPool.removeRandom();
        if(id == null){
            addGenerateId();
            id = idPool.removeRandom();
        }
        if(idPool.size() < 100){
            if(isAdd.compareAndSet(false,true)) {
                Thread.startVirtualThread(()->{
                    try {
                        addGenerateId();
                    } finally {
                        isAdd.set(false);
                    }
                });
            }
        }
        return id;
    }



}
