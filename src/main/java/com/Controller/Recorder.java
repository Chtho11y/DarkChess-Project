package com.Controller;


import com.Model.Piece;
import com.Model.PieceColor;
import com.View.ChessPainter;
import javafx.scene.control.TextInputDialog;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Recorder {

    static int seed;

    static class Point{
        int x,y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Operation{

        String op;
        Point p;
        Object arg;

        public Operation(String op, Point p) {
            this.op = op;
            this.p = p;
        }

        public Operation(String op, Point p1, Object p2) {
            this.op = op;
            this.p = p1;
            this.arg = p2;
        }

        public Operation(String s){
            String[] res=s.split(" ");
            op=res[0];
            p=new Point(Integer.parseInt(res[1]),Integer.parseInt(res[2]));
            if(op.equals("replace"))
                arg=new Piece(Integer.parseInt(res[3]),res[4],Integer.parseInt(res[5]));
            else if(op.equals("move"))
                arg=new Point(Integer.parseInt(res[3]),Integer.parseInt(res[4]));
        }

        @Override
        public String toString(){
            String s=String.format("%s %d %d",op,p.x,p.y);
            if(op.equals("move"))
                s+=String.format(" %d %d",((Point)arg).x,((Point)arg).y);
            if(op.equals("replace"))
                s+=" "+arg.toString();
            return s;
        }

        public String getOp() {
            return op;
        }

        public Point getPoint() {
            return p;
        }

        public Object getArg() {
            return arg;
        }
    }

    static ArrayList<Operation> operationList;
    static void recordMove(int x1,int y1,int x2,int y2){
        operationList.add(new Operation("move",new Point(x1,y1),new Point(x2,y2)));
    }

    static void recordSeed(int seed){
        Recorder.seed=seed;
    }

    static void clear(){
        if(operationList==null)
            operationList=new ArrayList<>();
        operationList.clear();
    }

    static void recordReplace(int x,int y,Piece piece){
        System.out.printf("replaced (%d,%d):%s\n",x,y,piece.getText());
        operationList.add(new Operation("replace",new Point(x,y),piece));
    }

    static void recordDiscover(int x,int y){
        System.out.printf("discovered (%d,%d)\n",x,y);
        operationList.add(new Operation("discover",new Point(x,y)));
    }

    static Operation pop(){
        int len=operationList.size();
        if(len<=1)return null;
        Operation operation=operationList.get(len-1);
        operationList.remove(len-1);
        System.out.println("withdraw:"+operation.toString());
        return operation;
    }

    public static void withdraw(){
        Operation op=pop();
        if(op==null)return;
        if(op.getOp().equals("replace")){
            Operation mv=pop();
            assert mv != null;
            ChessExecutor.move(((Point)mv.getArg()).x,((Point)mv.getArg()).y,mv.getPoint().x,mv.getPoint().y);
            ChessExecutor.reset(op.getPoint().x,op.getPoint().y,(Piece) op.getArg());
        }else if(op.getOp().equals("move")){
            ChessExecutor.move(((Point)op.getArg()).x,((Point)op.getArg()).y,op.getPoint().x,op.getPoint().y);
        }else{
            ChessExecutor.cover(op.getPoint().x,op.getPoint().y);
        }
    }

    static class Progress{
        String date,result,name,type;
        int seed,hashcode;
        ArrayList<String> operations;

        public Progress(String date, String result, String name, int seed, ArrayList<Operation> op,String type) {
            this.date = date;
            this.result = result;
            this.name = name;
            this.type=type;
            if(name.equals(""))
                this.name="未命名";
            this.seed = seed;
            operations=new ArrayList<>();
            for (Operation operation : op) {
                operations.add(operation.toString());
            }
            hashcode=stringHash(result)^seed;
            for (String operation : operations) {
                hashcode^=stringHash(operation);
            }
        }

        public Progress(BufferedReader bf) throws IOException {
            operations=new ArrayList<>();
            hashcode=Integer.parseInt(bf.readLine());
            type= bf.readLine();
            int counter= Integer.parseInt(bf.readLine());
            name=bf.readLine();
            date=bf.readLine();
            result=bf.readLine();
            seed= Integer.parseInt(bf.readLine());
            for (int i = 0; i < counter; i++) {
                operations.add(bf.readLine());
            }
        }

        public void writeTo(BufferedWriter output) throws IOException {
            output.write(Integer.toString(hashcode));
            output.newLine();
            output.write(type);
            output.newLine();
            output.write(Integer.toString(operations.size()));
            output.newLine();
            output.write(name);
            output.newLine();
            output.write(date);
            output.newLine();
            output.write(result);
            output.newLine();
            output.write(Integer.toString(seed));
            output.newLine();
            for (String operation : operations) {
                output.write(operation);
                output.newLine();
            }
        }

        @Override
        public int hashCode() {
            return hashcode;
        }

        static int stringHash(String s){
            long ans=0;
            for(int i=0;i<s.length();++i){
                ans=(ans*19260817L+s.charAt(i)+114514)%998244353;
            }
            return (int)(ans%998244353);
        }
    }

    static ArrayList<Progress> progressList=new ArrayList<>();
    static ArrayList<String> nameList=new ArrayList<>();

    static File file = new File(Objects.requireNonNull(Recorder.class.getResource("")).getPath()+"ProgressList.txt");
    static void load() {
        nameList.clear();
        progressList.clear();

        FileReader fr;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        BufferedReader input=new BufferedReader(fr);

        int counter;
        try {
            String s=input.readLine();

            counter=(s==null||s.isBlank())?0:Integer.parseInt(s);
            for(int i=0;i<counter;++i)
                nameList.add(input.readLine());
            for (int i = 0; i < counter; i++) {
                Progress p=new Progress(input);
                progressList.add(p);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(counter+" files are found");
    }

    public static void loadFile(int progressId){
        Progress progress=progressList.get(progressId);
        Executor.gameStart(false,progress.seed);
        Point point=new Point(0,0);
        for (String operation : progress.operations) {
            Operation op=new Operation(operation);
            if(op.op.equals("discover")){
                ChessExecutor.discover(op.p.x,op.p.y);
            }else if(op.op.equals("move")){
                operationList.add(op);
                ChessExecutor.move(op.p.x,op.p.y,((Point)(op.arg)).x,((Point)(op.arg)).y);
                point=op.p;
            }else{
                Piece p=(Piece) (op.arg);
                operationList.add(op);
                ChessPainter.modifyPieceNumber(p,1);
                ChessPainter.hidePiece(point.x, point.y);
                ChessExecutor.calcScore(p,1);
            }
        }
    }

    public static void save() {
        BufferedWriter output;
        try {
            FileWriter wr=new FileWriter(file);
            output=new BufferedWriter(wr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            output.write(Integer.toString(progressList.size()));
            System.out.println(progressList.size());
            output.newLine();
            for (String s : nameList) {
                output.write(s);
                output.newLine();
                System.out.println(s);
            }
            for (Progress progress : progressList) {
                progress.writeTo(output);
            }
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void buildProgress(){
        System.out.println("build.");
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
        String date=formatter.format(System.currentTimeMillis());
        String result=String.format("红 %d - 黑 %d", ChessExecutor.getScore(PieceColor.RED), ChessExecutor.getScore(PieceColor.BLACK));

        TextInputDialog dialog=new TextInputDialog("未命名");
        dialog.setTitle("命名");
        dialog.setHeaderText("输入存档名称");
        Optional<String> res=dialog.showAndWait();
        if(res.isEmpty())
            return;

        Progress p=new Progress(date,result,res.get(),seed,operationList,"存档");

        if(nameList.contains(Integer.toString(p.hashCode())))
            return;

        progressList.add(p);
        nameList.add(Integer.toString(p.hashCode()));
    }

    public static void deleteFile(int id){
        nameList.remove(id);
        progressList.remove(id);
    }

    public static ArrayList<String> getNameList(){
        ArrayList<String> nameList=new ArrayList<>();
        for (Progress progress : progressList) {
            nameList.add(progress.name);
        }
        return nameList;
    }

    public static ArrayList<String> getDateList(){
        ArrayList<String> dateList=new ArrayList<>();
        for (Progress progress : progressList) {
            dateList.add("["+progress.type+"] "+progress.date+"  "+progress.result);
        }
        return dateList;
    }

    public static int length(){
        return nameList.size();
    }

    public static String getName(int id){
        return progressList.get(id).name;
    }

    public static void setName(int id,String name){
        progressList.get(id).name=name;
    }
}
