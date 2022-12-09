package Network;

public class WebMessage {
    String message,arg;

    public WebMessage(String message, String arg) {
        this.message = message;
        this.arg = arg;
    }

    public WebMessage(String fullMsg){
        String[] res=fullMsg.split("@");
        message=res[0];
        arg=res[1];
    }

    public String getMessage(){
        return message;
    }

    public String getArg(){
        return arg;
    }

    public String toString(){
        return message+"@"+arg;
    }

    public static String toString(String message,String arg){
            return message+"@"+arg;
    }
    public boolean isClosed(){
        return message.equals("closed");
    }

    public boolean isDisconnect(){
        return message.equals("disconnect");
    }

    public boolean isCloseAll(){
        return message.equals("close");
    }
}
