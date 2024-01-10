package skin_cancer_detection_android.ui.main;

public enum AppTab {

    HOME(0),
    HISTORY(1),
    PROFILE(2);

    private int index;

    AppTab(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static AppTab getByIndex(int id) {
        switch (id) {
            default:
                return HOME;
            case 1:
                return HISTORY;
            case 2:
                return PROFILE;
        }
    }

}
