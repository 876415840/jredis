package org.jredis.modules;

import java.util.Arrays;

/**
 * @Description: 实现Redis的SDS
 * @Author MengQingHao
 * @Date 2020/5/7 3:18 下午
 */
public class Jsds implements CharSequence{

    /**
     * 容器最大长度
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * 容器初始长度
     */
    private static final int INIT_ARRAY_SIZE = 2 << 4;

    /**
     * 字符容器
     */
    private char[] value;

    /**
     * 字符计数
     */
    private int count;

    public Jsds() {
        value = new char[INIT_ARRAY_SIZE];
    }

    /**
     * 指定容器大小
     * @param capacity 初始大小
     * @author MengQingHao
     * @date 2020/5/7 3:39 下午
     */
    public Jsds(int capacity) {
        value = new char[capacity];
    }

    /**
     * 指定初始字符内容
     * @param str 初始内容
     * @author MengQingHao
     * @date 2020/5/7 3:39 下午
     */
    public Jsds(String str) {
        if (str == null) {
            value = new char[INIT_ARRAY_SIZE];
            return;
        }
        value = new char[str.length() + INIT_ARRAY_SIZE];
        append(str);
    }

    /**
     * 追加字符内容
     * 从末尾追加
     * @param str 追加内容
     * @return org.jredis.modules.Jsds
     * @author MengQingHao
     * @date 2020/5/7 3:42 下午
     */
    public Jsds append(String str) {
        if (str == null) {
            return this;
        }
        int len = str.length();
        resize(len + count);
        str.getChars(0, len, value, count);
        count += len;
        return this;
    }

    /**
     * 重设容器大小
     * @param minCapacity 容量最小值
     * @return void
     * @author MengQingHao
     * @date 2020/5/7 4:06 下午
     */
    private void resize(int minCapacity) {
        if (minCapacity > MAX_ARRAY_SIZE) {
            throw new OutOfMemoryError();
        }
        if (minCapacity > value.length) {
            int newCapacity = value.length << 1;
            if (minCapacity < minCapacity) {
                newCapacity = minCapacity;
            } else if (minCapacity > MAX_ARRAY_SIZE) {
                newCapacity = MAX_ARRAY_SIZE;
            }
            value = Arrays.copyOf(value, newCapacity);
        }
    }


    @Override
    public int length() {
        return count;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= count) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return value[index];
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    /**
     * 截取字符串
     * @param start 开始下标
     * @return java.lang.String
     * @author MengQingHao
     * @date 2020/5/7 4:13 下午
     */
    public String substring(int start) {
        return substring(start, count);
    }

    /**
     * 截取字符串
     * @param start 开始下标
     * @param end 指定长度(针对原值)
     * @return java.lang.String
     * @author MengQingHao
     * @date 2020/5/7 4:13 下午
     */
    public String substring(int start, int end) {
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end > count) {
            throw new StringIndexOutOfBoundsException(end);
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException(end - start);
        }
        return new String(value, start, end - start);
    }

    @Override
    public String toString() {
        return new String(value, 0, count);
    }
}
