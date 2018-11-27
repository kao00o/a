package com.kao00o.collection;

import java.util.List;

public class Linked<T> {

    private Node<T> head;
    private Node<T> end;
    private int size = 0;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Node<T> node = this.head.next;
        while (node !=this.end){
            builder.append(node.data).append(",");
            node = node.next;
        }
        return "Linked{" +
                "datas=[" + builder.toString() +"]"+

                ", size=" + size +
                '}';
    }

    public Linked(){
        this.head = new Node<>(null,null);
        this.end = new Node<>(null,null);
        this.head.next = this.end;
    }

    public int size(){
        return this.size;
    }

    private Node<T> getTrueEnd(){
        Node<T> trueEnd = this.head;
        while (trueEnd.next != this.end){
            trueEnd = trueEnd.next;
        }
        return trueEnd;
    }

    public void add(T data){
        Node<T> node = new Node<>(data, null);
        node.next = this.end;
        getTrueEnd().next = node;
        this.size++;
    }

    public T remove(int idx){
        if ( idx<0 || idx > size-1){
            throw new ArrayIndexOutOfBoundsException();
        }
        // rm 第一个
        Node<T> rmNode = null;

        if (idx == 0){
            rmNode = this.head.next;
            this.head.next = this.head.next.next;
        }else {
            Node<T> tNode = get(idx - 1);
            rmNode = get(idx);
            tNode.next = rmNode.next;
        }

//
//        if (size == 1){
//            this.head.next = this.head.next.next;
//        }else if (idx == 0){
//            this.head.next = this.head.next.next;
//        }else {
//            // zise > 2
//            Node<T> preRmNode = get(idx - 1);
//            preRmNode.next = rmNode.next;
//        }
        this.size--;
        return rmNode.data;
    }

    public Node<T> get(int idx){
        if (idx > size-1){
            throw new ArrayIndexOutOfBoundsException();
        }
        Node<T> node = this.head;
        for (int i =0; i<=idx; i++){
            node = node.next;
        }
        return node;
    }

    public void set(int idx, T data){
        if ( idx<0 || idx > size-1){
            throw new ArrayIndexOutOfBoundsException();
        }
        Node<T> tNode = this.get(idx);
        tNode.data = data;
    }

    /**
     * 翻转
     */
    public void revert(){

        Node<T> newNode = new Node<>(null,null);
        Node<T> node = newNode;

        int i = size -1;
        while (i >= 0){
            Node<T> inode = get(i);
            node.next = inode;
            node = inode;
            i--;
        }

        this.head = newNode;
        node.next = this.end;
    }


    private class Node<T>{
        private T data;
        private Node<T> next;

        public Node(T data, Node<T> next){
            this.data = data;
            this.next = next;
        }

        public T getData(){
            return data;
        }
        public void setData(T data){
            this.data = data;
        }

        public void setNext(Node<T> next){
            this.next = next;
        }

        public Node<T> getNext() {
            return next;
        }
    }


}

class Demo{
    public static void main(String[] args){
        Linked<Integer> linked = new Linked<>();
        linked.add(0);
        linked.add(1);
        linked.add(2);
        linked.add(3);
        linked.add(4);
        linked.add(5);
        System.out.println(linked);

//        System.out.println(linked.remove(3));
//        System.out.println(linked.remove(0));
//        System.out.println(linked.remove(0));
//        System.out.println(linked.remove(0));
//        System.out.println(linked.remove(0));
//        System.out.println(linked.remove(0));

        linked.revert();
        System.out.println(linked);
    }
}