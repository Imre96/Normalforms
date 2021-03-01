package com.company.Formula;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Arrays;

public class Formula {
    Node root;

    /**
     * Sima Constructor
     * @param root Fa szerkezet beli elem
     */
    public Formula(Node root) {
        this.root = root;
    }

    /**
     * Létrehozza a formula fa szerkezetét formula alapján
     * @param expr A formula pl:(A && B) > (B && C)
     * @return A formula faszerkezet alakban
     */
    public static Formula fromExpression(String expr){
        Stack<Node> values = new Stack<Node>();
        Stack<String> operators = new Stack<String>();
        //System.out.println(cleanExpressionForSplitting(expr));
        String[] tokens = cleanExpressionForSplitting(expr).split("\\s+");

        for (int i =0; i<tokens.length;i++){
            //System.out.println(tokens[i]);
             if(tokens[i].equals("(")){
                 operators.push(tokens[i]);
             } else if (tokens[i].equals(")")){
                 while (!operators.peek().equals("(")) {
                     if(operators.peek().equals("\u00AC")) {
                         values.push(new Node(operators.pop(), null, values.pop()));
                        //operators.pop();
                     }else {
                         values.push(new Node(operators.pop(), values.pop(), values.pop()));
                     }
                 }
                 operators.pop();
             }else if (tokens[i].equals("\u00AC") || //negáció
                     tokens[i].equals("\u2227") || //konjunkció
                     tokens[i].equals("\u2228") || //diszjunkció
                     tokens[i].equals("\u2283") ){ //Implikáció
                 //System.out.println("Belép ops");
                 while (!operators.empty() && hasPrecedence(tokens[i],operators.peek())) {
                     System.out.println(tokens[i]+" "+operators.peek());
                     if (operators.peek().equals("\u00AC")) {
                         values.push(new Node(operators.pop(), null, values.pop()));
                     } else {
                         values.push(new Node(operators.pop(), values.pop(), values.pop()));
                     }
                 }
                 operators.push(tokens[i]);
             }else {
                 //System.out.println("Belép values");
                 values.push(new Node(tokens[i]));
             }

            // Top of 'values' contains
            // result, return it
        }
        while (!operators.empty()){
            if(operators.peek().equals("\u00AC")) {
                values.push(new Node(operators.pop(), null, values.pop()));
                //System.out.println(values.peek().toString());
            }else {
                values.push(new Node(operators.pop(), values.pop(), values.pop()));
                //System.out.println(values.peek().toString());
            }
        }
        return  new Formula(values.pop());

    }

    /**
     * Segédfüggvény két máveletjek küzöl melyiknek van eglsőbfége
     * @param first
     * @param second
     * @return true ha az másofiknak,hamis ha az elősnek,
     */
    private static boolean hasPrecedence(String first, String second){
        //negáció > konjunkció/diszjunkció > implikáció

        return prefecence(first.charAt(0))<=prefecence(second.charAt(0));
    }

    /**
     * Segédfügvény segédfügvénye, A műveleti jelekhez rendel elsőbség szerint értéket.
     * @param op
     * @return
     */
    private static int prefecence(char op){
        switch (op){
            case '\u00AC':
                return 3;
            case '\u2283':
                return 1;
            case '(':
                return 0;
            default:
                return 2;

        }
    }

    /**
     * A bemenetben kapott esetleges formázási hibákat próbálja javítani, illetve az egyszerűség kedvéért lecseréli az alternatív műveleti jeleket
     * @param expr
     * @return
     */
    private static String cleanExpressionForSplitting(String expr){
        return expr.replaceAll("\\("," ( ")
                .replaceAll("\\)"," ) ")
                .replaceAll("\\|\\|","\u2228")
                .replaceAll("&&"," \u2227 ")
                .replaceAll(">","\u2283")
                .replaceAll("!","\u00AC")
                .replaceAll("\u00AC"," \u00AC ")
                .replaceAll("\u2227"," \u2227 ")
                .replaceAll("\u2228"," \u2228 ")
                .replaceAll("\u2283"," \u2283 ")
                .replaceAll(" +"," ")
                .trim();

    }

    public String toString(){
        return root.toString();
    }

    /**
     * Szöveggé alakító algotitmus, feltételezi hogy a formula DNF alakú
     * @return
     */
    public String toDNFString(){
        return "( "+root.toString().replaceAll("\\(","")
                .replaceAll("\\)","")
                .replaceAll("\u2228",") \u2228 (")+" )";

    }

    /**
     * Szöveggé alakító algotitmus, feltételezi hogy a formula KNF alakú
     * @return
     */
    public String toKNFString(){
        return "( "+root.toString().replaceAll("\\(","")
                .replaceAll("\\)","")
                .replaceAll("\u2227",") \u2227 (")+" )";

    }

    /**
     * Ezzel a formulával megegyező Diszjunktív normál formát állít elő
     * @return
     */
    public Formula toDisjunctiveNormalForm(){
        return new Formula(disjunctionOverKonjunction(pushNegationDown(removeImplication(this.root))));//,"\u2227","\u2228"));
    }
    /**
     * Ezzel a formulával megegyező Konjunktív normál formát állít elő
     * @return
     */
    public Formula toKonjunctiveNormalForm(){
        return new Formula(konjunctionOverDiszjunkction(pushNegationDown(removeImplication(this.root))));//,"\u2228","\u2227"));
    }

    /**
     * rekurzív függvény hogy a diszjunkciók és konjunkciók megfelelő sorrendben kövessék egymást.
     * feltételezi hogy implikáció már el lett távolítva, és negáció csak atomi formulákra vonatkozik.
     * @param node
     * @return
     */
    private Node konjunctionOverDiszjunkction(Node node){

        if(node.getSymbol().equals("\u00AC") || (node.getLeft()==null && node.getRight()==null)){//negáció vagy atom
            return node;
        }else if(node.getSymbol().equals("\u2228")){//diszjunkcio
            if (node.getRight().getSymbol().equals("\u2227") || node.getLeft().getSymbol().equals("\u2227")){
                Node disNode,otherNode;
                if (node.getRight().getSymbol().equals("\u2227")){
                    disNode=node.getRight();
                    otherNode=node.getLeft();
                }else{
                    disNode=node.getLeft();
                    otherNode=node.getRight();
                }
                node.setSymbol("\u2227");
                node.setRight(konjunctionOverDiszjunkction(new Node("\u2228",otherNode,disNode.getRight())));
                node.setLeft(konjunctionOverDiszjunkction(new Node("\u2228",otherNode,disNode.getLeft())));
            }
        }else if(node.getSymbol().equals("\u2227")){//konjunkció
            node.setRight(konjunctionOverDiszjunkction(node.getRight()));
            node.setLeft(konjunctionOverDiszjunkction(node.getLeft()));
        }
        return node;
    }
    /**
     * rekurzív függvény hogy a diszjunkciók és konjunkciók megfelelő sorrendben kövessék egymást.
     * feltételezi hogy implikáció már el lett távolítva, és negáció csak atomi formulákra vonatkozik.
     * @param node
     * @return
     */
    private Node disjunctionOverKonjunction(Node node){

        if(node.getSymbol().equals("\u00AC") || (node.getLeft()==null && node.getRight()==null)){//negáció vagy atom
            return node;
        }else if(node.getSymbol().equals("\u2227")){//konjunkció
            if (node.getRight().getSymbol().equals("\u2228") || node.getLeft().getSymbol().equals("\u2228")){
                Node disNode,otherNode;
                if (node.getRight().getSymbol().equals("\u2228")){
                    disNode=node.getRight();
                    otherNode=node.getLeft();
                }else{
                    disNode=node.getLeft();
                    otherNode=node.getRight();
                }
                node.setSymbol("\u2228");
                node.setRight(disjunctionOverKonjunction(new Node("\u2227",otherNode,disNode.getRight())));
                node.setLeft(disjunctionOverKonjunction(new Node("\u2227",otherNode,disNode.getLeft())));
            }
        }else if(node.getSymbol().equals("\u2228")){//distjunkció
            node.setRight(disjunctionOverKonjunction(node.getRight()));
            node.setLeft(disjunctionOverKonjunction(node.getLeft()));
        }
        return node;
    }

    /**
     * A fenti két fügvény(konjunctionOverDiszjunkction és disjunctionOverKonjunction) általánosítását megcélzó próbálkozás.
     * Nem működik.
     * @param node
     * @param over
     * @param under
     * @return
     */
    private Node distributeOperators(Node node, String over,String under){
        if(node.getSymbol().equals("\u00AC") || (node.getLeft()==null && node.getRight()==null)){//negáció vagy atom
            return node;
        }else if(node.getSymbol().equals(under)){//diszjunkcio
            if (node.getRight().getSymbol().equals(over) || node.getLeft().getSymbol().equals(over)){
                Node disNode,otherNode;
                if (node.getRight().getSymbol().equals(over)){
                    disNode=node.getRight();
                    otherNode=node.getLeft();
                }else{
                    disNode=node.getLeft();
                    otherNode=node.getRight();
                }
                node.setSymbol(over);
                node.setRight(distributeOperators(new Node(under,otherNode,disNode.getRight()),over,under));
                node.setLeft(distributeOperators(new Node(under,otherNode,disNode.getLeft()),over,under));
            }
        }else if(node.getSymbol().equals(over)){//konjunkció
            node.setRight(distributeOperators(node.getRight(),over,under));
            node.setLeft(distributeOperators(node.getLeft(),over,under));
        }
        return node;

    }

    /**
     * Rekurzív függvény amely eléri hogy a negáviók csak atomi formulákra vonatkozzanak.
     * Feltételezi, hogy a negávió csak atomi formulákra vonatkozik.
     * @param node
     * @return
     */
    private Node pushNegationDown(Node node){

        if (node.getSymbol().equals("\u00AC")){
            Node negated = node.getLeft();
            if (negated.getSymbol().equals("\u00AC")){
                node=pushNegationDown(negated.getLeft());
            }else if (negated.getSymbol().equals("\u2227")){
                node.setSymbol("\u2228");
                node.setLeft(pushNegationDown(new Node("\u00AC",null,negated.getLeft())));
                node.setRight(pushNegationDown(new Node("\u00AC",null,negated.getRight())));

            }else if (negated.getSymbol().equals("\u2228")){
                node.setSymbol("\u2228");
                node.setLeft(pushNegationDown(new Node("\u00AC",null,negated.getLeft())));
                node.setRight(pushNegationDown(new Node("\u00AC",null,negated.getRight())));
            }else {
                return node;
            }
        }else{
            if (node.getLeft()==null && node.getRight()==null){
                return node;
            }else{
                node.setLeft(pushNegationDown(node.getLeft()));
                node.setRight(pushNegationDown(node.getRight()));
            }

        }

        return node;
    }

    /**
     * Az implikációkat egyenértékű diszjunkcióra cseréli.
     * @param node
     * @return
     */
    private Node removeImplication(Node node){
        if (node.getSymbol().equals("\u2283")){
            //A->B = !A v B
            node.setLeft(removeImplication(new Node("\u00AC",null,node.getLeft())));
            node.setRight(removeImplication(node.getRight()));
            node.setSymbol("\u2228");

        }else if (node.getSymbol().equals("\u00AC")){
            node.setLeft(removeImplication(node.getLeft()));
        } else if(node.getRight()==null && node.getLeft()==null){

        }else {
            node.setLeft(removeImplication(node.getLeft()));
            node.setRight(removeImplication(node.getRight()));
        }
        return node;
    }

    /**
     * Létrehozza a faszerkezetet művelettábla alapján
     * @param table a bitsorozat
     * @param distOrCon Igaz a ha DNT-et akarunk hamis ha KNF-et
     * @param simplyfi Próbálja e meg egszerűsíteni
     * @return
     * @throws Exception
     */
    public static Formula fromOperationTable(String table,boolean distOrCon,boolean simplyfi) throws Exception {
        table=table.replaceAll("\\s+","");
        double numberOfVariables = Math.log(table.length())/Math.log(2);
        if(numberOfVariables==(int)numberOfVariables || table.matches("[0-1]+")) {
            int countForZero =0;
            int countForOne =0;
            char lines[][];
            for (int i = 0; i<table.length(); i++) {
                if (table.charAt(i) == '1') {
                    countForOne++;
                } else {
                    countForZero++;
                }
            }
            if (distOrCon){
                lines = new char[countForOne][(int)numberOfVariables];
                int flag=0;
                for (int i = 0; i<table.length(); i++) {
                    if (table.charAt(i) == '1') {

                        String binary = Integer.toBinaryString(i);
                        String padded = String.format("%1$" + (int) numberOfVariables + "s", binary).replace(' ', '0');
                        for (int j = 0; j < numberOfVariables; j++) {
                            lines[flag][j] = padded.charAt(j);
                        }
                        //System.out.println(padded+" , "+binary);
                        flag++;
                    }
                }
                if (simplyfi) {lines=quineMcCluskey(lines);}

                ArrayList<Node> konChains = new ArrayList<Node>();
                for (int j=0;j<lines.length;j++) {
                    //System.out.println(lines[j]);
                    ArrayList<Node> literals = bitsToLiterals(lines[j]);
                    konChains.add(toKonjChain(literals));
                }
                return new Formula(toDisChain(konChains));

            }else {
                lines = new char[countForZero][(int)numberOfVariables];
                int flag=0;
                for (int i = 0; i<table.length(); i++){
                    if (table.charAt(i)=='0'){
                        String binary= Integer.toBinaryString(i);
                        String padded=String.format("%1$" + (int)numberOfVariables + "s", binary).replace(' ','0');
                        for (int j=0;j<numberOfVariables;j++) {
                            lines[flag][j]=padded.charAt(j);
                        }
                        flag++;
                    }
                }
                    ArrayList<Node> diszChains = new ArrayList<Node>();
                    for (int j=0;j<countForZero;j++) {
                        ArrayList<Node> literals = bitsToLiterals(lines[j]);
                        diszChains.add(toDisChain(literals));
                    }
                    return new Formula(toKonjChain(diszChains));

                }}else {
            throw new Exception("A bemenet nem megfelelő");
        }
    }

    /**
     * Quine-McCluskey minimalizáció a fromOperationTable esetén lévő egyszerűsíteéshez.
     * kell még egy szűrés hogy csak az essenciálisakat jelenítse meg.
     * @param lines
     * @return
     */
    private static char[][] quineMcCluskey(char[][] lines){

        ArrayList<char[]> current=new ArrayList<char[]>(Arrays.asList(lines));
        ArrayList<char[]> finals=new ArrayList<char[]>();
        ArrayList<char[]> next;
        do {
            next=new ArrayList<char[]>();
            ArrayList<Boolean> marks=new ArrayList<Boolean>();
            ArrayList<Integer> oneclass=new ArrayList<Integer>();
            for (int i=0;i<current.size();i++){
                marks.add(true);
                oneclass.add(oneClass(current.get(i)));
            }
            for (int i=0; i<current.size();i++){
                for (int j=i;j<current.size();j++){

                    if (oneclass.get(i)+1==oneclass.get(j) && distance(current.get(i),current.get(j))==1){
                        marks.set(i,false);
                        marks.set(j,false);
                        char[] nextgeneration = current.get(i);
                        nextgeneration[firstDifferentBit(current.get(i),current.get(j))]='-';
                        next.add(nextgeneration);
                    }
                }
            }
            for (int i=0; i<marks.size();i++){
                if (marks.get(i)){
                    finals.add(current.get(i));
                    //System.out.println(current.get(i));
                }
            }
            if (!next.isEmpty())
                current=next;

        }while (!next.isEmpty());
        char[][] value=new char[finals.size()][lines[0].length];
        for (int i=0;i<finals.size();i++){
            value[i]=finals.get(i);
        }
        return value;
    }
    private static int distance(char[] A, char[] B){
        int count=0;
        for (int i =0; i<A.length; i++){
            if (A[i]!=B[i]){
                count++;
            }
        }
        //System.out.println(count);
        return count;
    }
    private static int firstDifferentBit(char[] A, char[] B){
        int index=0;
        for (int i=0; i<A.length;i++){
            if (A[i]!=B[i]){
                return i;
            }
        }
        return index;
    }
    private static int oneClass(char[] A){
        int count=0;
        for (int i =0; i<A.length;i++)
            if (A[i]=='1')
                count++;
        return count;
    }

    /**
     * Biteket literálokká alakító segédfügvény
     * @param bits
     * @return
     */
    private static ArrayList<Node> bitsToLiterals(char[] bits){
        ArrayList<Node> literals = new ArrayList<Node>();
        for (int i =0; i<bits.length;i++) {
            if(bits[i]=='1') {
                literals.add(new Node("X"+(i+1),null,null));
            }else if(bits[i]=='0'){
                literals.add(new Node("\u00AC",null,new Node("X"+(i+1),null,null)));
            }
        }
        return literals;
    }
    private static Node toKonjChain(ArrayList<Node> nodes){
        if (nodes.size()>0){
            Node result = nodes.get(0);
            for (int i =1; i< nodes.size();i++ ){
                result = new Node("\u2227", nodes.get(i),result);
            }
            return result;
        }
        return null;
    }
    private static Node toDisChain(ArrayList<Node>  nodes){

        if (nodes.size()>0){
            Node result = nodes.get(0);
            for (int i =1; i< nodes.size();i++ ){
                result = new Node("\u2228", nodes.get(i),result);
            }
            return result;
        }
        return null;
    }

}
