package com.figer.game;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.figer.game.bluetooth.BluetoothService;
import com.figer.game.game.Card;
import com.figer.game.game.CardCollection;
import com.figer.game.game.Indicator;
import com.figer.game.gui.Button;
import com.figer.game.gui.List;
import com.figer.game.gui.Signal;
import com.figer.game.stage.Stage;
import com.figer.game.stage.StageManager;
import com.figer.game.system.GameConstants;
import com.figer.game.system.Input;
import com.figer.game.system.Renderer;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Pattern;

public class GameStage extends Stage {
    private Button btnEnable;
    private Button btnEnableDiscover;
    private Button btnHost;
    private Button btnClient;
    private Button btnConnect;
    private ArrayList<BluetoothDevice> deviceNames;
    private List deviceList;
    private Indicator btIndicator;
    private Indicator hostIndicator;
    private Indicator clientIndicator;
    private Indicator connectedIndicator;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    Context context;

    private BluetoothService bluetoothService;
    private BluetoothDevice mBTDevice;
    private BluetoothAdapter mBluetoothAdapter;

    private boolean isHost;
    private boolean isClient;


    //GAME VARIABLES
    private Button btnReady;
    private Button btnYourMon;
    private Button btnEnemyMon;

    private int state;
    private int compNum;
    private int compType;
    private int compDir;

    private int yourScore = 0;
    private int enemyScore = 0;

    //Card Vars
    Array<String> allCards = new Array<String>();
    int place = 0;
    int chosenCard;
    String card;
    Card selectedCard;
    Card enemyCardBack;
    Card enemyCardFlip;
    int removedCardIndex;

    //Your Card Vars
    private CardCollection yourHand;
    int yourDeckSize = 30;
    int yourHandSize = 30;

    //Enemy Card Vars
    private CardCollection enemyHand;
    Array<String> enemyDeck = new Array<String>();
    int enemyDeckSize = 0;
    int enemyHandSize = 0;
    String enemyCardName = "a";

    //Message Handling
    String incMsg = "";
    String realMsg = "";
    String sendData;
    String[] data;

    int messageState;
    int handState;

    boolean flip = false;

    Timer timer;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println("ok");
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceNames.add(device);
                deviceList.addElement(device.getName());
            } else {
            }
        }
    };

    public void discoverDevices(){
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();

            hostIndicator.setName("lampRed");
            clientIndicator.setName("lampRed");

            mBluetoothAdapter.startDiscovery();
            if(isHost){
                hostIndicator.setName("lampGreen");
            } else {
                clientIndicator.setName("lampGreen");
            }

            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(mReceiver, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.startDiscovery();
            if(isHost){
                hostIndicator.setName("lampGreen");
            } else {
                clientIndicator.setName("lampGreen");
            }

            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(mReceiver, discoverDevicesIntent);
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameStage(StageManager stageManager, Context context){
        super(stageManager);
        this.context = context;

        state = GameConstants.PHASE_CONNECT;
        isHost = false;
        isClient = false;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothService = new BluetoothService(context);
        //bluetoothService = new BluetoothService(context);

        btnEnable = new Button(25,50,200,50,"On/Off");
        btnEnableDiscover = new Button(25,150,200,50,"Enable Discoverability");
        btnHost = new Button(25, 250, 200,50,"Host");
        btnClient = new Button(25, 350, 200, 50, "Client");
        btnConnect = new Button(25,450,200,50,"Connect");

        btIndicator = new Indicator("lampRed", 250, 50);
        if(mBluetoothAdapter.isEnabled()){
            btIndicator = new Indicator("lampGreen", 250, 50);
        }
        timer = new Timer();
        sendData = "";
        messageState = 0;
        handState = 0;
        removedCardIndex = 5;

        hostIndicator = new Indicator("lampRed", 250, 250);
        clientIndicator = new Indicator("lampRed", 250, 350);
        connectedIndicator = new Indicator("lampRed", 700, 500);

        deviceNames = new ArrayList<BluetoothDevice>();
        deviceList = new List(300,50,200,50);

        //GAME
        //CARD RELATED
        enemyCardBack = new Card("sadPepe");

        yourHand = new CardCollection();
        enemyHand = new CardCollection();

        compNum = 0;
        compType = 0;
        compDir = 0;

        allCards.add("newChallenger");
        allCards.add("sadPepe");
        allCards.add("loss");
        allCards.add("article13");
        allCards.add("markZuckerberg");
        allCards.add("imGay");
        allCards.add("skrattarDu");
        allCards.add("pewdiepie");
        allCards.add("ugandanKnuckles");
        allCards.add("twitchThot");

        //Get hand from deck
        for(int i=0; i<5; i++){
            Random random = new Random();
            int rand = random.nextInt(9) + 1; new Random();
            yourHand.addCard(allCards.get(rand),(i*98) + 100, 450, i);
            yourDeckSize--;
            yourHandSize++;
        }

        //Get EnemyHand from EnemyDeck
        for(int i=0; i<5; i++){
            enemyHand.addCard("cardBack",(i*98) + 100, 15, i);
            enemyDeckSize--;
            enemyHandSize++;
        }

        //GUI ELEMENTS
        btnReady = new Button(GameConstants.GAME_WIDTH-101, GameConstants.GAME_HEIGHT/2, 100, 25, "Ready");
        btnYourMon = new Button(GameConstants.GAME_WIDTH-600, GameConstants.GAME_HEIGHT/2-64, 96, 128,"YourMon");
        btnEnemyMon = new Button(GameConstants.GAME_WIDTH-300, GameConstants.GAME_HEIGHT/2-64, 96, 128,"EnemyMon");
    }

    @Override
    public void draw(Renderer renderer) {
        while(flip) {
            enemyCardFlip.draw(renderer);
        }

        if(bluetoothService.isConnected()) {
            btnReady.draw(renderer);
            btnYourMon.draw(renderer);
            btnEnemyMon.draw(renderer);

            yourHand.draw(renderer);
            enemyHand.draw(renderer);
            enemyCardBack.draw(renderer);

            renderer.drawText(intoString(yourDeckSize), 700, 500, Renderer.WHITE);
            renderer.drawText(intoString(enemyDeckSize), 100, 100, Renderer.WHITE);
            renderer.drawText(intoString(compNum), GameConstants.GAME_WIDTH / 2, GameConstants.GAME_HEIGHT / 2 - 15, Renderer.WHITE);
            renderer.drawText(intoString(yourScore), 50, 550, Renderer.WHITE);
            renderer.drawText(intoString(enemyScore), 750, 50, Renderer.WHITE);
            switch(compType){
                case 0:
                    renderer.drawText("HP", GameConstants.GAME_WIDTH/2, GameConstants.GAME_HEIGHT/2, Renderer.RED);
                    break;
                case 1:
                    renderer.drawText("DAMAGE", GameConstants.GAME_WIDTH/2, GameConstants.GAME_HEIGHT/2, Renderer.OLIVE);
                    break;
            }
            switch(compDir){
                case 0:
                    renderer.drawText("LESSER", GameConstants.GAME_WIDTH/2, GameConstants.GAME_HEIGHT/2+15, Renderer.BLUE);
                    break;
                case 1:
                    renderer.drawText("MORE", GameConstants.GAME_WIDTH/2, GameConstants.GAME_HEIGHT/2+15, Renderer.BLUE);
                    break;
            }
        } else {
            btnEnable.draw(renderer);
            btnEnableDiscover.draw(renderer);
            btnHost.draw(renderer);
            btnClient.draw(renderer);
            btnConnect.draw(renderer);

            btIndicator.draw(renderer);
            hostIndicator.draw(renderer);
            clientIndicator.draw(renderer);
            connectedIndicator.draw(renderer);

            deviceList.draw(renderer);
        }
    }

    @Override
    public void update(Input input) {
        if(bluetoothService.isConnected()){
            btnReady.update(input);
            btnYourMon.update(input);
            btnEnemyMon.update(input);

            yourHand.update(input);
            yourHand.sort();
            enemyHand.update(input);
            enemyHand.sort();
        } else {
            btnEnable.update(input);
            btnEnableDiscover.update(input);
            btnHost.update(input);
            btnClient.update(input);
            btnConnect.update(input);

            deviceList.update(input);
        }

        incMsg = bluetoothService.getIncomingMessage();
        String tmpMsg = incMsg;

        if(realMsg.equals(tmpMsg)){
            tmpMsg = "";
            gameLogic(tmpMsg);
        } else {
            realMsg = tmpMsg;
            System.out.println("realMsg: " + realMsg);
            gameLogic(realMsg);
        }

        if((bluetoothService.isConnected()) && (state == GameConstants.PHASE_CONNECT)){
            if(isHost){
                state = GameConstants.PHASE_GENERATE;
            } else {
                state = GameConstants.PHASE_READ;
            }
        }

        //BUTTON HANDLING
        if(btnEnable.consumeSignal() != Signal.NULL){
            bluetoothService.enableBluetooth();
            if(bluetoothService.isBtOn()){
                btIndicator.setName("lampGreen");
            } else {
                btIndicator.setName("lampRed");
            }

        }
        if(btnEnableDiscover.consumeSignal() != Signal.NULL){
            bluetoothService.enableDiscoverability();
        }
        if(btnHost.consumeSignal() != Signal.NULL){
            isHost = true;
            isClient = false;
            bluetoothService.enableDiscoverability();
            discoverDevices();

        }
        if(btnClient.consumeSignal() != Signal.NULL){
            isClient = true;
            isHost = false;
            bluetoothService.enableDiscoverability();
            discoverDevices();

        }
        if(deviceList.consumeSignal() != Signal.NULL){
            mBTDevice = deviceNames.get(deviceList.getSelectedIndex());
            mBluetoothAdapter.cancelDiscovery();
            hostIndicator.setName("lampRed");

                mBTDevice.createBond();
                System.out.println("In");
                bluetoothService = new BluetoothService(context);
                System.out.println("it SHOULD work");
                bluetoothService.startClient(mBTDevice, MY_UUID_INSECURE);
        }

        //GAME LOGIC
    }

    @Override
    public void dispose() {
        context.unregisterReceiver(mReceiver);
        //DON'T FUCKING DARE TO UNCOMMENT THIS, STOPS BLUETOOTH_SERVICE
//        mBluetoothAdapter.disable();
//        bluetoothService.stop();
//        unpairDevice(mBTDevice);
    }

    @Override
    public void onActivating() {

    }

    @Override
    public void onDeactivating() {

    }

    public String intoString(int number){
        return Integer.toString(number);
    }

    public int stringInt(String s){
        return Integer.parseInt(s);
    }

    public void sendMessage(String message){
        byte[] bytes = message.getBytes(Charset.defaultCharset());
        bluetoothService.write(bytes);
    }

    public void readMessage(String message){
        if(message.contains("compare")){
            messageState = 1;
        }
        if(message.contains("draw")){
            messageState = 2;
        }
        if(message.contains("card")){
            messageState = 3;
        }
        if(message.contains("flip")){
            messageState = 4;
        }

        switch(messageState){
            case 1:
                data = message.split(Pattern.quote("$"));
                compNum = stringInt(data[1]);
                compType = stringInt(data[2]);
                compDir = stringInt(data[3]);
                state = GameConstants.PHASE_READ;
                break;
            case 2:
                enemyDeckSize--;
                enemyHandSize++;
                break;
            case 3:
                data = message.split(Pattern.quote("$"));
                enemyCardName = data[1];

                state = GameConstants.PHASE_DRAW_CARD;
                break;
            case 4:
                data = message.split(Pattern.quote("$"));
                enemyCardName = data[1];

                state = GameConstants.PHASE_FLIP;
                break;
            case 0:
                break;
        }
    }

    public void gameLogic(String message){
        switch(state){
            case GameConstants.PHASE_CONNECT:
                break;
            case GameConstants.PHASE_GENERATE:
                if(isHost) {
                    compNum = MathUtils.random(8) + 1;
                    compType = MathUtils.random(1);
                    compDir = MathUtils.random(1);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            String sendData = "compare" + "$" + intoString(compNum) + "$" + intoString(compType) + "$" + intoString(compDir);
                            sendMessage(sendData);
                        }
                    }, 2 * 60 * 10);

                    state = GameConstants.PHASE_DRAW_CARD;
                } else {
                    state = GameConstants.PHASE_READ;
                }
                break;
            case GameConstants.PHASE_DRAW_CARD:
                place = removedCardIndex;
                System.out.println("drawCard: " + removedCardIndex);
                Random random = new Random();
                int rand = random.nextInt(allCards.size) + 1; new Random();
                if(yourDeckSize >= 1) {
                    if(yourHandSize == 10){
                        yourDeckSize--;
                    } else {
                        yourHand.addCard(allCards.get(rand), (place*98) + 100, 450, place);
                        yourDeckSize--;
                        yourHandSize++;
                    }
                }

                yourHand.getList();
                String sendData = "draw";
                sendMessage(sendData);

                state = GameConstants.PHASE_GAME;
                break;
            case GameConstants.PHASE_GAME:
                if(isHost){
                    chosenCard = yourHand.getChosenCard();

                    if((chosenCard != -1) && (btnYourMon.consumeSignal() != Signal.NULL)){
                        selectedCard = yourHand.getCard(chosenCard);
                        selectedCard.setX(btnYourMon.getX());
                        selectedCard.setY(btnYourMon.getY());
                        yourHandSize--;
                    }

                    if((chosenCard != -1) && (btnReady.consumeSignal() != Signal.NULL)){
                        sendData = "card" + "$" + selectedCard.getId();
                        sendMessage(sendData);

                        state = GameConstants.PHASE_READ;
                    }
                } else {
                    chosenCard = yourHand.getChosenCard();

                    if((chosenCard != -1) && (btnYourMon.consumeSignal() != Signal.NULL)){
                        selectedCard = yourHand.getCard(chosenCard);
                        selectedCard.setX(btnYourMon.getX());
                        selectedCard.setY(btnYourMon.getY());
                        yourHandSize--;
                    }

                    if((chosenCard != -1) && (btnReady.consumeSignal() != Signal.NULL)){
                        sendData = "flip" + "$" + selectedCard.getId();
                        sendMessage(sendData);

                        chosenCard = -1;
                        state = GameConstants.PHASE_FLIP;
                    }
                }
                break;
            case GameConstants.PHASE_FLIP:
                if(!enemyCardName.equals("")){
                    enemyCardFlip = new Card(enemyCardName, btnEnemyMon.getX(), btnEnemyMon.getY(), 0);

                    state = GameConstants.PHASE_EVALUATE;
                } else {
                    System.out.println("fuck cards");
                }
                break;
            case GameConstants.PHASE_EVALUATE:
                switch(compType){
                    case 0:
                        switch(compDir){
                            case 0:
                                if(stringInt(selectedCard.getHp()) < stringInt(enemyCardFlip.getHp())){
                                    yourScore++;
                                } else if(stringInt(selectedCard.getHp()) > stringInt(enemyCardFlip.getHp())){
                                    enemyScore++;
                                }
                                break;
                            case 1:
                                if(stringInt(selectedCard.getHp()) > stringInt(enemyCardFlip.getHp())){
                                    yourScore++;
                                } else if(stringInt(selectedCard.getHp()) < stringInt(enemyCardFlip.getHp())){
                                    enemyScore++;
                                }
                                break;
                        }
                        break;
                    case 1:
                        switch(compDir){
                            case 0:
                                if(stringInt(selectedCard.getDmg()) < stringInt(enemyCardFlip.getDmg())){
                                    yourScore++;
                                } else if(stringInt(selectedCard.getDmg()) > stringInt(enemyCardFlip.getDmg())){
                                    enemyScore++;
                                }
                                break;
                            case 1:
                                if(stringInt(selectedCard.getDmg()) > stringInt(enemyCardFlip.getDmg())){
                                    yourScore++;
                                } else if(stringInt(selectedCard.getDmg()) < stringInt(enemyCardFlip.getDmg())){
                                    enemyScore++;
                                }
                                break;
                        }
                        break;
                }

                chosenCard = -1;
                removedCardIndex = selectedCard.getPlace();
                yourHand.removeCard(removedCardIndex);
                compNum=0;
                compType=0;
                compDir=0;
                flip = false;
                enemyCardFlip.setX(-96);
                enemyCardFlip.setY(-128);
                selectedCard.setX(-96);
                selectedCard.setY(-128);

                System.out.println("evaluate: " + selectedCard.getPlace());

                if((yourScore == 5) || (enemyScore == 5)){
                    state = GameConstants.PHASE_GAME_OVER;
                } else {
                    state = GameConstants.PHASE_GENERATE;
                }
                break;
            case GameConstants.PHASE_READ:
                if(message.isEmpty()){
                    //System.out.println("msgEmpty");
                } else {
                    readMessage(message);
                }
                break;
            case GameConstants.PHASE_GAME_OVER:
                break;
        }
    }
}
