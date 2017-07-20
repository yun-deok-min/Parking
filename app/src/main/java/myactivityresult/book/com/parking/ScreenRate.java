package myactivityresult.book.com.parking;

public class ScreenRate {
    public int screen_width, screen_height;
    public int virtual_width, virtual_height;

    public ScreenRate(int ScreenWidth, int ScreenHeight) {
        screen_width = ScreenWidth;
        screen_height = ScreenHeight;
    }

    public void setSize(int width, int height) {
        virtual_width = width;
        virtual_height = height;
    }

    public int getX(int x) {
        return (int) (x * screen_width / virtual_width);
    }

    public int getY(int y) {
        return (int) (y * screen_height / virtual_height);
    }

    public int getVirtualX(int x) {
        return (int) (x * virtual_width / screen_width);
    }

    public int getVirtualY(int y) {
        return (int) (y * virtual_height / screen_height);
    }
}
