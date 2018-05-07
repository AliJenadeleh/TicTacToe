package ir.jcafe.tictactoe.TicTacToe;

import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ir.jcafe.tictactoe.MainActivity;
import ir.jcafe.tictactoe.R;

/**
 * Created by Jandil on 3/3/2017.
 */

public class TicTacToeProvider {
    public interface IMyRunnable {
        public abstract void Run(String playerName);
    };
    private final static long delay = 400;
    private int[][] States ;
    private int[][] idAry;
    private MainActivity context;
    private IMyRunnable _onGameEnd;
    private boolean isXTurn,isGameEnd,response = true,isHard;
    private int moveCounter,xColor,oColor;
    private Button.OnClickListener clickListener;


    public  TicTacToeProvider(MainActivity context,int[][] idAry,IMyRunnable onGameEnd,boolean ISHard){
        this.idAry = idAry;
        this.context = context;
        this._onGameEnd = onGameEnd;
        isHard = ISHard;
        reset();
    }

    public void reset(MainActivity context,boolean isHard){
        this.context = context;
        this.isHard = isHard;
        reset();
    }

    private void reset(){

        isXTurn = true;
        isGameEnd = false;
        moveCounter = 0;

        xColor = getColorResource(R.color.colorX);
        oColor = getColorResource(R.color.colorO);

        createClickListener();

        resetLayout();

        resetStates();

        //setLblUser();


    }

    private void createClickListener(){
        if(clickListener == null)
            clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(response) {
                        Button btn = (Button) v;
                        userMove(btn);
                    }
                }
            };
    }

    private void resetLayout(){
        int i,j;
        Button btn;
        for(i=0;i<3;i++)
            for(j=0;j<3;j++)
            {
                int id = idAry[i][j];
                btn = (Button)this.context.findViewById(id);
                btn.setText("");
                btn.setTag(-1);
                btn.setOnClickListener(clickListener);
            }
    }

    private void resetStates(){
        int[][] tmp = {{-1,-1,-1},{-1,-1,-1},{-1,-1,-1}};
        States = tmp;
    }

    private void setLblUser(){
        String player = getCurrentPlayer();
        setLblUser(player);
    }
    private void setLblUser(int msgID){
        String message = context.getString(msgID);
        setLblUser(message);
    }
    private void setLblUser(String Name){

        if(_onGameEnd != null){
            _onGameEnd.Run(Name);
        }
    }

    private String getCurrentPlayer(){
        String name;
        if(isXTurn)
          name = context.getResources().getString(R.string.userX);
        else
            name = context.getResources().getString(R.string.userO);

        return  name;
    }

    private void userMove(Button button){
        if(!isGameEnd) {
            int tag = (int) button.getTag();
            if (tag == -1) {
                _userMove(button);
                brainMove();
            }
        }
    }

    private void _userMove(Button button){
        button.setTextColor(xColor);
        button.setText(R.string.remove);
        setState(button.getId(),1);
        moveCounter ++;
        ifVictoryWinnerIs("X");
    }

    private void ifVictoryWinnerIs(String name){
        //Log.i("JCafe.Messages",moveCounter + " Check Victory for " + name );
        if(checkVictory())
        {
            //Log.i("Jcafe.Message","check ok");
            isGameEnd = true;
            setLblUser( " برنده شد " + name);
        }else if(moveCounter >= 9)
        {
            isGameEnd = true;
            setLblUser(R.string.no1win);
        }
    }

    private boolean checkVictory(){
        //Log.i("Jcafe.Message","into main check ");
        if(moveCounter >= 4) {
            for (int i = 0; i < 3; i++) {
                if (checkRow(i) || checkColumn(i)) {
                    return true;
                }
            }
            return checkZigzag();
        }
        return  false;
    }

    private boolean checkRow(int row){
        int a1 = States[row][0];
        if(a1 != -1) {
            int a2 = States[row][1], a3 = States[row][2];
            return  a1 == a2 && a1 == a3;
        }

        return  false;
    }

    private boolean checkColumn(int col){
        int a1 = States[0][col];
        if(a1 != -1) {
            int a2 = States[1][col], a3 = States[2][col];
            return  a1 == a2 && a1 == a3;
        }

        return  false;
    }

    private boolean checkZigzag(){
        int a1,a2,a3,a4,a5;
        a2 = States[1][1];
        if(a2 != -1)
        {
            a1 = States[0][0];
            a3 = States[2][2];

            a4 = States[0][2];
            a5 = States[2][0];

            return  (a1 == a2 && a1 == a3) ||
                    (a4 == a2 && a2 == a5);
        }
        return  false;
    }

    private void _brainMove(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                response = false;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // ----------------------------
                if(!tryWin())
                    if(!tryDefender())
                        if(!tryTakeCenter())
                            if(!tryTakeCorner())
                                takeAnyEmpty();

                response = true;
                // ----------------------------
            }
        });
    }
    private void brainMove(){
        // 0 == o
        if(!isGameEnd){
           _brainMove();
        }
    }

    private boolean tryTakeCenter(){
        if(States[1][1] == -1)
        {
            brainTake(1,1);
            return true;
        }
        return  false;
    }
    private boolean tryTakeCorner(){

        if(States[0][0] == -1)
        {
            brainTake(0,0);
            return true;
        }else if(States[2][2] == -1)
        {
            brainTake(2,2);
            return true;
        }else if(States[2][0] == -1)
        {
            brainTake(2,0);
            return true;
        }else if(States[0][2] == -1)
        {
            brainTake(0,2);
            return true;
        }

        return false;
    }
    private boolean tryWin(){
        for(int i=0;i<3;i++)
        {
         if(tryRow(i) || tryColumn(i))
             return true;
        }

        return tryZigZag();
    }
     private boolean tryRow(int row){

        int counter=0,tmp;

        for(int i=0;i<3;i++)
        {
            tmp = States[row][i];
            if(tmp == 0)
                counter++;
            else if(tmp== 1)
                counter --;
        }

        if(counter == 2)
        {
            for(int i=0;i<3;i++)
              if(States[row][i]== -1)
              {
                  brainTake(row,i);
                  return true;
              }
        }
        return  false;
    }
     private boolean tryColumn(int col){

        int counter=0,tmp;

        for(int i=0;i<3;i++)
        {
            tmp = States[i][col];
            if(tmp == 0)
                counter++;
            else if(tmp== 1)
                counter --;
        }

        if(counter == 2)
        {
            for(int i=0;i<3;i++)
                if(States[i][col]== -1)
                {
                    brainTake(i,col);
                    return true;
                }
        }
        return  false;
    }
     private boolean tryZigZag(){

        if(States[1][1] == -1)
        {
            return zigZagLTR() || zigZagRTL();
        }
        return  false;
    }
     private boolean zigZagLTR(){

        int counter = 0,tmp;

        for(int i=0;i<3;i++)
        {
            tmp = States[i][i];
            if(tmp == 0)
                counter++;
            if(tmp == 1)
                counter --;
        }

        if(counter == 2){
            for(int i=0;i<3;i++)
            {
                if(States[i][i] == -1) {
                  brainTake(i,i);
                    return  true;
                }
            }
        }

        return  false;
    }
     private boolean zigZagRTL(){

        int counter = 0,tmp;

        for(int i=2;i>=0;i--)
        {
            tmp = States[i][2 - i];
            if(tmp == 0)
                counter++;
            if(tmp == 1)
                counter --;
        }

        if(counter == 2){
            for(int i=2;i>=0;i--)
            {
                if(States[i][2 - i] == -1) {
                    brainTake(i,2 - i);
                    return  true;
                }
            }
        }

        return  false;
    }

    private boolean tryDefender(){
        if(!isHard) return false;

        for(int i=0;i<3;i++)
        {
            if(defenderRow(i) || defenderColumn(i))
                return true;
        }

        return defenderZigZag();

    }

    private boolean defenderColumn(int col){

        int counter=0,tmp;

        for(int i=0;i<3;i++)
        {
            tmp = States[i][col];
            if(tmp == 1)
                counter++;
            else if(tmp== 0)
                counter --;
        }

        if(counter == 2)
        {
            for(int i=0;i<3;i++)
                if(States[i][col]== -1)
                {
                    brainTake(i,col);
                    return true;
                }
        }
        return  false;
    }
    private boolean defenderRow(int row){

        int counter=0,tmp;

        for(int i=0;i<3;i++)
        {
            tmp = States[row][i];
            if(tmp == 1)
                counter++;
            else if(tmp== 0)
                counter --;
        }

        if(counter == 2)
        {
            for(int i=0;i<3;i++)
                if(States[row][i]== -1)
                {
                    brainTake(row,i);
                    return true;
                }
        }
        return  false;
    }
    private boolean defenderZigZag(){
            return defenderZigZagLTR() || defenderZigZagRTL();
    }

    private boolean defenderZigZagLTR(){

        int counter = 0,tmp;

        for(int i=0;i<3;i++)
        {
            tmp = States[i][i];
            if(tmp == 1)
                counter++;
            if(tmp == 0)
                counter --;
        }

        if(counter == 2){
            for(int i=0;i<3;i++)
            {
                if(States[i][i] == -1) {
                    brainTake(i,i);
                    return  true;
                }
            }
        }

        return  false;
    }
    private boolean defenderZigZagRTL(){

        int counter = 0,tmp;

        for(int i=2;i>=0;i--)
        {
            tmp = States[i][2 - i];
            if(tmp == 1)
                counter++;
            if(tmp == 0)
                counter --;
        }

        if(counter == 2){
            for(int i=2;i>=0;i--)
            {
                if(States[i][2 - i] == -1) {
                    brainTake(i,2 - i);
                    return  true;
                }
            }
        }

        return  false;
    }

    private void takeAnyEmpty(){
        int i,j;
        for(i= 0 ;i<3;i++)
            for (j=0;j<3;j++)
             if(States[i][j] == -1)
             {
                 brainTake(i,j);
                 return;
             }
    }

    private void brainTake(int i,int j)
    {
        States[i][j] = 0;
        int id = idAry[i][j];

        Button button = (Button)context.findViewById(id);
        button.setTextColor(oColor);
        button.setText(R.string.disk);
        button.setTag(0);
        //setState(button.getId(),1);
        moveCounter ++;
        ifVictoryWinnerIs("O");
    }

    private void setState(int id,int state){
        int i,j;

        for(i=0;i<3;i++)
            for(j=0;j<3;j++){
                if(idAry[i][j] == id) {
                    States[i][j] = state;
                    return;
                }
            }
    }

    private int getColorResource(int colorId){
        if(Build.VERSION.SDK_INT <= 21)
            return  context.getResources().getColor(colorId);

        return ContextCompat.getColor(context,colorId);
    }

}
