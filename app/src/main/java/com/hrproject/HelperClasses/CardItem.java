package com.hrproject.HelperClasses;

public class CardItem {
    private int mTextResource;
    private int mTitleResource;
    private String mImageResource;

    public CardItem(int title, int text) {
        mTitleResource = title;
        mTextResource = text;
    }

    public CardItem(String image){
        mImageResource=image;
    }



    public int getText() {
        return mTextResource;
    }

    public int getTitle() {
        return mTitleResource;
    }

    public String getImage(){
        return mImageResource;

    }
}
