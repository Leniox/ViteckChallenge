package com.example.viteck.viteckchallenge;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.*;

public class HealthSuggestion implements com.arlib.floatingsearchview.suggestions.model.SearchSuggestion {

    private String mSuggestionName;
    private boolean mIsHistory = false;

    public HealthSuggestion(String suggestion) {
        this.mSuggestionName = suggestion.toLowerCase();
    }

    public HealthSuggestion(Parcel source) {
        this.mSuggestionName = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    @Override
    public String getBody() {
        return mSuggestionName;
    }

    public static final Creator<HealthSuggestion> CREATOR = new Creator<HealthSuggestion>() {
        @Override
        public HealthSuggestion createFromParcel(Parcel in) {
            return new HealthSuggestion(in);
        }

        @Override
        public HealthSuggestion[] newArray(int size) {
            return new HealthSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSuggestionName);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}