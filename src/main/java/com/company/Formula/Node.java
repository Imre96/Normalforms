package com.company.Formula;


public class Node
{

    private Node left, right;
    private String symbol;

    public Node(String s)
    {
        left = null;
        right = null;
        symbol = s;
    }

    public Node(String s, Node r, Node l)
    {
        left = l;
        right = r;
        symbol = s;
    }


    public void setRight(Node r)
    {
        right = r;
    }

    public Node getRight()
    {
        return right;
    }

    public void setLeft(Node l)
    {
        left = l;
    }

    public Node getLeft()
    {
        return left;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String s)
    {
        symbol = s;
    }

    @Override
    public String toString(){
        if (left==null && right==null){
            return symbol;
        }else if (right==null){
            return symbol+" "+left.toString();

        }else {
            return "("+left.toString()+" "+symbol+" "+right.toString()+")";
        }
    }

}