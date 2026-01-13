import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

public class Main extends Application {

    private Label timeCurrent = new Label("0:00");
    private Label timeTotal = new Label("0:00");

    @Override
    public void start(Stage stage) {

        File folder = new File("music");
        File[] files = folder.listFiles();

        ArrayList<Song> songs = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.getName().toLowerCase().endsWith(".mp3")) {
                    songs.add(new Song(file.getName(), file.getPath()));
                }
            }
        }

        PlayerController player = new PlayerController(songs);

        // 🎧 LOGO
        ImageView logo = new ImageView(
                new Image(new File("logo.png").toURI().toString())
        );
        logo.setFitWidth(120);
        logo.setPreserveRatio(true);

        // 🎵 Título
        Label nowPlaying = new Label("Selecciona una canción");
        nowPlaying.setTextFill(Color.WHITE);
        nowPlaying.setFont(Font.font(16));

        // 📜 Playlist
        ListView<String> songList = new ListView<>();
        for (Song s : songs) {
            songList.getItems().add(new File(s.getPath()).getName());
        }

        songList.setOnMouseClicked(e -> {
            int index = songList.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                player.playAt(index);
                nowPlaying.setText("▶ " + new File(songs.get(index).getPath()).getName());
            }
        });

        // ▶️ CONTROLES
        Button play = new Button("▶");
        Button pause = new Button("⏸");
        Button next = new Button("⏭");

        play.setStyle(btn());
        pause.setStyle(btn());
        next.setStyle(btn());

        play.setOnAction(e -> player.play());
        pause.setOnAction(e -> player.pause());
        next.setOnAction(e -> player.next());

        HBox controls = new HBox(20, play, pause, next);
        controls.setAlignment(Pos.CENTER);

        // ⏱ PROGRESO
        Slider progress = new Slider();
        progress.setPrefWidth(320);

        if (player.getMediaPlayer() != null) {

            player.getMediaPlayer().setOnReady(() -> {
                Duration total = player.getMediaPlayer().getTotalDuration();
                timeTotal.setText(format(total));
            });

            player.getMediaPlayer().currentTimeProperty().addListener((obs, o, n) -> {
                Duration total = player.getMediaPlayer().getTotalDuration();
                if (total != null && total.toSeconds() > 0) {
                    progress.setValue(n.toSeconds() / total.toSeconds() * 100);
                    timeCurrent.setText(format(n));
                }
            });
        }

        progress.setOnMouseReleased(e -> {
            if (player.getMediaPlayer() != null) {
                Duration total = player.getMediaPlayer().getTotalDuration();
                player.getMediaPlayer().seek(
                        Duration.seconds(total.toSeconds() * progress.getValue() / 100)
                );
            }
        });

        timeCurrent.setTextFill(Color.GRAY);
        timeTotal.setTextFill(Color.GRAY);

        HBox timeBox = new HBox(10, timeCurrent, progress, timeTotal);
        timeBox.setAlignment(Pos.CENTER);

        // 🔊 VOLUMEN
        Slider volume = new Slider(0, 1, 0.5);
        volume.setPrefWidth(150);
        volume.valueProperty().addListener((o, a, b) -> {
            if (player.getMediaPlayer() != null)
                player.getMediaPlayer().setVolume(b.doubleValue());
        });

        // 🧱 LAYOUT
        VBox root = new VBox(15,
                logo,
                nowPlaying,
                songList,
                timeBox,
                controls,
                volume
        );

        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #121212;");

        stage.setScene(new Scene(root, 450, 600));
        stage.setTitle("Music Player");
        stage.show();
    }

    private String btn() {
        return "-fx-background-color:#1DB954;" +
               "-fx-text-fill:black;" +
               "-fx-font-size:16px;" +
               "-fx-background-radius:50%;" +
               "-fx-min-width:50px;" +
               "-fx-min-height:50px;";
    }

    private String format(Duration d) {
        int s = (int) d.toSeconds();
        return (s / 60) + ":" + String.format("%02d", s % 60);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
