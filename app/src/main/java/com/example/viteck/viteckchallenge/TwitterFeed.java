package com.example.viteck.viteckchallenge;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.CategoriesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EmotionResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.EntitiesResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.KeywordsResult;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentResult;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import eu.amirs.JSON;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class TwitterFeed extends android.support.v4.app.Fragment implements OnChartValueSelectedListener, AsyncTwitterDelegate {

    View twitterLayout;
    FloatingSearchView mSearchView;
    ImageView mtwitterImg;
    private ColorDrawable mDimDrawable;
    boolean isBearerComputed = false;
    private static final long ANIM_DURATION = 350;
    String [] bearerToken = new String[1];
    String bearerTokenStr = "";
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;
    private String mLastQuery = "";
    DataRelay dataRelay;
    String token = "";
    HashMap<LatLng, AnalysisResults> analysisRegionHashMap = new HashMap<>();


    public TwitterFeed() {
        // Required empty public





    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        twitterLayout =  inflater.inflate(R.layout.fragment_twitter_feed, container, false);
        mSearchView = twitterLayout.findViewById(R.id.floating_search_view);
        mtwitterImg = twitterLayout.findViewById(R.id.twitterImg);
        mDimDrawable = new ColorDrawable(Color.BLACK);
        mDimDrawable.setAlpha(0);
        dataRelay = (DataRelay) getActivity().getApplicationContext();






        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {



                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {

                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    DataHelper.findSuggestions(getActivity(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<HealthSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary

                                    mSearchView.swapSuggestions( results);

                                    //let the users know that the background
                                    //process has completed
                                    mSearchView.hideProgress();
                                }
                            });
                }

            }


        });
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {

            }

        });
        mSearchView.setOnLeftMenuClickListener(
                new FloatingSearchView.OnLeftMenuClickListener() {
                    @Override
                    public void onMenuOpened() {

                    }

                    @Override
                    public void onMenuClosed() {

                    }});

        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {

                //here you can set some attributes for the suggestion's left icon and text. For example,
                //you can choose your favorite image-loading library for setting the left icon's image.


                HealthSuggestion colorSuggestion = (HealthSuggestion) item;

                String textColor = "#000000";
                String textLight = "#787878";

                if (colorSuggestion.getIsHistory()) {
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_history_black_24dp, null));

                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
                    leftIcon.setAlpha(.36f);
                } else {
                    leftIcon.setAlpha(0.0f);
                    leftIcon.setImageDrawable(null);
                }

                textView.setTextColor(Color.parseColor(textColor));
                String text = colorSuggestion.getBody()
                        .replaceFirst(mSearchView.getQuery(),
                                "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));
            }



        });


        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                GoogleProgressBar googleProgressBar =  twitterLayout.findViewById(R.id.google_progress_twitter);
                ImageView twitterImg =  twitterLayout.findViewById(R.id.twitterImg);
                fadeOutAndHideImage(twitterImg);
                googleProgressBar.setVisibility(View.VISIBLE);

                HealthSuggestion colorSuggestion = (HealthSuggestion) searchSuggestion;
                DataHelper.findColors(getActivity(), colorSuggestion.getBody(),
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(List<SuggestionsWrapper> results) {
                                //show search results
                            }

                        });
                Log.d(TAG, "onSuggestionClicked()");
                //call here
//                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                mLastQuery = searchSuggestion.getBody();
                try
                {
//                    Intent testing = new Intent(getActivity(), graphActivity.class);
//                    testing.putExtra("query", mLastQuery);
//                    getActivity().startActivity(testing);
                    @SuppressLint("StaticFieldLeak")
                    TwitterAsync twitterAsync = new TwitterAsync(getContext(), token, twitterLayout){
                        @Override
                        protected void onPostExecute(HashMap<LatLng, AnalysisResults> result)  {

                            super.onPostExecute(result);
                            GoogleProgressBar googleProgressBar =  rootView.findViewById(R.id.google_progress_twitter);
                            googleProgressBar.setVisibility(View.INVISIBLE);
                            ImageView twitterImg =  rootView.findViewById(R.id.twitterImg);
                            twitterImg.setVisibility(View.VISIBLE);
//                         FloatingSearchView floatingSearchView =  rootView.findViewById(R.id.floating_search_view);
//                            floatingSearchView.setVisibility(View.VISIBLE);
////                            fadeInAndShowImg(twitterImg);
//                            twitterImg.setVisibility(View.GONE);
//                            analysisRegionHashMap = result;
//                            displayData(analysisRegionHashMap);


                            //testing.putExtra("query", mLastQuery);
                            dataRelay.analysisResults = result;
                            Intent testing = new Intent(getActivity(), graphActivity.class);
                            getActivity().startActivity(testing);

                        }
                    };

                    twitterAsync.execute(mLastQuery);

//                            = new TwitterAsync(getContext(), token, twitterLayout).execute(mLastQuery);
//                    displayData(analysisRegionHashMap);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }




            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;
                Toast.makeText(getContext(), "searchAction", Toast.LENGTH_SHORT).show();

                DataHelper.findColors(getActivity(), query,
                        new DataHelper.OnFindColorsListener() {

                            @Override
                            public void onResults(List<SuggestionsWrapper> results) {
                                //show search results
                            }

                        });
                Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                fadeOutAndHideImage(mtwitterImg);

                int headerHeight = getResources().getDimensionPixelOffset(R.dimen.sliding_search_view_header_height);
                ObjectAnimator anim = ObjectAnimator.ofFloat(mSearchView, "translationY",
                        headerHeight, 0);
                anim.setDuration(350);
                fadeDimBackground(0, 150, null);
                anim.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //show suggestions when search bar gains focus (typically history suggestions)
                        mSearchView.swapSuggestions(DataHelper.getHistory(getActivity(), 3));

                    }
                });
                anim.start();

                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {
                int headerHeight = getResources().getDimensionPixelOffset(R.dimen.sliding_search_view_header_height);
                ObjectAnimator anim = ObjectAnimator.ofFloat(mSearchView, "translationY",
                        0, headerHeight);
                anim.setDuration(350);
                anim.start();

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());

                Log.d(TAG, "onFocusCleared()");
                //fadeInAndShowImg(mtwitterImg);

            }
        });



        return twitterLayout;
    }

    public void displayData(HashMap<LatLng, AnalysisResults> theData)
    {
        //Bar
        setUpPieChart();
//        for (LatLng someLat : theData.keySet())
//        {
//            AnalysisResults results = theData.get(someLat);
//            String looking = results.getAnalyzedText();
//            List<CategoriesResult> catLists = results.getCategories();
//            EmotionResult emotions = results.getEmotion();
//            List<EntitiesResult> entities = results.getEntities();
//            List<KeywordsResult> keywords =  results.getKeywords();
//            SentimentResult sentiment = results.getSentiment();
//
//
//
//        }
//
//        Gson gson = new Gson();
//        String json = gson.toJson(theData);
//
//
//
//
//        try {
//            JSONObject dataObject = new JSONObject(json);
//            System.out.println(dataObject);
//
//
//
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


    }

    public void setUpPieChart()
    {
        PieChart mChart = (PieChart) twitterLayout.findViewById(R.id.piechart1);
        mChart.setVisibility(View.VISIBLE);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);
        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setCenterText(generateCenterSpannableText());

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setOnChartValueSelectedListener(this);

        //ENTRIES BELOW


        float mult = 100;
        int count = 4;
        String[] mParties = new String[] {
                "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
                "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
                "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
                "Party Y", "Party Z"};
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        for (int i = 0; i < count ; i++) {
            entries.add(new PieEntry((float) ((Math.random() * mult) + mult / 5),
                    mParties[i % mParties.length],
                    getResources().getDrawable(R.drawable.icons8_rating_50)));
        }
        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);


        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        // undo all highlights
//        mChart.highlightValues(null);
        mChart.invalidate();


        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(12f);









    }
    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("PittHacks Graph. Made with hardwork.");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }




    private void fadeOutAndHideImage(final ImageView img)
    {
        if (img.getVisibility() == View.GONE)
        {
            return;
        }
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(300);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }

    private void fadeDimBackground(int from, int to, Animator.AnimatorListener listener) {
        ValueAnimator anim = ValueAnimator.ofInt(from, to);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int value = (Integer) animation.getAnimatedValue();
                mDimDrawable.setAlpha(value);
            }
        });
        if (listener != null) {
            anim.addListener(listener);
        }
        anim.setDuration(ANIM_DURATION);
        anim.start();
    }
    private void fadeInAndShowImg(final ImageView img)
    {

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(700);

        fadeIn.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.VISIBLE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeIn);
    }

    @Override
    public void processFinish(HashMap<LatLng, AnalysisResults> output) {
        analysisRegionHashMap = output;
                    displayData(output);

    }


    //TWITTER STUFFF ====================================



}
