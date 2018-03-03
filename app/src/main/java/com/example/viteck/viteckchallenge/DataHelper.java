package com.example.viteck.viteckchallenge;


/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import android.content.Context;
        import android.widget.Filter;

        import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;

        import java.io.IOException;
        import java.io.InputStream;
        import java.lang.reflect.Type;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.List;

public class DataHelper {

    private static final String COLORS_FILE_NAME = "colors.json";

    private static List<SuggestionsWrapper> sColorWrappers = new ArrayList<>();

    private static List<HealthSuggestion> sColorSuggestions =
            new ArrayList<>(Arrays.asList(
                    new HealthSuggestion("Flu"),
                    new HealthSuggestion("Sick"),
                    new HealthSuggestion("Premium"),
                    new HealthSuggestion("Healthcare"),
                    new HealthSuggestion("Disease"),
                    new HealthSuggestion("Fever"),
                    new HealthSuggestion("Sore Throat"),
                    new HealthSuggestion("Headache"),
                    new HealthSuggestion("Upset Stomach")));

    public interface OnFindColorsListener {
        void onResults(List<SuggestionsWrapper> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<HealthSuggestion> results);
    }

    public static List<HealthSuggestion> getHistory(Context context, int count) {

        List<HealthSuggestion> suggestionList = new ArrayList<>();
        HealthSuggestion colorSuggestion;
        for (int i = 0; i < sColorSuggestions.size(); i++) {
            colorSuggestion = sColorSuggestions.get(i);
            colorSuggestion.setIsHistory(true);
            suggestionList.add(colorSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (HealthSuggestion healthSuggestion : sColorSuggestions) {
            healthSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DataHelper.resetSuggestionsHistory();
                List<HealthSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (HealthSuggestion suggestion : sColorSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<HealthSuggestion>() {
                    @Override
                    public int compare(HealthSuggestion lhs, HealthSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<HealthSuggestion>) results.values);
                }
            }
        }.filter(query);

    }


    public static void findColors(Context context, String query, final OnFindColorsListener listener) {
        initColorWrapperList(context);

        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {


                List<SuggestionsWrapper> suggestionList = new ArrayList<>();

                if (!(constraint == null || constraint.length() == 0)) {

                    for (SuggestionsWrapper color : sColorWrappers) {
                        if (color.getName().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(color);
                        }
                    }

                }

                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<SuggestionsWrapper>) results.values);
                }
            }
        }.filter(query);

    }

    private static void initColorWrapperList(Context context) {

        if (sColorSuggestions == null)
        {
             sColorWrappers = new ArrayList<>();

        }
        try
        {
            if (sColorWrappers.isEmpty()) {
                String jsonString = loadJson(context);
                sColorWrappers = deserializeColors(jsonString);
            }
        }
        catch (Exception e)
        {
            sColorWrappers = new ArrayList<>();
            String jsonString = loadJson(context);
            sColorWrappers = deserializeColors(jsonString);
        }

    }

    private static String loadJson(Context context) {

        String jsonString;

        try {
            InputStream is = context.getAssets().open(COLORS_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return jsonString;
    }

    private static List<SuggestionsWrapper> deserializeColors(String jsonString) {

        Gson gson = new Gson();

        Type collectionType = new TypeToken<List<SuggestionsWrapper>>() {
        }.getType();
        return gson.fromJson(jsonString, collectionType);
    }

}