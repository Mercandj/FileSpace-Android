package mercandalli.com.filespace.ui.views.game;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.utils.MathUtils;

/**
 * Created by Jonathan on 02/09/2015.
 */
public class Grille {

    private Case[][] matrice;

    public Case getCase(int i, int j) {
        return matrice[i][j];
    }

    public int getValeurCase(int i, int j) {
        return matrice[i][j].value;
    }

    public void setValeurCase(int i, int j, int value) {
        matrice[i][j].value = value;
    }

    List<Integer> wallValues;

    public final int size;

    public Grille(final int size) {
        this.size = size;
        this.matrice = new Case[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                matrice[i][j] = new Case(i, j, 0);

        wallValues = new ArrayList<>();
        wallValues.add(-1);

        addWalls();
    }

    public void resetMap() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                this.matrice[i][j].value = 0;
        addWalls();
    }

    public void resetWay() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (this.matrice[i][j].value == 8)
                    this.matrice[i][j].value = 0;
    }

    public void addWalls() {
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (MathUtils.random(0, 4) == 1)
                    this.matrice[i][j].value = wallValues.get(0);
    }

    public Way findWay() {
        // find start
        Case start = null;
        while (true) {
            int rdm = MathUtils.random(0, size - 1);
            if (getValeurCase(rdm, 0) == 0) {
                start = getCase(rdm, 0);
                break;
            }
        }
        start.value = 8;

        // find end
        Case end = null;
        while (true) {
            int rdm = MathUtils.random(0, size - 1);
            if (getValeurCase(rdm, size - 1) == 0) {
                end = getCase(rdm, size - 1);
                break;
            }
        }
        end.value = 8;

        return dijkstra(start, end);
    }

    public Way dijkstra(Case start, Case end) {
        Way result = new Way();


        // Create distances
        int[][] distance = new int[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                for (Integer wallValue : wallValues)
                    if (getValeurCase(i, j) == wallValue)
                        distance[i][j] = -1;
        distance[start.x][start.y] = 1;

        List<Case> currentCases = new ArrayList<>();
        currentCases.add(start);

        while (currentCases.size() != 0) {

            List<Case> tmp = new ArrayList<>();
            for (Case currentCase : currentCases) {
                List<Case> neighbors = neighbors(currentCase);
                for (Case neighbor : neighbors) {
                    if (distance[neighbor.x][neighbor.y] != -1 && (distance[currentCase.x][currentCase.y] + 1 < distance[neighbor.x][neighbor.y] || distance[neighbor.x][neighbor.y] == 0)) {
                        distance[neighbor.x][neighbor.y] = distance[currentCase.x][currentCase.y] + 1;
                        tmp.add(neighbor);
                    }
                }
            }

            currentCases.clear();
            for (Case tmpCase : tmp)
                currentCases.add(tmpCase);

        }

        // No way
        if (distance[end.x][end.y] == 0)
            return result;


        // Read distance from the end
        Case currentCase = end;


        int security = 0;

        while (!currentCase.equals(start) && security < size * size) {

            List<Case> neighbors = neighbors(currentCase);
            for (Case neighbor : neighbors) {
                if (distance[neighbor.x][neighbor.y] == distance[currentCase.x][currentCase.y] - 1) {
                    result.add(neighbor);
                    currentCase = neighbor;
                    break;
                }
            }

            security++;
        }


        result.revert();

        return result;
    }

    public List<Case> neighbors(Case target) {
        List<Case> result = new ArrayList<>();
        if (target.x - 1 >= 0)
            result.add(getCase(target.x - 1, target.y));
        if (target.y - 1 >= 0)
            result.add(getCase(target.x, target.y - 1));
        if (target.x + 1 < size)
            result.add(getCase(target.x + 1, target.y));
        if (target.y + 1 < size)
            result.add(getCase(target.x, target.y + 1));
        return result;
    }
}
