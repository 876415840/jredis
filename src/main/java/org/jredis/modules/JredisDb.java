package org.jredis.modules;

import lombok.Data;

import java.util.Objects;

/**
 * @Description: 数据库(内存)
 * @Author MengQingHao
 * @Date 2020/5/7 6:17 下午
 */
public class JredisDb<K, V> {

    /**
     * 容器最大扩容长度
     */
    private static final int MAX_RESIZE_SIZE = 1 << 29;
    /**
     * 容器最大长度
     */
    private static final int MAX_ARRAY_SIZE = 1 << 30;
    /**
     * 容器初始长度
     */
    private static final int INIT_ARRAY_SIZE = 1 << 4;
    /**
     * 键值容器
     */
    private Node<K, V>[] nodes;
    /**
     * 容器已用数量计数
     */
    private int count;

    @Data
    public static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
        /**
         * 单位:毫秒
         */
        final long createTime;
        /**
         * 有效时间 单位:毫秒 (-1 为永久有效)
         */
        long expires;

        public Node(int hash, K key, V value, Node<K, V> next, long createTime, long expires) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
            this.createTime = createTime;
            this.expires = expires;
        }

    }

    /**
     * 扩容-数组转换计数
     * 每转换一个累加1，转换完成时赋值为-1
     */
    private int rehashIndex = -1;

    /**
     * 数组转换时的容器
     * 此容器目的是不影响性能情况下转换
     */
    private Node<K, V>[][] resizeNodes = new Node[2][];

    public JredisDb() {
        nodes = new Node[INIT_ARRAY_SIZE];
        resizeNodes[0] = nodes;
    }
    
    public V getValue(K key) {
        Node<K, V> node = getNode(key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    public Node<K, V> getNode(K key) {
        int hash = hash(key);
        if (rehashIndex > -1) {
            Node<K, V>[] oldNodes = resizeNodes[0];
            int i;
            Node<K, V> oldNode = oldNodes[i = (oldNodes.length - 1) & hash];
            if (oldNode != null) {
                return oldNode;
            }
        }
        Node<K, V> node = nodes[(nodes.length - 1) & hash];
        if (node == null) {
            return null;
        }

        // 找到对应node
        Node<K, V> temp = node;
        while (true) {
            if (temp.hash == hash && Objects.equals(temp.key, key)) {
                return temp;
            }
            if ((temp = temp.next) == null) {
                return null;
            }
        }
    }

    /**
     * 添加键值关系
     * 处理容器 扩容/缩容
     * @param key 键
     * @param value 值
     * @param expires 有效时间，小于0为永久有效
     * @return V
     * @author MengQingHao
     * @date 2020/5/8 5:33 下午
     */
    public V put(K key, V value, long expires) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        if (expires < -1) {
            expires = -1;
        }
        resize();

        return putVal(hash(key), key, value, expires);
    }

    /**
     * 添加键值关系
     * @param hash key的hash值
     * @param key 键
     * @param value 值
     * @param expires 有效时间，-1为永久有效
     * @return V
     * @author MengQingHao
     * @date 2020/5/8 5:34 下午
     */
    private final V putVal(int hash, K key, V value, long expires) {
        Node<K, V> oldNode = null;
        if (rehashIndex > -1) {
            Node<K, V>[] oldNodes = resizeNodes[0];
            int i;
            oldNode = oldNodes[i = (oldNodes.length - 1) & hash];
            if (oldNode != null) {
                oldNodes[i] = null;
            }
        }

        int i = (nodes.length - 1) & hash;
        Node<K, V> node = nodes[i];
        if (oldNode != null) {
            if (node == null) {
                node = oldNode;
            } else {
                Node<K, V> temp = node;
                while (true) {
                    if (temp.next == null) {
                        temp.next = oldNode;
                        break;
                    }
                    temp = temp.next;
                }
            }
        }
        if (node == null) {
            node = new Node<K, V>(hash, key, value, null, System.currentTimeMillis(), expires);
        } else {
            Node<K, V> temp = node;
            while (true) {
                if (temp.hash == hash && Objects.equals(temp.key, key)) {
                    temp.value = value;
                    return value;
                }
                if ((temp = temp.next) == null) {
                    break;
                }
            }
            node = new Node<K, V>(hash, key, value, node, System.currentTimeMillis(), expires);
        }
        nodes[i] = node;
        return value;
    }

    /**
     * 使用hashMap的hash算法
     * @param key
     * @return int
     * @author MengQingHao
     * @date 2020/5/8 5:27 下午
     */
    static final int hash(Object key) {
        int h;
        return (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 扩容处理
     * @return void
     * @author MengQingHao
     * @date 2020/5/8 5:16 下午
     */
    private void resize() {
        int loadFactor = count * 100 / nodes.length;
        // 负载因子大于等于100% 或 小于等于10% 时，转换容器
        if (loadFactor < 100 && loadFactor > 10) {
          return;
        }
        if (rehashIndex > -1) {
            return;
        }
        if (count > MAX_ARRAY_SIZE && nodes.length == MAX_ARRAY_SIZE) {
            return;
        }
        nodes = new Node[tableSizeFor()];
        resizeNodes[1] = nodes;
        rehashIndex = 0;
    }

    /**
     * 保证得到2的n次幂
     * @return int
     * @author MengQingHao
     * @date 2020/5/8 6:07 下午
     */
    private final int tableSizeFor() {
        int n = count - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        if (n < 0) {
            return 2;
        }
        if (++n > MAX_RESIZE_SIZE) {
            return MAX_ARRAY_SIZE;
        }
        return n * 2;
    }

}
