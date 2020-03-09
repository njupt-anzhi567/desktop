package me.isaiah.shell.programs;

import java.io.File;
import java.net.URL;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.zunozap.Info;
import com.zunozap.Reader;
import com.zunozap.Settings;
import com.zunozap.UniversalEngine;
import com.zunozap.UniversalEngine.Engine;
import com.zunozap.ZunoAPI;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import me.isaiah.shell.Main;
import me.isaiah.shell.api.Toast;
import me.isaiah.shell.api.JFXProgram;
import me.isaiah.shell.api.ProgramInfo;

@Info(name="ZunoZap", version="0.7.2", engine = Engine.WEBKIT, enableGC = false)
@ProgramInfo(name = "ZunoZap Browser", version="0.7.2", authors="Contributers", width=1000, height=650)
@Deprecated // TODO better support
public class ZunoZap extends ZunoAPI {

    public static final File home = new File(System.getProperty("user.home"), "zunozap");
    private static Reader bmread;

    public static void run() {
        new JFXPanel(); // init JavaFX
        Platform.runLater(() -> {
            try {
                ZunoZap.main(null);
            }  catch (Exception e) { 
                Toast.show(e.getMessage(), 5000);
                e.printStackTrace(); 
            }
        });
    }

    public static void main(String[] args) throws Exception {
        setInstance(new ZunoZap());
        File s = new File(home, "settings.dat");
        if (!s.exists()) {
            home.mkdir();
            s.createNewFile();
            Settings.save();
            firstRun = true;
        }
        bmread = new Reader(menuBook);
        Stage st = new Stage();
        getInstance().start(st);
    }

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        BorderPane border = new BorderPane();

        root.getChildren().add(border);
        Scene scene = new Scene(root, 1200, 600);

        en = Engine.WEBKIT;

        //Settings.init(cssDir);

        try {
            setup(new URL("https://raw.githubusercontent.com/ZunoZap/Blacklist/master/list.dat"), true);
        } catch (Exception e) {}

        mkDirs(home, cssDir);

        tb = new TabPane();
        menuBar = new MenuBar();

        tb.setPrefSize(1365, 768);
        tb.setSide(Side.TOP);

        // Setup tabs
        Tab newtab = new Tab(" + ");
        newtab.setClosable(false);
        tb.getTabs().add(newtab);
        createTab("http://zunozap.com/");

        tb.getSelectionModel().selectedItemProperty().addListener((a,b,c) -> { if (c == newtab) createTab("http://duck.com/"); });

        border.setCenter(tb);
        border.setTop(menuBar);
        border.autosize();

        WebView dummy = new WebView();
        setUserAgent(dummy.getEngine());
        regMenuItems(menuFile, menuBook, aboutPageHTML(dummy.getEngine().getUserAgent(), "N/A", "ZunoZap/zunozap/master/LICENCE", "LGPLv3"), tb, en);
        menuBar.getMenus().addAll(menuFile, menuBook);
        Settings.set(cssDir, scene);
        scene.getStylesheets().add(ZunoAPI.stylesheet.toURI().toURL().toExternalForm());

        JFXProgram inf = new JFXProgram("ZunoZap 0.7.2-port");
        inf.setScene(scene);
        inf.setClosable(true);
        inf.setSize(1000, 650);
        inf.setVisible(true);
        inf.addInternalFrameListener(new InternalFrameAdapter(){
            public void internalFrameClosing(InternalFrameEvent e) { stop(); }
        });
        Main.p.add(inf);
    }

    @Override
    @SuppressWarnings("static-access") 
    public final void createTab(String url) {
        // Create Tab
        final Tab tab = new Tab("Loading...");

        final Button back = new Button("<"), forward = new Button(">"), goBtn = new Button("Go"), bkmark = new Button("Bookmark");

        WebView web = new WebView();
        WebEngine engine = web.getEngine();
        TextField urlField = new TextField("https://");
        HBox hBox = new HBox(back, forward, urlField, goBtn, bkmark);
        VBox vBox = new VBox(hBox, web);
        UniversalEngine e = new UniversalEngine(web);

        urlChangeLis(e, web, engine, urlField, tab, bkmark);

        goBtn.setOnAction(v -> loadSite(urlField.getText(), e));
        urlField.setOnAction(v -> loadSite(urlField.getText(), e));

        back.setOnAction(v -> history(engine, "BACK"));
        forward.setOnAction(v -> history(engine, "FORWARD"));

        bkmark.setOnAction(v -> bookmarkAction(e, bmread, t -> createTab(engine.getLocation()), bkmark, menuBook));

        // Setting Styles
        urlField.setId("urlfield");
        urlField.setMaxWidth(400);
        hBox.setId("urlbar");
        hBox.setHgrow(urlField, Priority.ALWAYS);
        vBox.setVgrow(web, Priority.ALWAYS);
        vBox.autosize();

        engine.setUserDataDirectory(data);
        setUserAgent(engine);

        loadSite(url, e);

        tab.setContent(vBox);

        tab.setOnCloseRequest(a ->
            ((WebView) ((VBox) ((Tab) a.getSource()).getContent()).getChildren().get(1)).getEngine().loadContent("Closing")
        );

        final ObservableList<Tab> tabs = tb.getTabs();
        tabs.add(tabs.size() - 1, tab);
        tb.getSelectionModel().select(tab);
    }

    public final void urlChangeLis(UniversalEngine u, WebView web, final WebEngine en, final TextField urlField, final Tab tab, Button bkmark) {
        en.locationProperty().addListener((o,oU,nU) -> ZunoZap.this.changed(u, urlField, tab, oU, nU, bkmark, bmread));
        en.titleProperty().addListener((ov, o, n) -> tab.setText(n));
    }

    @Override
    protected void onTabClosed(Object s) {
    }

    @Override
    public void start(Stage arg0, Scene arg1, StackPane arg2, BorderPane arg3) throws Exception {
    }

}