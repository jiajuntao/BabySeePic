package cn.babysee.picture.game;

import java.util.ArrayList;
import java.util.List;

public class GameList {

    public String title;
    public List<Game> list = new ArrayList<Game>();
    
    public void addGame(Game game) {
        list.add(game);
    }
    
    public List<Game> get() {
        return list;
    }

    @Override
    public String toString() {
        return "GameList [title=" + title + ", list=" + list + "]";
    }
}
