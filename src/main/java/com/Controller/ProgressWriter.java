package com.Controller;

import com.Model.ChessBoard;
import com.Model.Piece;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

public class ProgressWriter {

    static int errId;
    static String errMessage;

    public static Recorder.Progress readFromFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CDC archive","*.cdc"));
        File file= fileChooser.showOpenDialog(Executor.getStage());

        FileReader input=new FileReader(file);
        BufferedReader reader=new BufferedReader(input);
        Recorder.Progress p=new Recorder.Progress();

        p.operations=new ArrayList<>();
        int hashcode=Integer.parseInt(reader.readLine());
        p.type= reader.readLine();
        int counter= Integer.parseInt(reader.readLine());
        p.name=reader.readLine();
        p.date=reader.readLine();
        p.result=reader.readLine();
        p.seed= Integer.parseInt(reader.readLine());

        errId=200;
        errMessage="";

        ChessBoard cb=ChessBoard.createCB(p.seed);
        for (int i = 0; i < counter; i++) {
            String s=reader.readLine();
            p.operations.add(s);
            Recorder.Operation op=new Recorder.Operation(s);
            if(!op.getOp().equals("replace"))p.length++;
            if(op.getOp().equals("move")){
                Recorder.Point np=(Recorder.Point)(op.getArg());
                if(cb.moveCheck(op.p.x,op.p.y,np.x,np.y)){
                    cb.move(op.p.x,op.p.y,np.x,np.y);
                }else{
                    errId=105;
                    errMessage="存在不合法的移动步骤！";
                }
            }else if(op.getOp().equals("discover")){
                if(cb.flipCheck(op.p.x,op.p.y)){
                    cb.discover(op.p.x,op.p.y);
                }else{
                    errId=105;
                    errMessage="存在不合法的翻棋步骤！";
                }
            }else if(!op.getOp().equals("replace")){
                errId=105;
                errMessage="无法识别的行棋："+op.getOp();
            }
        }
        p.hashcode=p.seed;
        for (String operation : p.operations) {
            p.hashcode=(p.hashcode+Recorder.Progress.stringHash(operation))%998244353;
        }

        if(p.hashcode!=hashcode&&errId==200){
            errMessage="注意：存档可能被修改。";
        }

        cb=ChessBoard.createCB(p.seed);
        for(int i=0;i<8;++i){
            String s=reader.readLine();
            if(s==null){
                errId=102;
                errMessage="棋盘并非4*8";
                return null;
            }
            String[] res=s.split(";");
            if(res.length!=4){
                errId=102;
                errMessage="棋盘并非4*8";
                return null;
            }
            for (int j = 0; j < 4; j++) {
                String[] t=res[j].split(" ");
                try{
                    int id=Integer.parseInt(t[0]);
                    if(id<1||id>7||(!t[1].equals("RED")&&!t[1].equals("BLACK"))){
                        errId=103;
                        errMessage="无法识别的棋子："+res[j];
                    }
                    if(id!=cb.getBoard()[j][i].getType().getId()||!t[1].equals(cb.getBoard()[j][i].getColor().name())&&errId==200){
                        errId=108;
                        errMessage="棋盘与种子不一致："+res[j];
                    }

                }catch (NumberFormatException e){
                    errId=103;
                    errMessage="无法识别的棋子："+res[j];
                }
            }
        }

        String s=reader.readLine();
        if(s==null&&errId>=105){
            errId=104;
            errMessage="缺失行棋方";
        }

        return p;
    }

    public static boolean succeed(){
        if(errId==200){
            Alert alert=new Alert(Alert.AlertType.NONE,"导入成功"+errMessage,new ButtonType("确定", ButtonBar.ButtonData.YES));
            alert.setTitle("导入成功");
            alert.initOwner(Executor.getStage());
            alert.showAndWait();
            return true;
        }else{
            Alert alert=new Alert(Alert.AlertType.NONE,"导入失败"+"("+errId+"):"+errMessage,new ButtonType("确定", ButtonBar.ButtonData.YES));
            alert.setTitle("导入成功");
            alert.initOwner(Executor.getStage());
            alert.showAndWait();
            return false;
        }
    }

    public static void writeToFile(Recorder.Progress progress){
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select Direction");
        File file=dirChooser.showDialog(Executor.getStage());
        TextInputDialog dialog=new TextInputDialog(progress.name);
        dialog.setTitle("命名");
        dialog.setHeaderText("输入存档名称");
        Optional<String> res=dialog.showAndWait();
        if(res.isEmpty())
            return;
        String name=res.get();
        file=new File(file.getAbsolutePath()+"/"+progress.name+".cdc");
        try{
            file.createNewFile();
        }catch (IOException e) {
            Alert alert=new Alert(Alert.AlertType.NONE,e.getMessage(),new ButtonType("确定", ButtonBar.ButtonData.YES));
            alert.setTitle("错误");
            alert.initOwner(Executor.getStage());
            alert.showAndWait();
            return;
        }
        ChessBoard cb=ChessBoard.createCB(progress.seed);
        try{
            FileWriter fw=new FileWriter(file);
            BufferedWriter bf=new BufferedWriter(fw);
            progress.writeTo(bf);
            for(int i=0;i<8;++i){
                StringBuilder sb=new StringBuilder();
                for(int j=0;j<4;++j){
                    sb.append(cb.getBoard()[j][i].getString());
                    if(j!=3)sb.append(";");
                }
                bf.write(sb.toString());
                bf.newLine();
            }
            Recorder.Operation op=new Recorder.Operation(progress.operations.get(0));
            bf.write(cb.getBoard()[op.p.x][op.p.y].getColor().name());
            bf.flush();
            bf.close();
        }catch (IOException e) {
            Alert alert=new Alert(Alert.AlertType.NONE,e.getMessage(),new ButtonType("确定", ButtonBar.ButtonData.YES));
            alert.setTitle("错误");
            alert.initOwner(Executor.getStage());
            alert.showAndWait();
        }

    }
}
