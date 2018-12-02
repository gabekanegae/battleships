package battleships.main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;

public class Main extends Application {
    //CSS for selected icons
    public static final String HIGHLIGHT = "-fx-border-color: red; -fx-border-width: 1; -fx-border-radius: 2;";

    //Grid sprites
    public static final Image waterImage = new Image("/images/grid/water.png");
    public static final Image aimImage = new Image("/images/grid/aim.png");
    public static final Image wrongImage = new Image("/images/grid/wrong.png");
    public static final Image debrisImage = new Image("/images/grid/debris.png");
    public static final Image explosionImage = new Image("/images/grid/explosion.png");

    //Sprite arrays of ship cells
    public static final Image[] buoyImage = {new Image("/images/ships/buoy0.png")};
    public static final Image[] submarineImage = {new Image("/images/ships/submarine0.png"),
                                                  new Image("/images/ships/submarine1.png")};
    public static final Image[] torpedoImage = {new Image("/images/ships/torpedo0.png"),
                                                new Image("/images/ships/torpedo1.png"),
                                                new Image("/images/ships/torpedo2.png")};
    public static final Image[] tankerImage = {new Image("/images/ships/tanker0.png"),
                                               new Image("/images/ships/tanker1.png"),
                                               new Image("/images/ships/tanker2.png"),
                                               new Image("/images/ships/tanker3.png")};
    public static final Image[] carrierImage = {new Image("/images/ships/carrier0.png"),
                                                new Image("/images/ships/carrier1.png"),
                                                new Image("/images/ships/carrier2.png"),
                                                new Image("/images/ships/carrier3.png"),
                                                new Image("/images/ships/carrier4.png")};

    //Sprite arrays of destroyed ship cells
    public static final Image[] buoyDestroyedImage = {new Image("/images/debris/buoy0d.png")};
    public static final Image[] submarineDestroyedImage = {new Image("/images/debris/submarine0d.png"),
                                                           new Image("/images/debris/submarine1d.png")};
    public static final Image[] torpedoDestroyedImage = {new Image("/images/debris/torpedo0d.png"),
                                                         new Image("/images/debris/torpedo1d.png"),
                                                         new Image("/images/debris/torpedo2d.png")};
    public static final Image[] tankerDestroyedImage = {new Image("/images/debris/tanker0d.png"),
                                                        new Image("/images/debris/tanker1d.png"),
                                                        new Image("/images/debris/tanker2d.png"),
                                                        new Image("/images/debris/tanker3d.png")};
    public static final Image[] carrierDestroyedImage = {new Image("/images/debris/carrier0d.png"),
                                                         new Image("/images/debris/carrier1d.png"),
                                                         new Image("/images/debris/carrier2d.png"),
                                                         new Image("/images/debris/carrier3d.png"),
                                                         new Image("/images/debris/carrier4d.png")};

    //Bomb sprites
    public static final Image JDAMImage = new Image("/images/icons/JDAM.png");
    public static final Image missileImage = new Image("/images/icons/missile.png");

    //Images for turn arrows
    public static final Image turnImageOn = new Image("/images/turnImageOn.png");
    public static final Image turnImageOff = new Image("/images/turnImageOff.png");

    public static Stage mainStage;

    //Loads and returns a scene FXML
    public static FXMLLoader getLoader(String sceneName, Class c) {
        return new FXMLLoader(c.getResource("/gui/" + sceneName + ".fxml"));
    }

    //Loads and returns a Scene object
    public static Scene loadScene(FXMLLoader loader, String sceneName, Class c) {
        try {
            Parent root = loader.load();
            Scene scene = new Scene(root, 960, 540);
            scene.getStylesheets().add(c.getResource("/gui/" + sceneName + ".css").toExternalForm()); //Loads CSS
            return scene;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Sets and shows a scene
    public static void setScene(Scene scene) {
        mainStage.setScene(scene);
        mainStage.sizeToScene();
        mainStage.show();
    }

    //Runs a function after a set delay
    public static void delay(Runnable function) {
        Random r = new Random();
        int delay = r.nextInt(12)*100 + 900;

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(function);
        });
        t.setDaemon(true);
        t.start();
    }

    //JavaFX start
    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;

        Font.loadFont(getClass().getResourceAsStream("/fonts/arbn2plt.ttf"), 0); //Load Airborne II Pilot font file
        Font.loadFont(getClass().getResourceAsStream("/fonts/notosansblk.ttf"), 0); //Load Noto Sans Blk font file

        primaryStage.setResizable(false); //Window can't be resized
        primaryStage.getIcons().add(new Image("/images/icon.png")); //Load program icon
        primaryStage.setTitle("Battleships"); //Window title

        //Load and set menu scene
        FXMLLoader loader = Main.getLoader("menu", getClass());
        Scene scene = Main.loadScene(loader, "menu", getClass());
        Main.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}