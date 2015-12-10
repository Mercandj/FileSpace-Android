package com.mercandalli.android.apps.files.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.extras.genealogy.ModelGenealogyPerson;

import java.util.ArrayList;
import java.util.List;

public class GenealogyBigTreeView extends View {

    private ModelGenealogyPerson personne;
    List<ModelGenealogyPerson> tableau_ascendants;
    List<Integer> tableau_positions_horizontal;                // position sur la ligne de l arbre (de la gauche vers la droite)
    List<Integer> tableau_positions_vertical;                    // ligne de l'arbre (correspond aux generations 1, 2 => en remontant)
    List<Float> tableau_longueurs_lignes_horizontales;            // longueurs des lignes de l arbre
    ArrayList<Float> tableau_coord_horizontales_noms;                // coordonnees X (horizontales) pointant sur le milieu du nom
    ArrayList<Float> tableau_coord_horizontales_noms_old;            // coordonnees X (horizontales) pointant sur le milieu du nom de la ligne du dessous


    //Parametres de l'ecran tactile
    // --- un doigt
    private int x_down;                // down : appui
    private int y_down;
    private int x_up;                // up : relacher
    private int y_up;
    private int x_move = -1;        // move ! deplacement
    private int y_move = -1;

    private int deltaX;                // mesure la variation horizontale entre la fin et le debut du glissement du doigt
    private int deltaY;                // mesure la variation verticale entre la fin et le debut du glissement du doigt
    private int deltaXmouv;            // mesure les variations horizontales quand le doigt touche l ecran
    private int deltaYmouv;            // mesure les variations verticales quand le doigt touche l ecran

    // --- multi-touch
    private int x_move_multi = -1;    // move ! deplacement
    private int y_move_multi = -1;

    private int ecart;                // ecart entre les 2 doigts (multi-touch)
    private int ecart_old;            // ancien ecart entre les 2 doigts : permet comparaison et ajustement taille de l affichage => coeff-taille

    private int deltaXcomp;            // mesure les variations horizontales quand le doigt touche l ecran apres le multi-touch et si c est le 2eme doigt qui continue a toucher l'ecran (eq deltaXmouv)
    private int deltaYcomp;            // mesure les variations verticales quand le doigt touche l ecran apres le multi-touch et si c est le 2eme doigt qui continue a toucher l'ecran (eq deltaYmouv)

    private int multi_touch;        // 1 quand multi-touch, 0 sinon
    private int id_premier_doigt;    // ID du contact mono-touch ;permet de savoir a la fin du multi-touch si c est le meme doigt qui touche l ecran ou l autre (cas complique)
    private int inversion_doigt;    // 1 quand inversion des doigts apres un multi touch (c est le 2ieme doigt pose qui reste ensuite sur l ecran quand on repasse en mono-touch


    // dessin des arbres genealogiques
    private int coeff_taille = COEFF_TAILLE_INIT;                            // taille de l arbre

    private static final int TAILLE_MINI = 5;
    private static final int COEFF_TAILLE_INIT = 30;
    private static final int DECALAGE_TEXTE_VERTICAL = 3;
    private static final int DECALAGE_TEXTE_HORIZONTAL = 3;

    private int hauteur;
    private int largeur;

    private static Paint paint = new Paint();

    public GenealogyBigTreeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // --- arbre des ascendants
        //
        //	H	I	J	K	L	M	N	O
        //	|	|	|	|	|	|	|	|
        //	----	----	----	----
        //		D		E		F		G
        //		|		|		|		|
        //		--------		--------
        //			B				C
        //			|				|
        //			----------------
        //					A
        //
        // avec A,B, C... les personnes dans la BDD
        //
        //   --- Structure du tableau des ascendants
        //
        // A||BC||DEFG||HIJKLMNO|| ... ||0 => le tableau s'arrete qd il n y a plus de pere et de mere
        //
        // si info manquante, par ex, sur E, le tableau prend la forme
        //
        // A||BC||D0EFG||HILMNO|| ... ||0
        //
        // recuperation du tableau des ascendants
        tableau_ascendants = new ArrayList<>();

        ModelGenealogyPerson tmp = new ModelGenealogyPerson();
        tmp.last_name = "";
        tmp.is_man = true;
        tmp.first_name_1 = "Select a person on the list";
        tableau_ascendants.add(tmp);

        tableau_ascendants.add(new ModelGenealogyPerson(false));
        tableau_ascendants.add(new ModelGenealogyPerson(false));

        resetAscendants(tableau_ascendants);
    }

    private void resetAscendants(List<ModelGenealogyPerson> tableau_ascendants) {
        this.tableau_ascendants = tableau_ascendants;

        // elaboration des positions des differents noms a afficher
        // tableau_position : A(0,0)||B(1,0)C(1,1)||D(2,0)E(2,1)F(2,3)G(2,4)||HIJKLMNO...||
        // 							vertical	horizontal
        //	tableau des positions	0			0				personne au centre
        //	tableau des positions	1			0				position du pere
        //	tableau des positions	1			1				position de la mere
        //	tableau des positions	2			0				position du pere du pere
        //	tableau des positions	2			1				position de la mere du pere
        //	tableau des positions	2			2				position du pere de la mere
        //	tableau des positions	2			3				position de la mere de la mere
        //	tableau des positions	3			0				...
        //	tableau des positions	3			1
        //	tableau des positions	3			2
        //	tableau des positions	3			3
        //
        // init des tableaux de positions des elements a afficher
        tableau_positions_horizontal = new ArrayList<Integer>();
        tableau_positions_vertical = new ArrayList<Integer>();
        // --- elaboration des positions du 1er element : 0, 0 car au centre
        tableau_positions_vertical.add(0);
        tableau_positions_horizontal.add(0);
        // --- elaboration des positions des autres noms a afficher
        int ligne_courante = 1;
        int nbre_noms_ligne_precedente = 1;
        int nbre_noms_ligne_courante = 0;
        int position_ds_ligne_courante = 0;
        for (int i = 0; i < tableau_ascendants.size(); i++) {
            personne = tableau_ascendants.get(i);
            if (personne.isValid()) {
                Log.d("ArbreView_Constructeur", "analyse : " + personne.getAdapterTitle());
                tableau_positions_vertical.add(ligne_courante);
                tableau_positions_horizontal.add(2 * position_ds_ligne_courante);
                nbre_noms_ligne_courante++;
                Log.d("ArbreView_Constructeur", "position pere : " + String.valueOf(ligne_courante) + " " + String.valueOf(2 * position_ds_ligne_courante));
                tableau_positions_vertical.add(ligne_courante);
                tableau_positions_horizontal.add(2 * position_ds_ligne_courante + 1);
                Log.d("ArbreView_Constructeur", "position mere : " + String.valueOf(ligne_courante) + " " + String.valueOf(2 * position_ds_ligne_courante + 1));
                nbre_noms_ligne_courante++;
                position_ds_ligne_courante++;
            }
            nbre_noms_ligne_precedente--;
            if (nbre_noms_ligne_precedente == 0) {
                ligne_courante++;
                position_ds_ligne_courante = 0;
                nbre_noms_ligne_precedente = nbre_noms_ligne_courante;
            }
        }

        // --- voir les tableaux des ascendants et des positions dans les log
        for (int i = 0; i < tableau_ascendants.size(); i++) {
            personne = tableau_ascendants.get(i);
            Log.d("ArbreView_Constructeur", "tableau_ascendants : " + String.valueOf(personne.id) + " " + personne.getAdapterTitle());
        }
        for (int i = 0; i < tableau_positions_vertical.size(); i++) {
            Log.d("ArbreView_Constructeur", "tableau_positions : " + String.valueOf(tableau_positions_vertical.get(i)) + " " + String.valueOf(tableau_positions_horizontal.get(i)));
        }

        // --- init des tableaux des longueurs des lignes de l arbre et coordonnees horizontales des points milieu des noms a afficher
        tableau_longueurs_lignes_horizontales = new ArrayList<>();
        tableau_coord_horizontales_noms = new ArrayList<>();
        tableau_coord_horizontales_noms_old = new ArrayList<>();
    }

    public void resetOffsetTouch() {
        deltaX = deltaY = 0;
    }

    public void onTouch(MotionEvent event) {
        x_move = (int) event.getX(0);
        y_move = (int) event.getY(0);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x_down = (int) event.getX(0);
                y_down = (int) event.getY(0);
                deltaXmouv = 0;
                deltaYmouv = 0;
                break;
            case MotionEvent.ACTION_UP:
                x_up = (int) event.getX(0);
                y_up = (int) event.getY(0);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }

        // GESTION MULTI-TOUCH
        if (event.getPointerCount() > 1) {
            x_move_multi = (int) event.getX(1);
            y_move_multi = (int) event.getY(1);

            // --- gestion des deplacements X et Y quand le deuxieme doigt touche l ecran
            ecart_old = ecart;
            ecart = (int) Math.sqrt((x_move_multi - x_move) * (x_move_multi - x_move) + (y_move_multi - y_move) * (y_move_multi - y_move)) / 5;
            if (multi_touch != 0) {
                if (ecart - ecart_old + coeff_taille > TAILLE_MINI) {
                    coeff_taille = ecart - ecart_old + coeff_taille;
                } else coeff_taille = TAILLE_MINI;
            }
            multi_touch = 1;
        }
        // GESTION MONO-TOUCH
        else {
            // on est en mono-touch depuis le debut de l action
            if (multi_touch == 0) {
                // gestion du cas ou un multi-touch a eu lieu avant avec changement de doigts (deltaXcomp et deltaYcomp ont fonctionnes pour gerer deplacement du doigt)
                if ((inversion_doigt == 1)) {
                    // on additionne deltaXcomp a deltaX et on met a 0 deltaXcomp, ce qui ne change rien en terme de deplacement de l arbre (termes additionnes) mais se remet dans bonne config pour prochain multi-touch : deltaXcomp = 0)
                    // idem bien sur pour deltaYcomp a deltaY
                    deltaX = deltaX + deltaXcomp;
                    deltaY = deltaY + deltaYcomp;
                    deltaXcomp = 0;
                    deltaYcomp = 0;
                    // initilisation de x_up y_up, ainsi que x_down et y_down
                    x_up = 0;
                    y_up = 0;
                    x_down = x_move;
                    y_down = y_move;
                    // remise a 0 indicateur d inversion doigt pendant multi-touch
                    inversion_doigt = 0;
                }
                // gestion des deplacements du doigt
                Log.d("ArbreView_onTouch", "x_move : " + x_move + " - y_move : " + y_move);
                // --- variation X et Y entre le moment ou le doigt touche l ecran et ou il s enleve
                deltaX = deltaX + (x_up - x_down);
                deltaY = deltaY + (y_up - y_down);
                // --- gestion des deplacements X et Y quand le doigt touche l ecran
                deltaXmouv = x_move - x_up;
                deltaYmouv = y_move - y_up;

                // recupere l'ID du doigt qui touche l ecran
                id_premier_doigt = event.getPointerId(0);
            }
            // on passe du multi-touch au mono-touch
            else {
                // gestion du cas ou le doigt encore en contact avec l ecran n etait pas le premier pose
                if (event.getPointerId(0) != id_premier_doigt) {
                    // deltaXcomp et deltaYcomp gerent le deplacement du doigt (eq deltaXmouv et deltaYmouv en mono-touch)
                    deltaXcomp = x_move - x_move_multi;
                    deltaYcomp = y_move - y_move_multi;
                    // on additionne deltaXmouv a deltaX et on met a 0 deltaXmouv, ce qui ne change rien en terme de deplacement de l arbre (termes additionnes) mais se remet dans bonne config pour prochain mono-touch : deltaXmouv = 0)
                    // idem bien sur pour deltaYmouv a deltaY
                    deltaX = deltaX + deltaXmouv;
                    deltaY = deltaY + deltaYmouv;
                    deltaXmouv = 0;
                    deltaYmouv = 0;
                    // on met  l indicateur d inversion de doigt pendant le multi-touch a 1 pour indiquer que  l on est dans ce cas
                    inversion_doigt = 1;
                }
                // gestion du cas ou le doigt encore en contact avec l ecran etait le premier pose
                // aucun pb, on revient en mono-touch directement...
                else {
                    multi_touch = 0;
                    deltaXcomp = 0;
                    deltaYcomp = 0;
                    // ... mais il faut gerer le cas ou la personne leve son doigt principal sans deplacement
                    if (x_up != 0 || y_up != 0) {
                        // on additionne deltaXmouv a deltaX et on met a 0 deltaXmouv, ce qui ne change rien en terme de deplacement de l arbre (termes additionnes) mais se remet dans bonne config pour prochain mono-touch : deltaXmouv = 0)
                        // idem bien sur pour deltaYmouv a deltaY
                        deltaX = deltaX + deltaXmouv;
                        deltaY = deltaY + deltaYmouv;
                        deltaXmouv = 0;
                        deltaYmouv = 0;
                        // initilisation de x_up y_up, ainsi que x_down et y_down
                        x_up = 0;
                        y_up = 0;
                        x_down = x_move;
                        y_down = y_move;
                    }
                }
            }
        }

        x_down = 0;
        y_down = 0;
        x_move = 0;
        y_move = 0;
        x_up = 0;
        y_up = 0;

        this.invalidate(); //Permet de relanche onDraw : un peu comme g.repaint();
    } //Methode appellee ici par l'Activity


    protected void onDraw(Canvas canvas) {
        hauteur = this.getHeight();
        largeur = this.getWidth();

        // --- defini la taille des textes a afficher
        paint.setTextSize(coeff_taille);

        // --- elaboration des positions des autres noms a afficher
        int ligne_courante = 1;
        int position_ds_ligne_courante = 0;

        // --- elaboration de la longueur de chaque ligne des ascendants
        tableau_longueurs_lignes_horizontales.clear();
        // --- --- elaboration de la longueur du nom a afficher au centre (longueur de la ligne 0)
        personne = tableau_ascendants.get(0);
        String string_a_afficher = personne.getAdapterTitle();
        tableau_longueurs_lignes_horizontales.add(paint.measureText(string_a_afficher));
        // --- --- elaboration des autres longueurs
        Float longueur_courante = (float) 0;
        for (int i = 1; i < tableau_ascendants.size(); i++) {
            personne = tableau_ascendants.get(i);
            string_a_afficher = personne.getAdapterTitle();
            if (tableau_positions_vertical.get(i) == ligne_courante) {
                longueur_courante = longueur_courante + paint.measureText(string_a_afficher) + (DECALAGE_TEXTE_HORIZONTAL + ((position_ds_ligne_courante) % 2) * DECALAGE_TEXTE_HORIZONTAL) * coeff_taille;
                position_ds_ligne_courante++;
            } else {
                tableau_longueurs_lignes_horizontales.add(longueur_courante - 2 * DECALAGE_TEXTE_HORIZONTAL * coeff_taille);
                ligne_courante++;
                position_ds_ligne_courante = 0;
                longueur_courante = paint.measureText(string_a_afficher) + DECALAGE_TEXTE_HORIZONTAL * coeff_taille;
            }
        }
        // --- --- elaboration de la longueur de la derniere ligne
        tableau_longueurs_lignes_horizontales.add(longueur_courante - DECALAGE_TEXTE_HORIZONTAL * coeff_taille);

        // --- affichage de l arbre des ascendants
        ligne_courante = 0;
        position_ds_ligne_courante = 0;
        longueur_courante = (float) 0;
        float coord_horizontale_nom_a_afficher = 0;
        float coord_verticale_nom_a_afficher = 0;
        float ligne_parents_coord_horizontale_point_gauche = 0;
        float ligne_parents_coord_horizontale_point_droite = 0;
        float coord_verticale_au_dessus = 0;
        float coord_verticale_en_dessous = 0;
        float coord_horizontale_milieu_nom = 0;
        tableau_coord_horizontales_noms.clear();
        tableau_coord_horizontales_noms_old.clear();
        for (int i = 0; i < tableau_ascendants.size(); i++) {
            personne = tableau_ascendants.get(i);
            string_a_afficher = personne.getAdapterTitle();
            if (tableau_positions_vertical.get(i) == ligne_courante) {
                position_ds_ligne_courante++;
            } else {
                ligne_courante++;
                position_ds_ligne_courante = 0;
                longueur_courante = (float) 0;
                tableau_coord_horizontales_noms_old = (ArrayList) tableau_coord_horizontales_noms.clone();
                tableau_coord_horizontales_noms.clear();
            }

            // --- --- affichage des noms
            coord_horizontale_nom_a_afficher = largeur / 2 + deltaX + deltaXmouv + deltaXcomp + longueur_courante - ((tableau_longueurs_lignes_horizontales.get(tableau_positions_vertical.get(i))) / 2);
            coord_verticale_nom_a_afficher = hauteur / 2 + deltaY + deltaYmouv + deltaYcomp + (paint.getTextSize() / 2) - DECALAGE_TEXTE_VERTICAL * coeff_taille * tableau_positions_vertical.get(i);
            if (personne.isValid()) {
                paint.setColor(getResources().getColor(personne.is_man ? R.color.genealogy_man : R.color.genealogy_woman));
            }
            canvas.drawText(string_a_afficher, coord_horizontale_nom_a_afficher, coord_verticale_nom_a_afficher, paint);
            paint.setColor(Color.BLACK);

            // --- --- mise en memoire des coordonnees horizontales des noms pour gerer les lignes horizontales entre enfants et parents
            coord_horizontale_milieu_nom = coord_horizontale_nom_a_afficher + paint.measureText(string_a_afficher) / 2;
            if (personne.isValid()) {
                tableau_coord_horizontales_noms.add(coord_horizontale_milieu_nom);
            }
            // --- --- affichage des lignes verticales au dessus des noms (si un nom est vraiment present)
            coord_verticale_au_dessus = coord_verticale_nom_a_afficher - paint.getTextSize() / 2 - DECALAGE_TEXTE_VERTICAL * coeff_taille / 2;
            if (personne.isValid()) {
                canvas.drawLine(
                        coord_horizontale_milieu_nom,
                        coord_verticale_nom_a_afficher - paint.getTextSize(),
                        coord_horizontale_milieu_nom,
                        coord_verticale_au_dessus,
                        paint);
            }
            // --- --- affichage des lignes horizontales en dessous des noms...
            if ((position_ds_ligne_courante) % 2 == 1) {
                ligne_parents_coord_horizontale_point_gauche = ligne_parents_coord_horizontale_point_droite;
            }
            ligne_parents_coord_horizontale_point_droite = coord_horizontale_nom_a_afficher + paint.measureText(string_a_afficher) / 2;
            coord_verticale_en_dessous = coord_verticale_nom_a_afficher - paint.getTextSize() / 2 + DECALAGE_TEXTE_VERTICAL * coeff_taille / 2;
            if ((i > 0) && ((position_ds_ligne_courante) % 2 == 1)) {
                // --- --- ... qui relient les parents
                canvas.drawLine(
                        ligne_parents_coord_horizontale_point_gauche,
                        coord_verticale_en_dessous,
                        ligne_parents_coord_horizontale_point_droite,
                        coord_verticale_en_dessous,
                        paint);
                // --- --- ... qui relient les parents aux enfants

                if ((position_ds_ligne_courante - 1) / 2 < tableau_coord_horizontales_noms_old.size()) {
                    canvas.drawLine(
                            tableau_coord_horizontales_noms_old.get((position_ds_ligne_courante - 1) / 2),
                            coord_verticale_en_dessous,
                            ligne_parents_coord_horizontale_point_droite,
                            coord_verticale_en_dessous,
                            paint);
                }

            }
            // --- --- affichage des lignes verticales en dessous des noms
            canvas.drawLine(
                    coord_horizontale_milieu_nom,
                    coord_verticale_nom_a_afficher + paint.getTextSize() / 2,
                    coord_horizontale_milieu_nom,
                    coord_verticale_en_dessous,
                    paint);
            // --- --- calcul de la position horizontale courante compte tenu de ce qui vient d etre affiche
            longueur_courante = longueur_courante + paint.measureText(string_a_afficher) + (DECALAGE_TEXTE_HORIZONTAL + ((position_ds_ligne_courante) % 2) * DECALAGE_TEXTE_HORIZONTAL) * coeff_taille;
        }

        this.invalidate();
    }

    public void select(final ModelGenealogyPerson person) {
        tableau_ascendants = new ArrayList<>();
        ModelGenealogyPerson currentPerson = person;

        tableau_ascendants.add(currentPerson);
        if (currentPerson.father != null) {
            tableau_ascendants.add(currentPerson.father);
        } else {
            tableau_ascendants.add(new ModelGenealogyPerson(false));
        }
        if (currentPerson.mother != null) {
            tableau_ascendants.add(currentPerson.mother);
        } else {
            tableau_ascendants.add(new ModelGenealogyPerson(false));
        }

        int i = 0;
        while (i < tableau_ascendants.size() - 1) {
            currentPerson = tableau_ascendants.get(i + 1);

            if (currentPerson.isValid()) {
                if (currentPerson.father != null) {
                    tableau_ascendants.add(currentPerson.father);
                } else {
                    tableau_ascendants.add(new ModelGenealogyPerson(false));
                }
                if (currentPerson.mother != null) {
                    tableau_ascendants.add(currentPerson.mother);
                } else {
                    tableau_ascendants.add(new ModelGenealogyPerson(false));
                }
            }
            i++;
        }

        resetAscendants(tableau_ascendants);
    }
}

