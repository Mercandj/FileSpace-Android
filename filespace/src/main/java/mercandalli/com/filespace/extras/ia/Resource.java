/**
 * This file is part of Jarvis for Android, an app for managing your server (files, talks...).
 *
 * Copyright (c) 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 *
 * LICENSE:
 *
 * Jarvis for Android is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.
 *
 * Jarvis for Android is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * @author Jonathan Mercandalli
 * @license http://www.gnu.org/licenses/gpl.html
 * @copyright 2014-2015 Jarvis for Android contributors (http://mercandalli.com)
 */
package mercandalli.com.filespace.extras.ia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.extras.ia.language.Sentence;
import mercandalli.com.filespace.ui.activities.ApplicationActivity;
import mercandalli.com.filespace.utils.MathUtils;

import static mercandalli.com.filespace.utils.FileUtils.readStringAssets;

/**
 * Created by Jonathan on 24/04/2015.
 */
public class Resource {

    private ApplicationActivity app;
    protected List<QA> qas;
    protected List<Sentence> sentences;

    public Resource(ApplicationActivity app, String file_name) {
        this.app = app;
        this.qas = new ArrayList<>();
        this.sentences = new ArrayList<>();

        String text = readStringAssets(app, file_name);

        try {
            JSONObject json = new JSONObject(text);
            if(json.has("qa")) {
                JSONArray qa_array = json.getJSONArray("qa");
                int qa_array_length = qa_array.length();
                for(int i=0; i<qa_array_length; i++) {
                    JSONObject qa = qa_array.getJSONObject(i);
                    QA real_qa = new QA(app, qa);
                    if(real_qa.isValid())
                        qas.add(real_qa);
                }
            }

            if(json.has("sentences")) {
                JSONArray sentences_array = json.getJSONArray("sentences");
                int sentences_array_length = sentences_array.length();
                for(int i=0; i<sentences_array_length; i++) {
                    JSONObject sentence = sentences_array.getJSONObject(i);
                    Sentence real_sentence = new Sentence(app, sentence);
                    if(real_sentence.isValid())
                        sentences.add(real_sentence);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<QA> getQas() {
        return qas;
    }

    public String getSentence(String title) {
        String result = null;
        if(title == null)
            return result;
        for(Sentence sent : this.sentences)
            if(sent.getTitle().equals(title))
                return sent.getSentence().get(MathUtils.random(0, sent.getSentence().size() - 1));
        return null;
    }

    public boolean equalsSentenece(String title, String sentence) {
        if(title == null || sentence == null)
            return false;
        for(Sentence sent : this.sentences)
            if(sent.getTitle().equals(title))
                for(String str : sent.getSentence())
                    if(str.equals(sentence))
                        return true;
        return false;
    }

    public String startsWithSentenece(String title, String sentence) {
        String result = null;
        if(title == null || sentence == null)
            return result;
        for(Sentence sent : this.sentences)
            if(sent.getTitle().equals(title))
                for(String str : sent.getSentence())
                    if(sentence.startsWith(str)) {
                        if(result == null)
                            result = str;
                        else if(str.length() > result.length())
                            result = str;
                    }

        return result;
    }

    public String containsSentenece(String title, String sentence) {
        if(title == null || sentence == null)
            return null;
        for(Sentence sent : this.sentences)
            if(sent.getTitle().equals(title))
                for(String str : sent.getSentence())
                    if(str.contains(sentence))
                        return str;
        return null;
    }
}
