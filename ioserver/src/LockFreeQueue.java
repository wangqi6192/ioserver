/**
 * Copyright (c) 2008 IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;


/**
 * <p>
 * 高效并发队列
 * </p>
 * <br>
 * @author 胡玮@ritsky
 *
 * @param <E>
 */
public class LockFreeQueue<E> extends AbstractQueue<E> implements Queue<E> {
    private static final boolean IF_BACKOFF = false;
    private static final int WAIT_FOR_BACKOFF = 10;

    
    private volatile Node<E> head;             //头节点
    private volatile Node<E> tail;             //未节点

    private Node<E> dummy;


 ///////////////以下是用于原子更新的字段/////////////////////////////////
    @SuppressWarnings("unchecked")
    private static final AtomicReferenceFieldUpdater<LockFreeQueue, Node> TAIL_UPDATER = AtomicReferenceFieldUpdater
            .newUpdater(LockFreeQueue.class, Node.class, "tail");
    @SuppressWarnings("unchecked")
    private static final AtomicReferenceFieldUpdater<LockFreeQueue, Node> HEAD_UPDATER = AtomicReferenceFieldUpdater
            .newUpdater(LockFreeQueue.class, Node.class, "head");

    
    /**
     * <p>
     * 原子更新尾节点
     * </p>
     * <br>
     * @param cmp   预期值
     * @param val   更新值
     * @return
     */
    private boolean casTail(Node<E> cmp, Node<E> val) {
        return TAIL_UPDATER.compareAndSet(this, cmp, val);
    }


    /**
     * 原子更新头节点
     * @param cmp
     * @param val
     * @return
     */
    private boolean casHead(Node<E> cmp, Node<E> val) {
        return HEAD_UPDATER.compareAndSet(this, cmp, val);
    }

    
    /**节点*/
    private static class Node<E> {
        
        E value;

        
        Node<E> next, prev;

        
        public Node() {
            value = null;
            next = prev = null;
        }

        
        public Node(E val) {
            value = val;
            next = prev = null;
        }

        
        public Node(Node<E> next) {
            value = null;
            prev = null;
            this.next = next;
        }

       
        public Node<E> getNext() {
            return prev;
        }
    }

    
    public LockFreeQueue() {
        dummy = new Node<E>();
        head = dummy;
        tail = dummy;
    }

  

    public LockFreeQueue(Collection<? extends E> c) {
        addAll(c);
    }

   
    public boolean isEmpty() {
        return (head.value == null) && (tail.value == null);
        // or return first() == null;
    }

    
    private void fixList(Node<E> tail, Node<E> head) {
       
        Node<E> curNode = tail;
        while ((head == this.head) && (curNode != head)) {
            Node<E> curNodeNext = curNode.next;
            if (curNodeNext == null)
                break;
            Node<E> nextNodePrev = curNodeNext.prev;

            
            if (nextNodePrev != curNode) {
                curNodeNext.prev = curNode;
            }
            curNode = curNodeNext;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new QueueItr();
    }

   
    private class QueueItr implements Iterator<E> {
        
        private Node<E> nextNode;
       
        private E nextItem;

       
        QueueItr() {
            advance();
        }

        
        private E advance() {
           
            E x = nextItem;

          
            Node<E> p = (nextNode == null) ? first() : nextNode.getNext();
            while (true) {
              
                if (p == null) {
                    nextNode = null;
                    nextItem = null;
                    return x;
                }
                E item = p.value;
                if (item != null) {
                  
                    nextNode = p;
                    nextItem = item;
                    return x;
                } else
                 
                    p = p.getNext();
            }
        }

       
        public boolean hasNext() {
            return nextNode != null;
        }

       
        public E next() {
            if (nextNode == null)
                throw new NoSuchElementException();
            return advance();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    
    @Override
    public int size() {
       
        int count = 0;
        Node<E> cur;
        for (cur = first(); cur != null && cur.value != null; cur = cur.prev) {
            if (++count == Integer.MAX_VALUE)
                break;
        }
        return count;
    }

    
    private Node<E> first() {
        while (true) {
            Node<E> header = this.head;
            if (header.value != null)
                return header;

            Node<E> tail = this.tail;

            if (header == this.head) {
               
                if (tail == header) {
                    return null;
                } else {
                    Node<E> fstNodePrev = header.prev;
                    if (null == fstNodePrev) {
                        fixList(tail, header);
                        continue;
                    }
                    casHead(header, fstNodePrev);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean offer(E e) {
        if (e == null)
            throw new IllegalArgumentException();

        Node<E> node = new Node<E>(e);
        while (true) {
            Node<E> tail = this.tail;
            node.next = tail;
            if (casTail(tail, node)) {
                // Thread.yield();
                tail.prev = node;
                return true;
            }

            if (IF_BACKOFF) {
                // back off, wait for 10 milliseconds before retry.
                try {
                    Thread.sleep(WAIT_FOR_BACKOFF);
                } catch (Exception exp) {
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public E peek() {
        while (true) {
            Node<E> header = this.head;
            if (header.value != null)
                return header.value;

            Node<E> tail = this.tail;

            if (header == this.head) {
              
                if (tail == header) {
                    return null;
                } else {
                    Node<E> fstNodePrev = header.prev;
                    if (null == fstNodePrev) {
                        fixList(tail, header);
                        continue;
                    }
                    casHead(header, fstNodePrev);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public E poll() {
       
        Node<E> tail, head, fstNodePrev;
        E val;
        while (true) {
            head = this.head;
            tail = this.tail;
            fstNodePrev = head.prev;
            val = head.value;
            if (head == this.head) {
                if (val != null) { 
                    if (tail != head) { 
                        if (null != fstNodePrev) {
                            if (casHead(head, fstNodePrev)) {
                                fstNodePrev.next = null;
                                return val;
                            }
                        } else {
                            fixList(tail, head);
                            continue;
                        }
                    } else {
                        dummy.next = tail;
                        dummy.prev = null;
                        if (casTail(tail, dummy)) {
                            head.prev = dummy;
                        }
                        continue;
                    }
                } else {
                    if (tail == head) {
                        return null;
                    } else {
                        if (null != fstNodePrev) {
                            casHead(head, fstNodePrev);
                        } else
                            fixList(tail, head);
                    }
                }
            }

            if (IF_BACKOFF) {
                try {
                    Thread.sleep(WAIT_FOR_BACKOFF);
                } catch (Exception exp) {
                }
            }
        }
    }
}
