
package simulator.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import simulator.dao.FileTMDao;
import simulator.dao.TMDao;
import simulator.domain.Handler;

public class UI extends Application {
    
    private Handler handle;
    private int canvasWidth = 803;
    private int canvasHeight = 200;
    private int mainWindowWidth = 1105;
    private int mainWindowHeight = 450;
    private int creationWindowWidth = 1050;
    private int creationWindowHeight = 650;
    private int drawerFontSize = 14;
    private int tapeLength = 49;
    private double[] headX = new double[]{(canvasWidth/2) + 2, (canvasWidth/2) + 9, (canvasWidth/2) + 16};
    private double[] headY = new double[]{78,90,78};
    private int inputLimit = 5000000;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        TMDao tmdao = new FileTMDao();
        handle = new Handler(tmdao);
    }
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("Turing Machine Simulator");
        stage.setMaxWidth(mainWindowWidth);
        stage.setMaxHeight(mainWindowHeight);
        stage.setResizable(false);
        Stage creationWindow = new Stage();
        creationWindow.setMaxWidth(creationWindowWidth);
        creationWindow.setMaxHeight(creationWindowHeight);
        creationWindow.setResizable(false);
        
        //creates elements for the main scene
        //menu buttons at the top
        Button neww = new Button("Create");
        Button open = new Button("Open");
        ToggleGroup choises = new ToggleGroup();
        RadioButton atonce = new RadioButton("Simulate without showing steps in-between");
        atonce.setSelected(true);
        atonce.setToggleGroup(choises);
        RadioButton manually = new RadioButton("Simulate manually");
        manually.setToggleGroup(choises);
        VBox fast = new VBox();
        fast.setSpacing(10);
        RadioButton fastsbs = new RadioButton("Simulate automatically");
        fastsbs.setToggleGroup(choises);
        Slider howFast = new Slider(1,4,1);
        howFast.setShowTickMarks(true);
        howFast.setShowTickLabels(true);
        howFast.setSnapToTicks(true);
        howFast.setMajorTickUnit(1);
        howFast.setMinorTickCount(0);
        howFast.setLabelFormatter(new StringConverter<Double>(){
            
            @Override
            public String toString(Double d){
                if(d < 2) return "Slow";
                if(d < 3) return "Normal";
                if(d < 4) return "Fast";
                else {
                    return "Very fast";
                }
            }

            @Override
            public Double fromString(String string) {
                if(string.equals("Slow")) return 1d;
                if(string.equals("Normal")) return 2d;
                if(string.equals("Fast")) return 3d;
                else {
                    return 4d;
                }
            }
            
        });
        fast.getChildren().addAll(fastsbs, howFast);
        HBox menu = new HBox();
        menu.getChildren().addAll(neww, open, atonce, manually, fast);
        menu.setPadding(new Insets(10,10,2,60));
        menu.setSpacing(15);
        //place for output, input and simulate-button at the middle
        VBox simulationArea = new VBox();
        simulationArea.setAlignment(Pos.CENTER_RIGHT);
        simulationArea.setPadding(new Insets(0,20,20,20));
        simulationArea.setSpacing(10);
        Label tmname = new Label("");
        tmname.setFont(new Font("Arial",18));
        
        Canvas canvas = new Canvas(canvasWidth,canvasHeight);
        GraphicsContext drawer = canvas.getGraphicsContext2D();
        drawer.setFill(Color.WHITE);
        drawer.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawer.setFill(Color.LAVENDER);
        drawer.fillRoundRect(401, 93, 18, 24, 5, 5);
        drawer.setFill(Color.BLACK);
        drawer.setFont(Font.font(java.awt.Font.MONOSPACED,drawerFontSize));
        StringBuilder tape = new StringBuilder();
        for(int i = 0; i < tapeLength; i++){
            tape.append("_ ");
        }
        drawer.strokeText(tape.toString(), 1, 110);
        drawer.fillPolygon(headX, headY, 3);
        drawer.strokeText("Steps: ", 25, 30);
        
        HBox inputLine = new HBox();
        inputLine.setSpacing(8);
        inputLine.setAlignment(Pos.CENTER_RIGHT);
        Label linput = new Label("Input: ");
        TextField input = new TextField();
        input.setPrefWidth(345);
        Label lstepLimit = new Label("Step limit: ");
        TextField stepLimit = new TextField();
        stepLimit.setText("1000000");
        stepLimit.setPrefWidth(100);
        Label ltapeSize = new Label("Tape size limit: ");
        TextField tapeSize = new TextField("");
        tapeSize.setText("3000000");
        tapeSize.setPrefWidth(100);
        inputLine.getChildren().addAll(ltapeSize,tapeSize,lstepLimit,stepLimit,linput,input);
        HBox startButtons = new HBox();
        startButtons.setSpacing(10);
        startButtons.setAlignment(Pos.CENTER_RIGHT);
        Button start = new Button("Start simulation");
        startButtons.getChildren().add(start);
        start.setDisable(true);
        Button simulateStep = new Button("Simulate step");
        HBox results = new HBox();
        results.setAlignment(Pos.CENTER_RIGHT);
        Label lresult = new Label("Result: ");
        Label result = new Label("");
        results.getChildren().addAll(lresult,result);
        simulationArea.getChildren().addAll(tmname,canvas,inputLine,results,startButtons);
        //name, description, alphabet and states listed on the right
        VBox information = new VBox();
        information.setPadding(new Insets(0,20,20,0));
        information.setSpacing(10);
        information.setAlignment(Pos.CENTER_LEFT);
        Label currentTMname = new Label("");
        currentTMname.setWrapText(true);
        currentTMname.setMaxWidth(200);
        currentTMname.setPrefWidth(200);
        Label currentTMdescription = new Label("");
        currentTMdescription.setWrapText(true);
        currentTMdescription.setMaxWidth(200);
        Label currentTMalphabet = new Label("");
        currentTMalphabet.setWrapText(true);
        currentTMalphabet.setMaxWidth(200);
        information.getChildren().addAll(currentTMname, currentTMdescription, currentTMalphabet);
        //overall layout
        BorderPane layout = new BorderPane();
        layout.setTop(menu);
        layout.setCenter(simulationArea);
        layout.setRight(information);
        
        AnimationTimerExtra loopF = new AnimationTimerExtra() {
            
            long previous = 0;
            long interval = 0;
            boolean first = true;
            
            @Override
            public void setInterval(long interval) {
                this.interval = interval * 1000000;
            }
            
            @Override
            public void resetFirst(){
                this.first = true;
            }
            
            @Override
            public void handle(long now) {
                if((now - previous) < interval){
                    return;
                }
                if(first){
                    drawer.strokeText(handle.getTape(), 1, 110);
                    drawer.strokeText(handle.getState(), 405, 70);
                    drawer.fillPolygon(headX, headY, 3);
                    drawer.strokeText("Steps: " + handle.getSteps(), 25, 30);
                    this.previous = now;
                    first = false;
                    return;
                }
                String step = handle.simulateStep();
                if(step.equals("Accepted") || step.equals("Rejected") || step.equals("Tape limit exceeded.") || step.equals("Undefined character and state combination.") || step.equals("Bad input for this machine.")){
                    result.setText(step);
                    this.stop();
                    first = true;
                    return;
                } else if (step.equals("Turing machine did not halt after")){
                    result.setText(step + " " + stepLimit.getText() + " steps.");
                    this.stop();
                    first = true;
                    return;
                }
                drawer.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                drawer.setFill(Color.WHITE);
                drawer.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                drawer.setFill(Color.LAVENDER);
                drawer.fillRoundRect(401, 93, 18, 24, 5, 5);
                drawer.setFill(Color.BLACK);
                drawer.strokeText(step, 1, 110);
                drawer.strokeText(handle.getState(), 405, 70);
                drawer.fillPolygon(headX, headY, 3);
                drawer.strokeText("Steps: " + handle.getSteps(), 25, 30);
                this.previous = now; 
            }
            
        };
        
        Scene main = new Scene(layout, mainWindowWidth, mainWindowHeight);
        
        //creates elements and sets the creation scene
        //name-row
        Label lname = new Label("Name: ");
        TextField tfname = new TextField();
        tfname.setMaxWidth(200);
        //description-row
        Label desc = new Label("Description: ");
        TextArea tadesc = new TextArea();
        tadesc.setPrefWidth(400);
        tadesc.setMaxWidth(400);
        //gridpane for the elements above
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(10,10,10,10));
        gp.setHgap(10);
        gp.setVgap(10);
        gp.add(lname, 0, 0);
        gp.add(tfname, 1, 0);
        gp.add(desc, 0, 1);
        gp.add(tadesc, 1, 1);
        //instructions
        Label inst = new Label("Instructions:"
                            + "\nSimulator uses \"_\" as a blank symbol. \"qa\" is the accepting state and \"qr\" is the rejecting state (these are not acceptable names for your own states). "
                            + "Possible movements are left, right and no movement written as L, R or N.\n" 
                            + "\nThe first row is for the alphabet, enter one character into one cell."
                            + "\nThe first column is for naming the states, the first state will be used as the initial state.\n"
                            + "\nThe other cells are for the instructions: give the instruction for the corresponding state of the row and the character of the column."
                            + "\nGive instructions in the following order separated by space: "
                            + "\ncharacter movement state."
                            + "\nFor example: "
                            + "\na R qar"
                            + "\nwhere 'a' is the character, 'R' is the movement and 'qar' is the state.");
        inst.setWrapText(true);
        inst.setMaxWidth(500);
        inst.setPrefWidth(500);
        //transition table
        GridPane table = new GridPane();
        table.setGridLinesVisible(true);
        ArrayList<TextField> nodes = new ArrayList<>();
        ArrayList<TextField> stateslist = new ArrayList<>();
        ArrayList<TextField> alphablist = new ArrayList<>();
        TextField blankSymbol = new TextField();
        int rows  = 3;
        int columns = 4;
        char def = 'a';
        for(int i = 1; i < columns; i++){
            if(i == 1){
                blankSymbol.setText("_");
                blankSymbol.setEditable(false);
                blankSymbol.setPrefWidth(70);
                table.add(blankSymbol, i, 0);
                alphablist.add(blankSymbol);
            } else {
                TextField tf1 = new TextField();
                tf1.setPrefWidth(70);
                tf1.setText(def + "");
                table.add(tf1, i, 0);
                alphablist.add(tf1);
                def++;
            }
        }
        for(int j = 1; j < rows; j++){
            TextField tf1 = new TextField();
            tf1.setPrefWidth(70);
            tf1.setText("state" + j);
            table.add(tf1, 0, j);
            stateslist.add(tf1);
        }
        for(int i = 1; i < columns; i++){
            for(int j = 1; j < rows; j++){
                TextField tf1 = new TextField();
                tf1.setPrefWidth(70);
                table.add(tf1, i, j);
                nodes.add(tf1);
                
            }
        }
        //transition table related buttons and labels
        Label transitionTable = new Label("Transition table: ");
        transitionTable.setFont(new Font("Arial",15));
        transitionTable.setPadding(new Insets(10,10,10,0));
        Button addColumn = new Button("Add Column");
        Button addRow = new Button("Add Row");
        Button removeColumn = new Button("Remove Column");
        Button removeRow = new Button("Remove Row");
        Label initialState = new Label("(initial state)");
        initialState.setPadding(new Insets(30,0,0,0));
        HBox h3 = new HBox();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(table);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setMaxWidth(368);
        h3.getChildren().addAll(initialState, scrollPane);
        h3.setSpacing(10);
        HBox h2 = new HBox();
        h2.setPadding(new Insets(10,10,10,0));
        h2.setSpacing(10);
        h2.getChildren().addAll(addRow, removeRow, addColumn, removeColumn);
        //button-row and error label
        Button finish = new Button("Finish");
        Button cancel = new Button("Cancel");
        Label error = new Label("");
        error.setTextFill(Color.RED);
        HBox buttons = new HBox();
        buttons.setSpacing(10);
        buttons.getChildren().addAll(finish, cancel);
        //overall layout of creation scene
        VBox v = new VBox();
        v.setPadding(new Insets(10,10,10,10));
        v.setSpacing(10);
        v.getChildren().addAll(gp,inst,error,buttons);
        VBox v2 = new VBox();
        v2.setPadding(new Insets(10,10,10,0));
        v2.setSpacing(10);
        v2.getChildren().addAll(transitionTable,h2,h3);
        HBox overall = new HBox();
        overall.setPadding(new Insets(10,10,10,10));
        overall.setSpacing(10);
        overall.getChildren().addAll(v, v2);
        
        Scene creation = new Scene(overall, creationWindowWidth, creationWindowHeight);
        
        //"Start simulation" -button starts the simulation
        start.setOnAction((event) -> {
            result.setText("");
            String stepLimitInt = stepLimit.getText().trim();
            String tapeSizeInt = tapeSize.getText().trim();
            int limit = format(stepLimitInt);
            int tapeLimit = format(tapeSizeInt);
            String inputt = input.getText().trim();
            int inputSize = inputt.length();
            if(inputSize > inputLimit){
                result.setText("Input is too large.");
                return;
            }
            if(tapeLimit < 500){
                tapeLimit = 500;
                tapeSize.setText("" + tapeLimit);
            }
            drawer.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            drawer.setFill(Color.WHITE);
            drawer.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            drawer.setFill(Color.LAVENDER);
            drawer.fillRoundRect(401, 93, 18, 24, 5, 5);
            drawer.setFill(Color.BLACK);
            if(startButtons.getChildren().contains(simulateStep)){
                startButtons.getChildren().remove(simulateStep);
            }
            loopF.stop();
            loopF.resetFirst();
            
            if(atonce.isSelected()){
                drawer.strokeText(tape.toString(), 1, 110);
                drawer.fillPolygon(headX, headY, 3);
                String resultt = handle.simulate(inputt, limit, tapeLimit);
                drawer.strokeText("Steps: " + handle.getSteps(), 25, 30);
                if(resultt.equals("Terminated after")){
                    result.setText(resultt + " " + limit + " steps.");
                } else {
                    result.setText(resultt);
                }
            }
            if(manually.isSelected()){
                if(handle.setUpStepByStep(inputt, limit, tapeLimit)){
                    drawer.strokeText(handle.getTape(), 1, 110);
                    drawer.strokeText(handle.getState(), 405, 70);
                    drawer.fillPolygon(headX, headY, 3);
                    drawer.strokeText("Steps: " + handle.getSteps(), 25, 30);
                    startButtons.getChildren().add(simulateStep);
                } else {
                    result.setText("Input size exceeds tape limit.");
                }
            }
            if(fastsbs.isSelected()){
                if(handle.setUpStepByStep(inputt, limit, tapeLimit)){
                    int level = (int) howFast.getValue();
                    if(level == 1){
                        level = 1000;
                    } else if (level == 2){
                        level = 200;
                    } else if (level == 3){
                        level = 20;
                    } else {
                        level = 5;
                    }
                    loopF.setInterval(level);
                    loopF.start();
                } else {
                    result.setText("Input size exceeds tape limit.");
                }
            }
        });
        
        //"Simulate step" -button simulates a step
        simulateStep.setOnAction((event) -> {
            String step = handle.simulateStep();
            int counter = handle.getSteps();
                if(step.equals("Accepted") || step.equals("Rejected") || step.equals("Tape limit exceeded.") || step.equals("Undefined character and state combination.") || step.equals("Bad input for this machine.")){
                    result.setText(step);
                    startButtons.getChildren().remove(simulateStep);
                    return;
                } else if (step.equals("Turing machine did not halt after")){
                    result.setText(step + " " + stepLimit.getText() + " steps.");
                    startButtons.getChildren().remove(simulateStep);
                    return;
                }
                drawer.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                drawer.setFill(Color.WHITE);
                drawer.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                drawer.setFill(Color.LAVENDER);
                drawer.fillRoundRect(401, 93, 18, 24, 5, 5);
                drawer.setFill(Color.BLACK);
                drawer.strokeText(step, 1, 110);
                drawer.strokeText(handle.getState(), 405, 70);
                drawer.fillPolygon(headX, headY, 3);
                drawer.strokeText("Steps: " + counter, 25, 30);
        });
        
        //"Create"-button: moves to the creation scene
        neww.setOnAction((event) -> {
            creationWindow.setTitle("Create a Turing Machine");
            creationWindow.setScene(creation);
            blankSymbol.setText("_");
            
            creationWindow.show();
        });
        
        //"Open" -button: opens a file chooser
        open.setOnAction((event) -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Choose Turing Machine");
            fc.setInitialDirectory(new File(handle.getProjectFolder()));
            File f = fc.showOpenDialog(stage);
            if(f != null){
                handle.setUpTM(f);
                tmname.setText(handle.getCurrentTMName());
                currentTMname.setText("Name: " + handle.getCurrentTMName());
                currentTMdescription.setText("Description: " + handle.getCurrentTMDescription());
                currentTMalphabet.setText("Alphabet: " + handle.getCurrentTMAlphabet());
                start.setDisable(false);
            }
        });
        
        //"Add Row" -button
        addRow.setOnAction((event) -> {
            int columnCount = table.getColumnCount();
            Node[] nodess = new Node[columnCount];
            for(int i = 0; i < columnCount; i++){
                TextField tf1 = new TextField();
                tf1.setPrefWidth(70);
                nodess[i] = tf1;
                if(i == 0) stateslist.add(tf1);
                else { nodes.add(tf1); }
            }
            int rowCount = table.getRowCount();
            table.addRow(rowCount, nodess);
        });
        
        //"Add Column" -button
        addColumn.setOnAction((event) -> {
            int rowCount = table.getRowCount();
            Node[] nodess = new Node[rowCount];
            for(int i = 0; i < rowCount; i++){
                TextField tf1 = new TextField();
                tf1.setPrefWidth(70);
                nodess[i] = tf1;
                if(i == 0) alphablist.add(tf1);
                else { nodes.add(tf1); }
            }
            int columnCount = table.getColumnCount();
            table.addColumn(columnCount, nodess);
            if(columnCount >= 5){
                table.setPadding(new Insets(0,0,17,0));
            }
        });
        
        //"Remove Row" -button
        removeRow.setOnAction((event) -> {
            ObservableList<Node> children = table.getChildren();
            int rowCount = table.getRowCount();
            if(rowCount <= 2) return;
            Node[] nodess = new Node[table.getColumnCount()];
            int idx = 0;
            for(Node n: children){
                if(GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) == (rowCount - 1)) {
                    nodess[idx] = n;
                    idx++;
                    if(GridPane.getColumnIndex(n) == 0) {
                        stateslist.remove((TextField) n);
                    }
                    else { nodes.remove((TextField) n); }
                }
            }
            table.getChildren().removeAll(nodess);
        });
        
        //"Remove Column" -button
        removeColumn.setOnAction((event) -> {
            ObservableList<Node> children = table.getChildren();
            int columnCount = table.getColumnCount();
            if(columnCount <= 2) return;
            if(columnCount <= 6){
                table.setPadding(new Insets(0,0,0,0));
            }
            Node[] nodess = new Node[table.getRowCount()];
            int idx = 0;
            for(Node n: children){
                if(GridPane.getColumnIndex(n) != null && GridPane.getColumnIndex(n) == (columnCount - 1)){
                    nodess[idx] = n;
                    idx++;
                    if(GridPane.getRowIndex(n) == 0){
                        alphablist.remove((TextField) n);
                    } else { nodes.remove((TextField) n); }
                }
            }
            table.getChildren().removeAll(nodess);
        });
        
        //"Cancel" -button: exits the creation scene an returns to main scene with no changes made
        cancel.setOnAction((event) -> {
            //clears the textfields before returning to main scene
            tfname.clear();
            tadesc.clear();
            error.setText("");
            for(Node n: table.getChildren()){
                if((GridPane.getRowIndex(n) == null || GridPane.getColumnIndex(n) == null) || (GridPane.getRowIndex(n) == 0 && GridPane.getColumnIndex(n) == 0)) continue;
                TextField t = (TextField) n;
                t.clear();
            }
            creationWindow.close();
            stage.setScene(main);
        });
        
        //"Finish"-button: when finished, creates a turing machine defined by the instructions
        finish.setOnAction((event) -> {
            String name = tfname.getText().trim();
            if(name.isEmpty()){
                error.setText("Give a name to the Turing machine.");
                return;
            }
            String dsc = tadesc.getText().trim();
            if(dsc.isEmpty()){
                error.setText("Describe the Turing machine shortly.");
                return;
            }
            boolean[] checkerAlpha = new boolean[130];
            int alphasize = alphablist.size();
            char[] alphabet = new char[alphasize];
            for(int i = 0; i < alphasize; i++){
                Node n = alphablist.get(i);
                int column = GridPane.getColumnIndex(n);
                String character = alphablist.get(i).getText().trim();
                if(character.length() > 1) {
                    error.setText("The alphabet must only contain characters.");
                    return;
                } else if (character.length() < 1){
                    error.setText("Please don't leave any empty columns or rows.");
                    return;
                } else if (!character.matches("[a-z]|[A-Z]|å|Å|ä|Ä|ö|Ö|_|[0-9]")){
                    error.setText("Please only use characters a-z, A-Z, å-ö, Å-Ö or numbers in the alphabet.");
                    return;
                }
                char a = character.charAt(0);
                if(checkerAlpha[a] == false){
                    checkerAlpha[a] = true;
                } else {
                    error.setText("Characters in the alphabet must be defined only once.");
                    return;
                }
                alphabet[column-1] = a;
            }
            
            HashSet<String> checkerBeta = new HashSet<>();
            int statesize = stateslist.size();
            String[] states = new String[statesize];
            for(int i = 0; i < statesize; i++){
                Node n = stateslist.get(i);
                int row = GridPane.getRowIndex(n);
                String text = stateslist.get(i).getText().trim();
                if(text.isEmpty()){
                    error.setText("Please don't leave any empty columns or rows.");
                    return;
                } else if (!text.matches("([a-z]|[A-Z]|å|Å|ä|Ä|ö|Ö|[0-9]|_)*")){
                    error.setText("Please only use characters a-z, A-Z, å-ö, Å-Ö, _ or numbers in the state names.");
                    return;
                } else if (text.equals("qa") || text.equals("qr")){
                    error.setText("\"qa\" and \"qr\" are not valid state names.");
                    return;
                }
                if(checkerBeta.contains(text)){
                    error.setText("State names must be unique.");
                    return;
                } else {
                    checkerBeta.add(text);
                }
                states[row-1] = text;
            }
            
            int rowCount = table.getRowCount();
            int columnCount = table.getColumnCount();
            String[][] ttable = new String[rowCount - 1][columnCount - 1];
            for(int i = 0; i < nodes.size(); i++){
                Node n = nodes.get(i);
                int row = GridPane.getRowIndex(n);
                int column = GridPane.getColumnIndex(n);
                String text = nodes.get(i).getText().trim();
                System.out.println(text);
                if(text.isEmpty()){
                    error.setText("Fill in all of the instructions.");
                    return;
                }
                if(!text.matches("(([a-z]|[A-Z]|å|Å|ä|Ä|ö|Ö|_|[0-9])( L | R | N )([a-z]|[A-Z]|å|Å|ä|Ä|ö|Ö|[0-9]|_)*)|qa|qr")){
                    error.setText("Instruction \"" + text + "\" is not a valid instruction.");
                    return;
                }
                if(text.length() > 2){
                    char character = text.charAt(0);
                    String stateName = text.substring(4, text.length());
                    if(checkerAlpha[character] == false){
                        error.setText("Instruction \"" + text + "\" is not a valid instruction.");
                        return;
                    } else if (!checkerBeta.contains(stateName) && !stateName.equals("qa") && !stateName.equals("qr")){
                        error.setText("Instruction \"" + text + "\" is not a valid instruction.");
                        return;
                    }
                }
                ttable[row-1][column-1] = text;
            }
            
            //creates a Turing machine
            boolean create = handle.createTM(name, dsc, ttable, alphabet, states);
            if(!create){
                error.setText("Project with the same name already exists.");
                return;
            }
            //clears the textfields before returning to main scene
            tfname.clear();
            tadesc.clear();
            error.setText("");
            char idx = 'a' - 1;
            int idxx = 1;
            for(Node n: table.getChildren()){
                if((GridPane.getRowIndex(n) == null && GridPane.getColumnIndex(n) == null)) continue;
                TextField t = (TextField) n;
                if(GridPane.getRowIndex(n) == null || GridPane.getRowIndex(n) == 0){
                    t.setText(idx + "");
                    idx++;
                } else if (GridPane.getColumnIndex(n) == null || GridPane.getColumnIndex(n) == 0){
                    t.setText("state" + idxx);
                    idxx++;
                } else {
                    t.clear();
                }
            }
            tmname.setText(handle.getCurrentTMName());
            currentTMname.setText("Name: " + handle.getCurrentTMName());
            currentTMdescription.setText("Description: " + handle.getCurrentTMDescription());
            currentTMalphabet.setText("Alphabet: "  +handle.getCurrentTMAlphabet());
            creationWindow.close();
            start.setDisable(false);
            stage.setScene(main);
        });
        
        //by default shows the main scene
        stage.setScene(main);
        stage.show();
    }
    
    private int format(String s){
        StringBuilder number = new StringBuilder();
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) != ' '){
                number.append(s.charAt(i));
            }
        }
        return Integer.valueOf(number.toString());
    }
    
}
