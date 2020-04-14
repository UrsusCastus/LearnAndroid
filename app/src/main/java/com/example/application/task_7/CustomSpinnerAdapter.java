package com.example.application.task_7;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;

public class CustomSpinnerAdapter implements SpinnerAdapter {
    private static final int EXTRA = 1;
    private SpinnerAdapter mAdapter;
    private Context mContext;
    private int mNothingSelectedLayout;
    private int mNothingSelectedDropdownLayout;
    private LayoutInflater mLayoutInflater;

    public CustomSpinnerAdapter(
            SpinnerAdapter spinnerAdapter,
            int nothingSelectedLayout, Context context) {
        this(spinnerAdapter, nothingSelectedLayout, -1, context);
    }

    public CustomSpinnerAdapter(SpinnerAdapter spinnerAdapter, int nothingSelectedLayout,
                                int nothingSelectedDropdownLayout, Context context) {
        this.mAdapter = spinnerAdapter;
        this.mContext = context;
        this.mNothingSelectedLayout = nothingSelectedLayout;
        this.mNothingSelectedDropdownLayout = nothingSelectedDropdownLayout;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            return getNothingSelectedView(parent);
        }
        return mAdapter.getView(position - EXTRA, null, parent);
    }

    protected View getNothingSelectedView(ViewGroup parent) {
        return mLayoutInflater.inflate(mNothingSelectedLayout, parent, false);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            return mNothingSelectedDropdownLayout == -1 ?
                    new View(mContext) :
                    getNothingSelectedDropdownView(parent);
        }
        return mAdapter.getDropDownView(position - EXTRA, null, parent);
    }

    protected View getNothingSelectedDropdownView(ViewGroup parent) {
        return mLayoutInflater.inflate(mNothingSelectedDropdownLayout, parent, false);
    }

    @Override
    public int getCount() {
        int count = mAdapter.getCount();
        return count == 0 ? 0 : count + EXTRA;
    }

    @Override
    public Object getItem(int position) {
        return position == 0 ? null : mAdapter.getItem(position - EXTRA);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position >= EXTRA ? mAdapter.getItemId(position - EXTRA) : position - EXTRA;
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }
}
