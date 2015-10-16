package mercandalli.com.filespace.ui.views.game;

/**
 * Created by Jonathan on 02/09/2015.
 */
public class Case {

    public int value;
    public final int x, y;

    public Case() {
        x = -1;
        y = -1;
    }

    public Case(int x, int y) {
        this.x = x;
        this.y = y;
        this.value = 0;
    }

    public Case(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof Case))
            return false;
        Case obj = (Case) o;
        return obj.x == this.x && obj.y == this.y && obj.value == this.value;
    }
}