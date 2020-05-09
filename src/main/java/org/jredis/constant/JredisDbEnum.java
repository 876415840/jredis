package org.jredis.constant;

import org.jredis.modules.JredisDb;

/**
 * @Description: 枚举版单例
 * @Author MengQingHao
 * @Date 2020/5/9 1:36 下午
 */
public enum JredisDbEnum {
    ENTITY(new JredisDb<String, Object>())
    ;

    private JredisDb<String, Object> jredisDb;

    JredisDbEnum(JredisDb<String, Object> jredisDb) {
        this.jredisDb = jredisDb;
    }

    public JredisDb<String, Object> getJredisDb() {
        return jredisDb;
    }
}
