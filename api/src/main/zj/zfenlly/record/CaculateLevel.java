package zj.zfenlly.record;

/**
 * Created by Administrator on 2016/7/18.
 */
public class CaculateLevel {

    private static int[][] lv = {
            {1, 5}, {2, 15}, {3, 30}, {4, 60}, {5, 120}, {6, 210}, {7, 330}, {8, 480}, {9, 660}, {10, 870}, {11, 1110},
            {12, 1380}, {13, 1680}, {14, 2010}, {15, 2370}, {16, 2760}, {17, 3180}, {18, 3630}, {19, 4110}, {20, 4620}, {21, 5160},
            {22, 5730}, {23, 6330}, {24, 6960}, {25, 7620}, {26, 8310}, {27, 9030},
            {28, 9780}, {29, 10560}, {30, 11370}, {31, 12210},
            {32, 13080}, {33, 13980}, {34, 14910}, {35, 15870},
            {36, 16860}, {37, 17780}, {38, 18930}, {39, 20010},
            {40, 21120}, {41, 22260}, {42, 23430}, {43, 24630},
            {44, 25860}, {45, 27120}, {46, 28410}, {47, 29730}, {48, 31080}
    };

    public CaculateLevel() {

    }

    public static String getNextLevelScore(String score) {
        String s;
        s = score;
        int iscore = Integer.parseInt(s);
        for (int i = 0; i < 48; i++) {
            if (lv[i][1] > iscore) {
                s = lv[i][1] - iscore + "";
                break;
            }
        }
        return s;
    }

    public static String getCaculateLevel(String score) {
        String a;

        a = score;
        int iscore = Integer.parseInt(a);
        for (int i = 0; i < 48; i++) {
            if (lv[i][1] >= iscore) {
                a = lv[i][0] - 1 + "";
                break;
            }
        }
        return a;
    }
}
