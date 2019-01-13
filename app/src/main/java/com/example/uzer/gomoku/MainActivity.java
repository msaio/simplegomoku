package com.example.uzer.gomoku;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //board size
    static int maxSize = 15;
    private Context context;
    //array of cell
    private ImageView[][] ivCell = new ImageView[maxSize][maxSize];
    //4 status of the cell: empty(null)[0], blank(but not null)[3], player1(x)[1], player2 or bot(o)[2]
    private Drawable[] drawCell=new Drawable[4];
    //button newgame
    private Button btnNewGame;
    private TextView tvInfo;
    //3 value of the cell: empty(belongs to nobody )(0), player 1(1), bot or player 2(2)
    private int[][] valueCell = new int[maxSize][maxSize];
    //3 status of the game: 0(no winner), 1(player wins), 2(bot wins)
    private int winner;
    private boolean firstmove;
    // difine position fo the cell, x and y axis of cell
    private int xMove, yMove;
    private int turn;
    private boolean isClicked;
    static int playWith = 1; // 0 is pve, 1 is pvp
    private Boolean switchStateOption = false;

    CheckBox pvp;
    CheckBox pve;
//    EditText sizeOption;
    Button ok;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChangeOptionDialog();

        context = this;
        setListen();
        loadResources();
        designBoardGame();
    }

    private void ChangeOptionDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_option);

        pvp = dialog.findViewById(R.id.pvp);
        pve = dialog.findViewById(R.id.pve);
//        sizeOption = dialog.findViewById(R.id.sizeOption);
        ok = dialog.findViewById(R.id.buttonOk);

        pvp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(pvp.isChecked()) pve.setChecked(false);
                else pve.setChecked(true);
            }
        });
        pve.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(pve.isChecked()) pvp.setChecked(false);
                else pvp.setChecked(true);
            }

        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Hello", "maxsize = "+ maxSize+"; playWith = "+playWith);
                if (pve.isChecked()) playWith = 0;
                else if (pvp.isChecked()) playWith = 1;
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void setListen() {
        btnNewGame = (Button) findViewById(R.id.button_newgame);
        tvInfo = (TextView) findViewById(R.id.information_of_thegame);

        btnNewGame.setText("Play Game");
        tvInfo.setText("Hello, My Friend");

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init_game();

                if(playWith == 0){
                    play_game_vs_bot();
                }else{
                    play_game_vs_friend();
                }
            }
        });
    }

    private void init_game() {
        firstmove = true;
        winner = 0;
        for (int i=0;i<maxSize;i++){
            for (int j=0;j<maxSize;j++){
                ivCell[i][j].setImageDrawable(drawCell[0]);
                valueCell[i][j]=0;
            }
        }
    }
    // Play with bot
    private void play_game_vs_bot() {
        Random random = new Random();
        turn = random.nextInt(2)+1;
        if(turn==1){
            Toast.makeText(context, "Player goes first", Toast.LENGTH_SHORT).show();
            playerTurn();
        }else{
            Toast.makeText(context, "Bot goes first", Toast.LENGTH_SHORT).show();
            botTurn();
        }
    }
    // Play with player
    private void play_game_vs_friend(){
        Random random = new Random();
        turn = random.nextInt(2)+1;
        if(turn==1){
            Toast.makeText(context, "Player 1 goes first", Toast.LENGTH_SHORT).show();
            playerTurn();
        }else{
            Toast.makeText(context, "Player 2 goes first", Toast.LENGTH_SHORT).show();
            player2Turn();
        }
    }

    private void player2Turn() {
        tvInfo.setText("Player 2's Turn");
        firstmove=false;
        isClicked=false;
    }

    private void playerTurn() {
//        Log.d("hentai", "playerTurn: ");
        if(playWith == 0) tvInfo.setText("Player 's Turn");
        else tvInfo.setText("Player 1's Turn");
        firstmove=false;
        isClicked=false;
    }


    private void botTurn() {
//        Log.d("hentai", "botTurn: ");
        tvInfo.setText("Bot's Turn");
        if(firstmove){
            firstmove=false;
            xMove=maxSize/2;
            yMove=maxSize/2; //bot will always choose center by default
            make_a_move();
        }else {
            findBotMove();
            make_a_move();
        }
    }
    private final int[] iRow={-1,-1,-1,0,1,1, 1, 0};
    private final int[] iCol={-1, 0, 1,1,1,0,-1,-1};
    private void findBotMove() {
        List<Integer> listX= new ArrayList<Integer>();
        List<Integer> listY= new ArrayList<Integer>();
        final int range=2;
        for (int i=0; i<maxSize;i++){
            for (int j=0;j<maxSize;j++){
                if (valueCell[i][j]!=0){
                    for (int t=1; t<=range;t++){
                        for (int k=0;k<8;k++){
                            int x = i+iRow[k]*t;
                            int y = j+iCol[k]*t;
                            if(inBoard(x,y) && valueCell[x][y]==0){
                                listX.add(x);
                                listY.add(y);
                            }
                        }
                    }
                }
            }
        }
        int lx = listX.get(0);
        int ly = listY.get(0);
        int res = Integer.MAX_VALUE-10;
        for(int i=0;i<listX.size();i++){
            int x=listX.get(i);
            int y=listY.get(i);
            valueCell[x][y]=2;
            int rr=getValue_Position();
            if (rr<res){
                res=rr;lx=x;ly=y;
            }
            valueCell[x][y]=0;
        }
        xMove=lx;yMove=ly;
    }

    private int getValue_Position() {
        // find  board_position_value
        int rr=0;
        int pl = turn;
        //row
        for (int i=0;i<maxSize;i++){
            rr+=CheckValue(maxSize-1,i,-1,0,pl);
        }
        //collum
        for (int i=0;i<maxSize;i++){
            rr+=CheckValue(i,maxSize-1,0,-1,pl);
        }
        //right to left
        for (int i=maxSize-1;i>=0;i--){
            rr+=CheckValue(i,maxSize-1,-1,-1,pl);
        }
        for (int i=maxSize-2;i>=0;i--){
            rr+=CheckValue(maxSize-1,i,-1,-1,pl);
        }
        //left to right
        for (int i=maxSize-1;i>=0;i--){
            rr+=CheckValue(i,0,-1,-1,pl);
        }
        for (int i=maxSize-2;i>=1;i--){
            rr+=CheckValue(maxSize-1,i,-1,1,pl);
        }
        return rr;
    }

    private int CheckValue(int xd, int yd, int vx, int vy, int pl) {
        int i, j;
        int rr = 0;
        i=xd;
        j=yd;
        String at = String.valueOf(valueCell[i][j]);
        while (true){
            i+=vx;
            j+=vy;
            if(inBoard(i,j)){
                at = at+String.valueOf(valueCell[i][j]);
                if(at.length()==6){
                    rr+=Eval(at,pl);
                    at=at.substring(1,6);
                }
            }else break;
        }
        return rr;
    }

    private void make_a_move() {
//        Log.d("hentai", "make_a_move with " + xMove+", "+yMove+"; "+turn+" ->>> "+valueCell[xMove][yMove]);
        ivCell[xMove][yMove].setImageDrawable(drawCell[turn]);
        valueCell[xMove][yMove] = turn;
        if (noEmptyCell()) {
            Toast.makeText(context,"Draw!!",Toast.LENGTH_SHORT).show();
            return;
        }else if (CheckIfIsWinner()) {
            if (winner == 1) {
                if(playWith == 0){
                    Toast.makeText(context, "Winner is Player", Toast.LENGTH_SHORT).show();
                    tvInfo.setText("Winner is Player");
                } else if (playWith == 1){
                    Toast.makeText(context, "Winner is Player 1", Toast.LENGTH_SHORT).show();
                    tvInfo.setText("Winner is Player 1");
                }
            } else {
                if (playWith == 0){
                    Toast.makeText(context, "Winner is Bot", Toast.LENGTH_SHORT).show();
                    tvInfo.setText("Winner is Bot");
                }else if (playWith == 1){
                    Toast.makeText(context, "Winner is Player 2", Toast.LENGTH_SHORT).show();
                    tvInfo.setText("Winner is Player 2");
                }
            }
            return;
        }
        if(turn==1){
            turn= 2;
            if(playWith == 0) botTurn();
            else player2Turn();
        }else {
            turn= 1;
            playerTurn();
        }
    }

    private boolean CheckIfIsWinner() {
        if(winner!=0) return true;
        //check in row
        VectorEnd(xMove,0,0,1,xMove,yMove);
        //check in collum
        VectorEnd(0,yMove,1,0,xMove,yMove);
        //check left to right
        if(xMove+yMove >= maxSize-1){
            VectorEnd(maxSize -1,xMove+yMove-maxSize+1,-1,1,xMove,yMove);
        }else {
            VectorEnd(xMove+yMove,0,-1,1,xMove,yMove);
        }
        //check right to left
        if (xMove<=yMove){
            VectorEnd(xMove-yMove+maxSize-1,maxSize-1,-1,-1,xMove,yMove);
        }else {
            VectorEnd(maxSize-1,maxSize-1-(xMove-yMove),-1,-1,xMove,yMove);
        }
        if (winner !=0) return true; else return false;
    }

    private void VectorEnd(int xx, int yy, int vx, int vy, int rx, int ry) {
        if(winner!=0)return;
        int i,j;
        final int range = 4;
        // start at pos x=0, y=0
        int xbelow = rx - range * vx; //xleft
        int xabove = rx + range * vx; //xright
        int ybelow = ry - range * vy;
        int yabove = ry + range * vy;
        String at="";
        i=xx;j=yy;
        while (!inside(i,xbelow,xabove) || !inside(j,ybelow,yabove)){
            i+=vx;j+=vy;
        }
        while (true){
            at = at+ String.valueOf(valueCell[i][j]);
            if(at.length() == 5){
                EvalEnd(at);
                at=at.substring(1,5);
            }
            i+=vx;j+=vy;
            if(!inBoard(i,j) || !inside(i,xbelow,xabove) || !inside(j,ybelow,yabove) || winner!=0){
                break;
            }
        }

    }

    private boolean inBoard(int i, int j) {
        // check if i, j in the board
        if(i<0 || i> maxSize-1 || j<0 || j> maxSize-1) return false;
        return true;
    }

    private void EvalEnd(String at) {
        switch (at){
            case "11111": winner=1;break;
            case "22222": winner=2;break;
            default: break;
        }
    }

    private boolean inside(int i, int xbelow, int xabove) {
        // check if i is the same sign with left + right or below + above
        return (i-xbelow)*(i-xabove)<=0;
    }

    private boolean noEmptyCell() {
        for (int i=0;i<maxSize;i++){
            for (int j=0;j<maxSize;j++){
                if(valueCell[i][j]==0) return false;
            }
        }
        return false;
    }

    private void loadResources() {
        drawCell[0] = null;
        drawCell[1] = context.getResources().getDrawable(R.drawable.x);
        drawCell[2] = context.getResources().getDrawable(R.drawable.o);
        drawCell[3] = context.getResources().getDrawable(R.drawable.cell_background);
    }

//    @SuppressLint("NewApi")
//    Listen all the click on the board game
    private void designBoardGame() {
        //optimize board for screen size with maxSize
        int sizeOfCell = Math.round(ScreenSize()/maxSize);
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams( sizeOfCell*maxSize, sizeOfCell);
        LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(sizeOfCell,sizeOfCell);

        LinearLayout linBoardGame = findViewById(R.id.board);

        for (int i=0;i<maxSize;i++){
            LinearLayout linRow = new LinearLayout(context);

            for (int j=0;j<maxSize;j++){
                ivCell[i][j]= new ImageView(context);
                //empty background cell for default
                ivCell[i][j].setBackground(drawCell[3]);
                final int x =i;
                final int y =j;
                ivCell[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(valueCell[x][y] == 0){
                            if(turn==1 || !isClicked){
//                                Log.d("hentai", "onClick: on click cell");
                                isClicked=true;
                                xMove=x;yMove=y;
                                make_a_move();
                            }
                        }
                    }
                });
                linRow.addView(ivCell[i][j], lpCell);
            }
            linBoardGame.addView(linRow, lpRow);
        }
    }
    //get screen size
    private float ScreenSize() {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }
    // this function helps bot choose best move
    private int Eval(String at, int pl){
        int b1=1, b2=1;
        if(pl == 1){
            b1 = 2;
            b2 = 1;
        }else {
            b1 = 1;
            b2 = 2;
        }
        switch (at){
            case "111110": return b1*100000000;
            case "011111": return b1*100000000;
            case "211111": return b1*100000000;
            case "111112": return b1*100000000;
            case "011110": return b1*10000000;
            case "101110": return b1*1002;
            case "011101": return b1*1002;
            case "011112": return b1*1000;
            case "011100": return b1*102;
            case "001110": return b1*102;
            case "210111": return b1*100;
            case "211110": return b1*100;
            case "211011": return b1*100;
            case "211101": return b1*100;
            case "010100": return b1*10;
            case "011000": return b1*10;
            case "001100": return b1*10;
            case "000110": return b1*10;
            case "211000": return b1*1;
            case "201100": return b1*1;
            case "200110": return b1*1;
            case "200011": return b1*1;

            case "222220": return b2*-100000000;
            case "022222": return b2*-100000000;
            case "122222": return b2*-100000000;
            case "222221": return b2*-100000000;
            case "022220": return b2*-10000000;
            case "202220": return b2*-1002;
            case "022202": return b2*-1002;
            case "022221": return b2*-1000;
            case "022200": return b2*-102;
            case "002220": return b2*-102;
            case "120222": return b2*-100;
            case "122220": return b2*-100;
            case "122022": return b2*-100;
            case "122202": return b2*-100;
            case "020200": return b2*-10;
            case "022000": return b2*-10;
            case "002200": return b2*-10;
            case "000220": return b2*-10;
            case "122000": return b2*-1;
            case "102200": return b2*-1;
            case "100220": return b2*-1;
            case "100022": return b2*-1;
            default:break;
        }
        return 0;
    }


}
