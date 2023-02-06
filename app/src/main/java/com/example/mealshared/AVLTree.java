package com.example.mealshared;

import com.example.mealshared.Models.User;

public class AVLTree {
    User user;
    AVLTree left;
    AVLTree right;
    int height;

    public AVLTree(User user){
        this.user = user;
    }

    public AVLTree(){

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AVLTree getLeft() {
        return left;
    }

    public void setLeft(AVLTree left) {
        this.left = left;
    }

    public AVLTree getRight() {
        return right;
    }

    public void setRight(AVLTree right) {
        this.right = right;
    }

    public int findHeight() {
        return height;
    }

    public int height(AVLTree node){
        return node != null? node.findHeight() : -1;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public User findMax(){
        if (this.getUser() == null) throw new IllegalArgumentException("Tree is Empty");
        else if (this.right!=null) {
            return this.right.findMax();
        }
        return this.user;
    }

    public void updateHeight(AVLTree node){
        int leftChildHeight = height(node.left);
        int rightChildHeight = height(node.right);
        node.height = Math.max(leftChildHeight,rightChildHeight) + 1;
    }

    public int balanceFactor(AVLTree node){
        return height(node.right) - height(node.left);
    }

    public AVLTree rotateRight(AVLTree node) {
        AVLTree leftChild = node.left;

        node.left = leftChild.right;
        leftChild.right = node;

        updateHeight(node);
        updateHeight(leftChild);

        return leftChild;
    }

    public AVLTree rotateLeft(AVLTree node) {
        AVLTree rightChild = node.right;

        node.right = rightChild.left;
        rightChild.left = node;

        updateHeight(node);
        updateHeight(rightChild);

        return rightChild;
    }

    private AVLTree rebalance(AVLTree node) {
        int balanceFactor = balanceFactor(node);

        if (balanceFactor < -1) {
            if (balanceFactor(node.left) <= 0) {
                node = rotateRight(node);
            } else {
                node.left = rotateLeft(node.left);
                node = rotateRight(node);
            }
        }

        if (balanceFactor > 1) {
            if (balanceFactor(node.right) >= 0) {
                node = rotateLeft(node);
            } else {
                node.right = rotateRight(node.right);
                node = rotateLeft(node);
            }
        }

        return node;
    }



    public AVLTree insertNode(User user, AVLTree node) {
        if (node == null) {
            node = new AVLTree(user);
        }

        else if (user.getUid() < node.user.getUid()) {
            node.left = insertNode(user, node.left);
        } else if (user.getUid() > node.user.getUid()) {
            node.right = insertNode(user, node.right);
        } else {
            throw new IllegalArgumentException("AVL already contains a user with this uid.  " + user);
        }

        updateHeight(node);

        return rebalance(node);
    }


    public AVLTree deleteNode(User user, AVLTree node) {
        if (node == null) {
            return null;
        }

        if (user.getUid() < node.getUser().getUid()) {
            node.left = deleteNode(user, node.left);
        } else if (user.getUid() > node.getUser().getUid()) {
            node.right = deleteNode(user, node.right);
        }

        else if (node.left == null && node.right == null) {
            node = null;
        }

        else if (node.left == null) {
            node = node.right;
        } else if (node.right == null) {
            node = node.left;
        }

        else {
            deleteNodeWithTwoChildren(node);
        }
        return this;
    }


    private void deleteNodeWithTwoChildren(AVLTree node) {
        AVLTree inOrderSuccessor = findMinimum(node.right);

        node.user.setUid(inOrderSuccessor.user.getUid());

        node.right = deleteNode(inOrderSuccessor.getUser(), node.right);
    }

    public AVLTree findNode(int uid){
        if (uid == this.getUser().getUid()) {
          return this;
        }
        else if (uid < this.getUser().getUid()){
            return this.left.findNode(uid);
        }
        else if (uid > this.getUser().getUid()){
            return this.right.findNode(uid);
        }
        return this;
    }

    public User find(int uid){
        User tempUser=null;
        if (uid == this.getUser().getUid()) {
            tempUser = this.getUser();
            return tempUser;
        }
        else if (uid < this.getUser().getUid()){
             return this.left.find(uid);
        }
        else if (uid > this.getUser().getUid()){
             return this.right.find(uid);
        }
        return tempUser;
    }

    private AVLTree findMinimum(AVLTree node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    public String toString() {
        return "AVLTree{" +
                "user=" + user +
                ", left=" + left +
                ", right=" + right +
                ", height=" + height +
                '}';
    }
}