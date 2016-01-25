package com.mercandalli.android.apps.files.extras.admin.game;

import com.mercandalli.android.apps.files.common.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class GameGrille {

    private GameCase[][] matrice;

    public GameCase getCase(int i, int j) {
        return matrice[i][j];
    }

    public int getCaseValue(int i, int j) {
        return matrice[i][j].value;
    }

    public void setCaseValue(int i, int j, int value) {
        matrice[i][j].value = value;
    }

    List<Integer> wallValues;

    public final int size;

    public GameGrille(final int size) {
        this.size = size;
        this.matrice = new GameCase[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrice[i][j] = new GameCase(i, j, 0);
            }
        }

        wallValues = new ArrayList<>();
        wallValues.add(-1);

        addWalls();
    }

    public void resetMap() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.matrice[i][j].value = 0;
            }
        }
        addWalls();
    }

    public void resetWay() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (this.matrice[i][j].value == 8) {
                    this.matrice[i][j].value = 0;
                }
            }
        }
    }

    public void addWalls() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (MathUtils.random(0, 4) == 1) {
                    this.matrice[i][j].value = wallValues.get(0);
                }
            }
        }
    }

    public GameWay findWay() {
        // find start
        GameCase start = null;
        while (true) {
            int rdm = MathUtils.random(0, size - 1);
            if (getCaseValue(rdm, 0) == 0) {
                start = getCase(rdm, 0);
                break;
            }
        }
        start.value = 8;

        // find end
        GameCase end = null;
        while (true) {
            int rdm = MathUtils.random(0, size - 1);
            if (getCaseValue(rdm, size - 1) == 0) {
                end = getCase(rdm, size - 1);
                break;
            }
        }
        end.value = 8;

        return dijkstra(start, end);
    }

    public GameWay dijkstra(GameCase start, GameCase end) {
        GameWay result = new GameWay();


        // Create distances
        int[][] distance = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (Integer wallValue : wallValues) {
                    if (getCaseValue(i, j) == wallValue) {
                        distance[i][j] = -1;
                    }
                }
            }
        }
        distance[start.x][start.y] = 1;

        List<GameCase> currentGameCases = new ArrayList<>();
        currentGameCases.add(start);

        while (currentGameCases.size() != 0) {

            List<GameCase> tmp = new ArrayList<>();
            for (GameCase currentGameCase : currentGameCases) {
                List<GameCase> neighbors = neighbors(currentGameCase);
                for (GameCase neighbor : neighbors) {
                    if (distance[neighbor.x][neighbor.y] != -1 && (distance[currentGameCase.x][currentGameCase.y] + 1 < distance[neighbor.x][neighbor.y] || distance[neighbor.x][neighbor.y] == 0)) {
                        distance[neighbor.x][neighbor.y] = distance[currentGameCase.x][currentGameCase.y] + 1;
                        tmp.add(neighbor);
                    }
                }
            }

            currentGameCases.clear();
            for (GameCase tmpGameCase : tmp) {
                currentGameCases.add(tmpGameCase);
            }
        }

        // No way
        if (distance[end.x][end.y] == 0) {
            return result;
        }

        // Read distance from the end
        GameCase currentGameCase = end;


        int security = 0;

        while (!currentGameCase.equals(start) && security < size * size) {

            List<GameCase> neighbors = neighbors(currentGameCase);
            for (GameCase neighbor : neighbors) {
                if (distance[neighbor.x][neighbor.y] == distance[currentGameCase.x][currentGameCase.y] - 1) {
                    result.add(neighbor);
                    currentGameCase = neighbor;
                    break;
                }
            }

            security++;
        }


        result.revert();

        return result;
    }

    public List<GameCase> neighbors(GameCase target) {
        List<GameCase> result = new ArrayList<>();
        if (target.x - 1 >= 0) {
            result.add(getCase(target.x - 1, target.y));
        }
        if (target.y - 1 >= 0) {
            result.add(getCase(target.x, target.y - 1));
        }
        if (target.x + 1 < size) {
            result.add(getCase(target.x + 1, target.y));
        }
        if (target.y + 1 < size) {
            result.add(getCase(target.x, target.y + 1));
        }
        return result;
    }
}
