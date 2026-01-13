import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;

public class PlayerController {

    private ArrayList<Song> songs;
    private int currentIndex = 0;
    private MediaPlayer mediaPlayer;

    public PlayerController(ArrayList<Song> songs) {
        this.songs = songs;

        if (!songs.isEmpty()) {
            loadSong(0);
        }
    }

    // 🎵 Cargar canción
    private void loadSong(int index) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        currentIndex = index;

        File file = new File(songs.get(currentIndex).getPath());
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
    }

    // ▶ Play
    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    // ⏸ Pause
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    // ⏭ Next
    public void next() {
        if (songs.isEmpty()) return;

        currentIndex++;
        if (currentIndex >= songs.size()) {
            currentIndex = 0;
        }

        loadSong(currentIndex);
        play();
    }

    // ▶ Reproducir una canción específica
    public void playAt(int index) {
        if (index < 0 || index >= songs.size()) return;

        loadSong(index);
        play();
    }

    // 🔧 NECESARIO para barra de progreso y volumen
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
